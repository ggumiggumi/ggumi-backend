package com.uplus.ggumi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GgumiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GgumiApplication.class, args);
	}

}
