package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ProjetoExcluidoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class ProjetoExcluidoEventsLogger implements EventHandler<ProjetoExcluidoEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(ProjetoExcluidoEventsLogger.class);

  @Override
  public void handle(ProjetoExcluidoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}