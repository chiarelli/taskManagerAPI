package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ComentarioCriadoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class ComentarioCriadoEventsLogger implements EventHandler<ComentarioCriadoEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(ComentarioCriadoEventsLogger.class);

  @Override
  public void handle(ComentarioCriadoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}