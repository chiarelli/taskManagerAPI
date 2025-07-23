package com.github.chiarelli.taskmanager.application.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chiarelli.taskmanager.domain.event.DomainEventBufferImpl;
import com.github.chiarelli.taskmanager.domain.model.TarefaService;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.shared.iDomainEventBuffer;
import com.github.chiarelli.taskmanager.domain.shared.iTarefaService;

import io.github.jkratz55.mediator.core.Mediator;
import io.github.jkratz55.mediator.core.Registry;
import io.github.jkratz55.mediator.spring.SpringMediator;
import io.github.jkratz55.mediator.spring.SpringRegistry;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

	private final iTarefasRepository tarefaRepository;
  private final iProjetoRepository projetoRepository;

	@Bean
	Registry registry(ApplicationContext applicationContext) {
		return new SpringRegistry(applicationContext);
	}

	@Bean
	Mediator mediator(Registry registry) {
		return new SpringMediator(registry);
	}

	@Bean
	iDomainEventBuffer eventBuffer() {
		return new DomainEventBufferImpl();
	}

	@Bean
	iTarefaService tarefaService(iDomainEventBuffer eventBuffer) {
		return new TarefaService(tarefaRepository, projetoRepository, eventBuffer);
	}

}
