package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ProjetoAlteradoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
class ProjetoAlteradoEventsLogger implements EventHandler<ProjetoAlteradoEventAdapter> {

  private static final Logger logger = LoggerFactory.getLogger(ProjetoAlteradoEventsLogger.class);

  @Override
  public void handle(ProjetoAlteradoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }
}