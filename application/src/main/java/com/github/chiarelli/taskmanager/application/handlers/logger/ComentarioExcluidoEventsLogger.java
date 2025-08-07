package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ComentarioExcluidoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class ComentarioExcluidoEventsLogger implements EventHandler<ComentarioExcluidoEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(ComentarioExcluidoEventsLogger.class);

  @Override
  public void handle(ComentarioExcluidoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}