package com.nttdata.yankitransfers.controller;

import com.nttdata.yankitransfers.entity.YankiTransfers;
import com.nttdata.yankitransfers.service.YankiTransfersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/yanki-transfers")
public class YankiTransfersController {

    @Autowired
    YankiTransfersService yankiTransfersService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<YankiTransfers> getYankiTransfers(){
        System.out.println("Listar transferencias");
        return yankiTransfersService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<YankiTransfers> saveYankiAccount(@RequestBody YankiTransfers yankiTransfers){
        System.out.println("Guardar transferencia");
        return yankiTransfersService.save(yankiTransfers);
    }
}
