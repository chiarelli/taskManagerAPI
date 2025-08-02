package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.HistoricoAdicionadoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class HistoricoAdicionadoEventsLogger implements EventHandler<HistoricoAdicionadoEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(HistoricoAdicionadoEventsLogger.class);

  @Override
  public void handle(HistoricoAdicionadoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}