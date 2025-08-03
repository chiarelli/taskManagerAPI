package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.TarefaAdicionadaEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class TarefaAdicionadaEventsLogger implements EventHandler<TarefaAdicionadaEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(TarefaAdicionadaEventsLogger.class);

  @Override
  public void handle(TarefaAdicionadaEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}