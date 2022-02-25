package com.nttdata.yankitransfers.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@Document(collection = "yanki-transfers")
public class YankiTransfers {

    @Id
    private String id;
    private String sourceCelphone;
    private String destinationCelphone;
    private Float amount;
    private String comment;
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date createdAt;
    private Boolean status;
}