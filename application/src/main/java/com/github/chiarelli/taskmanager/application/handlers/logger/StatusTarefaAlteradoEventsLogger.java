package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.StatusTarefaAlteradoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class StatusTarefaAlteradoEventsLogger implements EventHandler<StatusTarefaAlteradoEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(StatusTarefaAlteradoEventsLogger.class);

  @Override
  public void handle(StatusTarefaAlteradoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}