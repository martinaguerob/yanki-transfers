package com.nttdata.yankitransfers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YankiAccount {

    private String id;
    private String typeDoc;//dni, ce
    private String numberDoc;
    private String numberCelphone;
    private String imei;
    private String email;
    private String typeAccount; //yanki purse, cuenta bancaria
    private String numberAccountPurse; //Guardará la cuenta de yankiPurse
    private String numberAccount; //Sea de banco o yankiPurse
    private Date createdAt;
    private Date updateAt;
    private Boolean status;
}
