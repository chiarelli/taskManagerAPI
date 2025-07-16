package com.github.chiarelli.taskmanager.domain.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.github.chiarelli.taskmanager.domain.model.BaseModel;
import com.github.chiarelli.taskmanager.domain.shared.iDomainEventBuffer;

/**
 * Implementação padrão do buffer de eventos de domínio.
 * <p>
 * Esta classe coleta e armazena eventos de domínio gerados por agregados durante a execução de um caso de uso.
 * O objetivo é permitir o envio posterior (geralmente ao final de uma transação ou unidade de trabalho),
 * evitando que eventos sejam propagados imediatamente ao serem gerados.
 *
 * <p>
 * O buffer é mutável e seu ciclo de vida normalmente acompanha a execução de um serviço de aplicação
 * ou transação de domínio.
 *
 * <p>
 * Uso típico:
 * <pre>{@code
 *   buffer.collectFrom(pedido, cliente);
 *   List<AbstractDomainEvent<?>> eventos = buffer.flushEvents();
 * }</pre>
 *
 * @see AbstractDomainEvent
 * @see BaseModel
 */
public class DomainEventBufferImpl implements iDomainEventBuffer {

  private List<AbstractDomainEvent<?>> events = new ArrayList<>();

  /**
   * Coleta eventos de domínio dos agregados informados e os adiciona ao buffer interno.
   * <p>
   * Os eventos são extraídos via {@code BaseModel.flushEvents()}, o qual limpa os eventos do agregado após a coleta.
   *
   * @param aggregates um ou mais agregados que estendem {@code BaseModel}
   * @throws NullPointerException se o array de agregados for {@code null}
   */
  @Override
  public void collectFrom(BaseModel... aggregates) {
    Objects.requireNonNull(aggregates);

    Arrays.stream(aggregates)
      .filter(Objects::nonNull)
      .map(BaseModel::flushEvents)
      .flatMap(Collection::stream)
      .forEach(events::add);
  }

  /**
   * Retorna uma cópia imutável dos eventos atualmente armazenados no buffer e limpa o buffer.
   * <p>
   * Após a execução, o buffer é esvaziado e limpo.
   *
   * @return lista imutável dos eventos coletados até o momento
   */
  @Override
  public List<AbstractDomainEvent<?>> flushEvents() {
    List<AbstractDomainEvent<?>> result = List.copyOf(events);
    events.clear();
    return result;
  }
}
