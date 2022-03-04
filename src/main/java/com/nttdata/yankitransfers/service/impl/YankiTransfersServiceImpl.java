package com.nttdata.yankitransfers.service.impl;

import com.nttdata.yankitransfers.config.WebClientConfig;
import com.nttdata.yankitransfers.entity.YankiTransfers;
import com.nttdata.yankitransfers.model.BankAccount;
import com.nttdata.yankitransfers.model.MovementBankAccount;
import com.nttdata.yankitransfers.model.YankiAccount;
import com.nttdata.yankitransfers.model.YankiPurse;
import com.nttdata.yankitransfers.repository.YankiTransfersRepository;
import com.nttdata.yankitransfers.service.Calculate;
import com.nttdata.yankitransfers.service.YankiTransfersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class YankiTransfersServiceImpl implements YankiTransfersService {

    @Autowired
    YankiTransfersRepository yankiTransfersRepository;
    private final WebClientConfig webClientConfig = new WebClientConfig();

    @Override
    public Flux<YankiTransfers> findAll() {
        return yankiTransfersRepository.findAll();
    }

    @Override
    public Mono<YankiTransfers> save(YankiTransfers entity) {
        entity.setStatus(true);
        entity.setCreatedAt(new Date());
        entity.setComment(entity.getComment()==null ? " " : entity.getComment());

        Mono<YankiAccount> sourceYankiAccount = webClientConfig.getYankiAccountByNumberCelphone(entity.getSourceCelphone());
        return sourceYankiAccount
                .flatMap(s ->
                        s.getTypeAccount().equals("yanki purse")
                                ? yankiTransfer(entity, s.getNumberAccount())
                                : bankTransfer(entity, s.getNumberAccount())
                );

    }

    public Mono<YankiTransfers> yankiTransfer(YankiTransfers entity, String numberAccount){
        //Transferencia desde monedero
        System.out.println("Transferencia desde monedero");

        //Cuentas Yanki: origen y destino
        Mono<YankiAccount> sourceYankiAccount = webClientConfig.getYankiAccountByNumberCelphone(entity.getSourceCelphone());
        Mono<YankiAccount> destinationYankiAccount = webClientConfig.getYankiAccountByNumberCelphone(entity.getDestinationCelphone());

        //Formula de actualización de saldos
        Calculate sub = ((monto, saldo) -> saldo - monto);

        Mono<YankiPurse> yankiPurse = webClientConfig.getYankiPurseByNumberAccount(numberAccount);
        return yankiPurse
                .filter(yp -> yp.getBalance() >= entity.getAmount())
                .flatMap(y -> {
                    //Actualizar saldo de Yanki purse
                    Double balanceSource = sub.calcular(entity.getAmount(), y.getBalance());
                    y.setBalance(balanceSource);
                    webClientConfig.updateYankiPurse(y, y.getId());

                    //Actualizar en destino
                    destinationYankiAccount
                            .flatMap(d ->
                                    d.getTypeAccount().equals("yanki purse")
                                            ? destinationYankiTransfer(d.getNumberAccount(), entity.getAmount())
                                            : destinationBankTransfer(d.getNumberAccount(), entity.getAmount())
                            ).subscribe();

                    return yankiTransfersRepository.save(entity);
                });


    }
    public Mono<YankiTransfers> bankTransfer(YankiTransfers entity, String numberAccount){
        //Transderencia desde una cuenta bancaria

        System.out.println("Transferencia desde cuenta bancaria");
        //Cuentas Yanki: origen y destino
        Mono<YankiAccount> sourceYankiAccount = webClientConfig.getYankiAccountByNumberCelphone(entity.getSourceCelphone());
        Mono<YankiAccount> destinationYankiAccount = webClientConfig.getYankiAccountByNumberCelphone(entity.getDestinationCelphone());

        //Formula de actualización de saldos
        Calculate sub = ((monto, saldo) -> saldo - monto);

        Mono<BankAccount> bankAccount = webClientConfig.getAccountByNumberAccount(numberAccount);

        return bankAccount
                .filter(ba -> ba.getBalance() >= entity.getAmount())
                .flatMap(b -> {
                    //Actualizar saldo en banco
                    System.out.println("Ingresó a  ver la cuenta del banco");
                    Double balanceSource = sub.calcular(entity.getAmount(), b.getBalance());
                    b.setBalance(balanceSource);
                    webClientConfig.updateBankAccount(b, b.getId()).subscribe();

                    //Guardar movimiento
                    String description = "Transferencia Yanki";
                    this.saveMovementAccount(b.getNumberAccount(), entity.getAmount()*-1, description).subscribe();

                    //Actualizar en destino
                    destinationYankiAccount
                            .flatMap(d ->
                                            d.getTypeAccount().equals("yanki purse")
                                                    ? destinationYankiTransfer(d.getNumberAccount(), entity.getAmount())
                                                    : destinationBankTransfer(d.getNumberAccount(), entity.getAmount())
                                    ).subscribe();

                    return yankiTransfersRepository.save(entity);
                });
    }

    public Mono<BankAccount> destinationBankTransfer(String numberAccount, Double amount){
        //Formulas de actualización de saldos
        Calculate sum = ((monto, saldo) -> saldo + monto);

        Mono<BankAccount> bankAccount = webClientConfig.getAccountByNumberAccount(numberAccount);
        return bankAccount
                .flatMap(ba -> {
                    //Guardar movimiento
                    String description = "Transferencia desde Yanki";
                    this.saveMovementAccount(ba.getNumberAccount(), amount, description).subscribe();

                    //Actualizar cuenta bancaria
                    Double balanceDestination = sum.calcular(amount, ba.getBalance());
                    ba.setBalance(balanceDestination);
                    return webClientConfig.updateBankAccount(ba, ba.getId());
                });
    }

    public Mono<YankiPurse> destinationYankiTransfer(String numberAccount, Double amount){
        //Formulas de actualización de saldos
        Calculate sum = ((monto, saldo) -> saldo + monto);

        Mono<YankiPurse> yankiPurse = webClientConfig.getYankiPurseByNumberAccount(numberAccount);

        return yankiPurse
                .flatMap(yp -> {
                    //Actualizar monedero
                    Double balanceDestination = sum.calcular(amount, yp.getBalance());
                    yp.setBalance(balanceDestination);
                    return webClientConfig.updateYankiPurse(yp, yp.getId());
                });
    }

    public Mono<MovementBankAccount> saveMovementAccount(String numberAccount, Double amount, String description) {
        MovementBankAccount movementBankAccount = new MovementBankAccount();
        movementBankAccount.setAmount(amount);
        movementBankAccount.setDescription(description);
        movementBankAccount.setNumberAccount(numberAccount);
        movementBankAccount.setStatus(true);
        return  webClientConfig.saveMovementBankAccount(movementBankAccount);
    }
}
