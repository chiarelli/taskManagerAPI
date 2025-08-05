package com.github.chiarelli.taskmanager.application.usecases.commands;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.shared.Command;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.validation.GenericValidator;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlterarDadosTarefaCommand(

  @NotNull
  ProjetoId projetoId,

  @NotNull
  TarefaId tarefaId,

  @Size(min = 8, max = 100, message = "O título deve ter entre 8 e 100 caracteres")
  String titulo,

  @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
  String descricao,

  @Valid
  @NotNull
  DataVencimentoVO dataVencimento,

  @NotNull
  @Min(value = 0, message = "A versão é obrigatória")
  Long version

) implements Command<TarefaDTO> {

  public static TarefaDTO toTarefaDTO(Tarefa tarefa) {
    return TarefaDTO.fromIgnoringCollections(tarefa);
  }

  public void validate() {
     new GenericValidator<>(this).assertValid();
  }

}
