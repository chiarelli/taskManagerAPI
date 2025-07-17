package com.github.chiarelli.taskmanager.application.handlers.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.events.DomainEventAdapters.ProjetoCriadoEventAdapter;
import com.github.chiarelli.taskmanager.application.shared.EventHandler;

@Component
public class ProjetoCriadoEventLogger implements EventHandler<ProjetoCriadoEventAdapter> {

 private static final Logger logger = LoggerFactory.getLogger(ProjetoCriadoEventLogger.class);

  @Override
  public void handle(ProjetoCriadoEventAdapter event) {
    logger.info("Mensagem %s mensagem".formatted(event.getAggregate().getIdAsString()));
  }

}
