package com.github.chiarelli.taskmanager.domain.vo;

import java.util.Date;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TarefaConcluidaPorVO {
  private final AutorId autorId;
  private final Date dataConclusao;
}
