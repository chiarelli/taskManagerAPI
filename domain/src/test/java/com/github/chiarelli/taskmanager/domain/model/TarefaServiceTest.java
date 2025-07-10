package com.github.chiarelli.taskmanager.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.repository.iComentariosRepository;
import com.github.chiarelli.taskmanager.domain.repository.iHistoricosRepository;
import com.github.chiarelli.taskmanager.domain.repository.iTarefasRepository;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

@ExtendWith(MockitoExtension.class)
public class TarefaServiceTest {

  @Mock
  iTarefasRepository tarefaRepository;

  @Mock
  iComentariosRepository comentarioRepository;

  @Mock
  iHistoricosRepository historicoRepository;

  @InjectMocks
  TarefaService tarefaService;

  @Test
  void deveAlterarStatusComSucessoEChamarRepositorios() {
    var tarefa = mock(Tarefa.class);
    var randomUUID = UUID.randomUUID();
    var autorId = new AutorId(randomUUID.toString());

    var novoStatus = eStatusTarefaVO.CONCLUIDA;

    tarefaService.alterarStatusComHistorico(tarefa, novoStatus, autorId);

    verify(tarefa).alterarStatus(eq(novoStatus), any());
    verify(tarefaRepository).save(tarefa);
    verify(historicoRepository).save(any());
  }

  @Test
  void deveAlterarDescricaoComSucessoEChamarRepositorios() {
    var tarefa = mock(Tarefa.class);
    var autor = new AutorId(UUID.randomUUID().toString());
    var novaDescricao = "Nova descrição";

    tarefaService.alterarDescricaoComHistorico(tarefa, novaDescricao, autor);

    verify(tarefa).alterarDescricao(eq(novaDescricao), any());
    verify(tarefaRepository).save(tarefa);
    verify(historicoRepository).save(any());
  }

  @Test
  void deveAdicionarComentarioEChamarRepositorios() {
      var tarefa = mock(Tarefa.class);
      var comentario = new Comentario(
          new ComentarioId(),
          LocalDateTime.now(),
          "comentário teste",
          "Descrição teste",
          new AutorId(UUID.randomUUID().toString())
      );

      tarefaService.adicionarComentarioComHistorico(tarefa, comentario);

      verify(tarefa).adicionarComentario(eq(comentario.getId()), any());
      verify(comentarioRepository).save(eq(comentario));
      verify(historicoRepository).save(any());
  }

  @Test
  void deveExcluirTarefaComStatusPendenteEChamarRepositorios() {
    var tarefa = mock(Tarefa.class);
    var autor = new AutorId(UUID.randomUUID().toString());

    tarefaService.excluirTarefaComHistorico(tarefa, autor);

    verify(tarefa).excluirTarefa();
    verify(tarefaRepository).delete(tarefa);
    verify(historicoRepository).save(any());
  }

  @Test
  void devePropagarExcecaoAoExcluirTarefaComStatusInvalido() {
    var tarefa = mock(Tarefa.class);
    var autor = new AutorId(UUID.randomUUID().toString());

    doThrow(new DomainException("erro")).when(tarefa).excluirTarefa();

    assertThatThrownBy(() -> tarefaService.excluirTarefaComHistorico(tarefa, autor))
        .isInstanceOf(DomainException.class)
        .hasMessage("erro");

    verify(tarefaRepository, never()).delete(any());
    verify(historicoRepository, never()).save(any());
  }

}
