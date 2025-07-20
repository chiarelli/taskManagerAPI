package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ComentarioAdicionadoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class ComentarioAdicionadoEventsLogger implements EventHandler<ComentarioAdicionadoEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(ComentarioAdicionadoEventsLogger.class);

  @Override
  public void handle(ComentarioAdicionadoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}