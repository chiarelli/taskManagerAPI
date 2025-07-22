package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.TarefaExcluidaEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class TarefaExcluidaEventsLogger implements EventHandler<TarefaExcluidaEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(TarefaExcluidaEventsLogger.class);

  @Override
  public void handle(TarefaExcluidaEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}