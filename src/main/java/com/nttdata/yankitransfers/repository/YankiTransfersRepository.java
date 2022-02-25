package com.nttdata.yankitransfers.repository;

import com.nttdata.yankitransfers.entity.YankiTransfers;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface YankiTransfersRepository extends ReactiveMongoRepository<YankiTransfers, String> {
}
