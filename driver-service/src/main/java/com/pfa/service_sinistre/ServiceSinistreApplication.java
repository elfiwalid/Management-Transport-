package com.pfa.service_sinistre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class ServiceSinistreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceSinistreApplication.class, args);
	}

}
