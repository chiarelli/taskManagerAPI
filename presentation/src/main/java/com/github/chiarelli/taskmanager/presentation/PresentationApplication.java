package com.github.chiarelli.taskmanager.presentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.github.chiarelli.taskmanager")
public class PresentationApplication {

	public static void main(String[] args) {
		SpringApplication.run(PresentationApplication.class, args);
	}

}
