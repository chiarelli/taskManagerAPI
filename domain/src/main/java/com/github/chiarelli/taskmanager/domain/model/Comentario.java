package com.github.chiarelli.taskmanager.domain.model;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.dto.AlterarComentario;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.event.ComentarioAlteradoEvent;
import com.github.chiarelli.taskmanager.domain.event.ComentarioCriadoEvent;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Comentario extends BaseModel implements iDefaultAggregate {

  @EqualsAndHashCode.Include
  private ComentarioId id;
  
  private Date dataCriacao;
  private String titulo;
  private String descricao;

  private AutorId autor;
  private TarefaId tarefaId;

  private Long version = 0L;

  public Comentario(ComentarioId comentarioId, Date date, String titulo, String descricao, AutorId autorId,
      TarefaId tarefaId2) {
    this.id = comentarioId;
    this.dataCriacao = date;
    this.titulo = titulo;
    this.descricao = descricao;
    this.autor = autorId;
    this.tarefaId = tarefaId2;
  }

  public static Comentario criarNovoComentario(String titulo, String descricao,
      AutorId autorId, TarefaId tarefaId) {
    Comentario comentario = new Comentario(new ComentarioId(), new Date(), titulo, descricao, autorId, tarefaId);
    
    var payload = new ComentarioCriadoEvent.Payload(tarefaId, comentario.getId(), 
        comentario.getDataCriacao(), titulo, descricao, autorId);

    comentario.addEvent(new ComentarioCriadoEvent(comentario, payload));

    return comentario;
  }

  public void atualizarComentario(AlterarComentario data) {
    titulo = data.titulo();
    descricao = data.descricao();

    version++;

    var payload = new ComentarioAlteradoEvent.Payload(titulo, descricao);
    addEvent(new ComentarioAlteradoEvent(this, payload));
  }

  @Override
  public String getIdAsString() {
    return id.toString();
  }

}
