package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.dto.CriarTarefa;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarTarefaCommand(
  @NotNull
  ProjetoId projetoId,

  @Size(min = 8, max = 100, message = "O título deve ter entre 8 e 100 caracteres")
  String titulo,

  @Size(max = 255, message = "A descricao nao pode ter mais de 255 caracteres")
  String descricao,
  
  @Valid
  @NotNull(message = "A data de vencimento deve ser informada")
  DataVencimentoVO vencimento,

  ePrioridadeVO prioridade,
  eStatusTarefaVO status

) implements Command<TarefaDTO> {

  public CriarTarefa toCriarTarefa() {
    return new CriarTarefa(projetoId, titulo, descricao, vencimento, 
        status, prioridade
    );
  }

  public void validate() {
    new GenericValidator<>(this).assertValid();
  }

}
