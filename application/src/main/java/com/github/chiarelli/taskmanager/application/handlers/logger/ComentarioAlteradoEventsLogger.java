package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ComentarioAlteradoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class ComentarioAlteradoEventsLogger implements EventHandler<ComentarioAlteradoEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(ComentarioAlteradoEventsLogger.class);

  @Override
  public void handle(ComentarioAlteradoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}