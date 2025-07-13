package com.github.chiarelli.taskmanager.task_manager_spring_boot_run.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.infra.mock.TarefasRepositoryMock;

@Configuration
public class InfraConfig {

  @Bean
  iTarefasRepository tarefaService() {
    // TODO Excluir o mock
		return new TarefasRepositoryMock();
	}

}
