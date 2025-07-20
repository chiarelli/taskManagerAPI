package com.github.chiarelli.taskmanager.application.events;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.chiarelli.taskmanager.application.shared.Event;
import com.github.chiarelli.taskmanager.domain.event.*;
import com.github.chiarelli.taskmanager.domain.model.Projeto;
import com.github.chiarelli.taskmanager.domain.model.ProjetoCriadoEvent;
import com.github.chiarelli.taskmanager.domain.model.iDefaultAggregate;

public class DomainEventAdapters {

  private DomainEventAdapters() {}

  public static Event adapt(AbstractDomainEvent<?> event) {
    return DomainEventMapper.adapt(event);
  }

  /** Eventos de domínio que serão adaptados **/

  public static class NovaTarefaCriadaEventAdapter extends NovaTarefaCriadaEvent implements Event {
    public NovaTarefaCriadaEventAdapter(iDefaultAggregate aggregate, Payload payload) {
      super((Projeto) aggregate, payload);
    }    
  }

  public static class ComentarioAdicionadoEventAdapter extends ComentarioAdicionadoEvent implements Event {
    public ComentarioAdicionadoEventAdapter(iDefaultAggregate aggregate,Payload payload) {
      super(aggregate, payload);
    }    
  }

  public static class ProjetoCriadoEventAdapter extends ProjetoCriadoEvent implements Event {
    public ProjetoCriadoEventAdapter(iDefaultAggregate projeto, Payload payload) {
      super((Projeto) projeto, payload);
    }
  }

  public static class ProjetoAlteradoEventAdapter extends ProjetoAlteradoEvent implements Event {
    public ProjetoAlteradoEventAdapter(iDefaultAggregate projeto, Payload payload) {
      super((Projeto) projeto, payload);
    }
  }

}

class DomainEventMapper {

  private static final Map<String, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

  public static Event adapt(AbstractDomainEvent<?> event) {
    try {
      String domainEventClassName = event.getClass().getSimpleName(); // Ex: ComentarioAdicionadoEvent
      String adapterClassName = DomainEventAdapters.class.getName() + "$" + domainEventClassName + "Adapter";

      Constructor<?> ctor = constructorCache.computeIfAbsent(adapterClassName, className -> {
        try {
          Class<?> adapterClass = Class.forName(className);
          return adapterClass.getConstructor(iDefaultAggregate.class, event.getPayload().getClass());
        } catch (Exception e) {
          throw new RuntimeException("Falha ao refletir classe adaptadora: " + className, e);
        }
      });

      return (Event) ctor.newInstance(event.getAggregate(), event.getPayload());

    } catch (Exception e) {
      throw new IllegalStateException("Erro ao adaptar evento de domínio: " + event.getClass(), e);
    }
  }

  private DomainEventMapper() {}
}

/*
Mapeador sem reflection

class EventsMapperInner {
   * 
   static final Map<Class<?>, Function<AbstractDomainEvent<?>, Event>> adapters = Map.of(
    
   ComentarioAdicionadoEvent.class, ev 
   -> new DomainEventsAdapter.ComentarioAdicionadoEventAdapter(ev.getAggregate(), ev.getPayload())
   
   );
  }
*/