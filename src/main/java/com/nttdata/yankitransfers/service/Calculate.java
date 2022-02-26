package com.nttdata.yankitransfers.service;

@FunctionalInterface
public interface Calculate {

    Double calcular(Double monto, Double saldo);

}
