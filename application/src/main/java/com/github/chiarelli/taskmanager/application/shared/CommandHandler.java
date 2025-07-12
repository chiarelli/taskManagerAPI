package com.github.chiarelli.taskmanager.application.shared;

import io.github.jkratz55.mediator.core.RequestHandler;

public interface CommandHandler<C extends Command<R>, R> extends RequestHandler<C, R> {
  R handle(C command);
}
