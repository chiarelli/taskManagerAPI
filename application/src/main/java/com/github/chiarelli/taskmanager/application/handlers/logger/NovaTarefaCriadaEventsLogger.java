package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.NovaTarefaCriadaEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class NovaTarefaCriadaEventsLogger implements EventHandler<NovaTarefaCriadaEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(NovaTarefaCriadaEventsLogger.class);

  @Override
  public void handle(NovaTarefaCriadaEventAdapter event) {
    logger.info("Tarefa %s criada com sucesso".formatted(event.getAggregate().getIdAsString()));
  }
}
