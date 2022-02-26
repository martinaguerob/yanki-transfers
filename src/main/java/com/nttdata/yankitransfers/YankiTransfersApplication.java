package com.nttdata.yankitransfers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class YankiTransfersApplication {

	public static void main(String[] args) {
		SpringApplication.run(YankiTransfersApplication.class, args);
	}

}
