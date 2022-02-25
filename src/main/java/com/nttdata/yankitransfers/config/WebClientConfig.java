package com.nttdata.yankitransfers.config;

import com.nttdata.yankitransfers.model.BankAccount;
import com.nttdata.yankitransfers.model.MovementBankAccount;
import com.nttdata.yankitransfers.model.YankiAccount;
import com.nttdata.yankitransfers.model.YankiPurse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientConfig {

    private final WebClient.Builder webClientBuilder = WebClient.builder();

    public Mono<YankiAccount> getYankiAccountByNumberCelphone(@PathVariable String numberCelphone){
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/yanki-account/number/"+numberCelphone)
                .retrieve()
                .bodyToMono(YankiAccount.class);
    }

    public Mono<YankiPurse> getYankiPurseByNumberAccount(@PathVariable String numberAccount){
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/purse/number/"+numberAccount)
                .retrieve()
                .bodyToMono(YankiPurse.class);
    }

    public Mono<YankiPurse> updateYankiPurse(YankiPurse yankiPurse, String id){
        System.out.println("Se llegó a updateYankiPurse");
        return webClientBuilder.build()
                .put()
                .uri("http://localhost:8080/purse/update/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(yankiPurse))
                .retrieve()
                .bodyToMono(YankiPurse.class);
    }

    public Mono<BankAccount> getAccountByNumberAccount(@PathVariable String numberAccount){
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/accounts/bank-account/number/"+numberAccount)
                .retrieve()
                .bodyToMono(BankAccount.class);
    }

    public Mono<BankAccount> updateBankAccount(BankAccount bankAccount, String id){
        System.out.println("Se llegó a updateBankAccount");
        return webClientBuilder.build()
                .put()
                .uri("http://localhost:8080/accounts/bank-account/update/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bankAccount))
                .retrieve()
                .bodyToMono(BankAccount.class);
    }

    public Mono<MovementBankAccount> saveMovementBankAccount(MovementBankAccount movementBankAccount){
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8080/movements/bank-account")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(movementBankAccount))
                .retrieve()
                .bodyToMono(MovementBankAccount.class);
    }
}
