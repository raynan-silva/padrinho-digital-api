package com.dnnr.padrinho_digital_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PadrinhoDigitalApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PadrinhoDigitalApiApplication.class, args);
	}

}
