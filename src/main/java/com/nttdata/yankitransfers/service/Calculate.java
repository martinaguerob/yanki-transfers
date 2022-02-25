package com.nttdata.yankitransfers.service;

@FunctionalInterface
public interface Calculate {

    Float calcular(Float monto, Float saldo);

}
