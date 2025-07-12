package com.github.chiarelli.taskmanager.application.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.jkratz55.mediator.core.Mediator;
import io.github.jkratz55.mediator.core.Registry;
import io.github.jkratz55.mediator.spring.SpringMediator;
import io.github.jkratz55.mediator.spring.SpringRegistry;

@Configuration
public class AppConfig {

	@Bean
	Registry registry(ApplicationContext applicationContext) {
		return new SpringRegistry(applicationContext);
	}

	@Bean
	Mediator mediator(Registry registry) {
		return new SpringMediator(registry);
	}

}
