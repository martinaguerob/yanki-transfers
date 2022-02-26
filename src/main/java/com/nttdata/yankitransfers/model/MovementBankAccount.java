package com.nttdata.yankitransfers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementBankAccount {

    private String id;
    private String description;
    private Double amount;
    private Date date;
    private Boolean status;
    private String numberAccount;

}
