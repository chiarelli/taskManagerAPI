package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.TarefaAlteradaEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class TarefaAlteradaEventsLogger implements EventHandler<TarefaAlteradaEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(TarefaAlteradaEventsLogger.class);

  @Override
  public void handle(TarefaAlteradaEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}