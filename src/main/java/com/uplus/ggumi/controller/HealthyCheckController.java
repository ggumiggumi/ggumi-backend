package com.uplus.ggumi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthyCheckController {

	@GetMapping("/")
	public String healthyCheck() {
		return "healthy";
	}

}
