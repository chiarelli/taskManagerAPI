package com.github.chiarelli.taskmanager.domain.repository;

import java.util.Optional;

import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.model.Comentario;

public interface iComentariosRepository {

  Optional<Comentario> findById(ComentarioId id);

  void save(Comentario comentario);
  
}
