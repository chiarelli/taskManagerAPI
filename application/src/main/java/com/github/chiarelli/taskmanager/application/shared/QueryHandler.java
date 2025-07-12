package com.github.chiarelli.taskmanager.application.shared;

import io.github.jkratz55.mediator.core.RequestHandler;

public interface QueryHandler<Q extends Query<R>, R> extends RequestHandler<Q, R> {
  R handle(Q query);
}
