package com.pfa.service_admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class ServiceAdminApplication {

	public static void main(String[] args) {

		SpringApplication.run(ServiceAdminApplication.class, args);
	}

}
