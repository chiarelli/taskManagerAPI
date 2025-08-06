package com.github.chiarelli.taskmanager.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chiarelli.taskmanager.application.dtos.TarefaDTO;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.repository.ITarefaReaderRepository;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemTarefasDoProjetoQuery;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

@ExtendWith(MockitoExtension.class)
public class ListagemTarefasDoProjetoUseCaseTest {

  @Mock
  private iProjetoRepository projetoRepository;

  @Mock
  private ITarefaReaderRepository tarefaReaderRepository;

  @InjectMocks
  private ListagemTarefasDoProjetoUseCase useCase;

  private ProjetoId projetoId;

  @BeforeEach
  void setUp() {
    projetoId = new ProjetoId(UUID.randomUUID());
  }
  
  @Test
  void deveRetornarListaDeTarefaDTO_quandoProjetoExiste() {
    // Arrange
    Tarefa tarefa1 = new Tarefa(new TarefaId(UUID.randomUUID()), "Tarefa 1", "Desc 1", DataVencimentoVO.now(),
        eStatusTarefaVO.PENDENTE, ePrioridadeVO.ALTA, Set.of(), Set.of());

    Tarefa tarefa2 = new Tarefa(new TarefaId(UUID.randomUUID()), "Tarefa 2", "Desc 2", DataVencimentoVO.now(),
        eStatusTarefaVO.CONCLUIDA, ePrioridadeVO.BAIXA, Set.of(), Set.of());

    ListagemTarefasDoProjetoQuery query = new ListagemTarefasDoProjetoQuery(projetoId);

    when(projetoRepository.existsById(projetoId)).thenReturn(true);
    when(projetoRepository.findAllTarefasByProjetoId(projetoId)).thenReturn(List.of(tarefa1, tarefa2));

    // Act
    List<TarefaDTO> tarefas = useCase.handle(query);

    // Assert
    assertEquals(2, tarefas.size());

    assertEquals("Tarefa 1", tarefas.get(0).getTitulo());
    assertEquals("Tarefa 2", tarefas.get(1).getTitulo());
  }

  @Test
  void deveLancarNotFoundException_quandoProjetoNaoExiste() {
    // Arrange
    ListagemTarefasDoProjetoQuery query = new ListagemTarefasDoProjetoQuery(projetoId);

    when(projetoRepository.existsById(projetoId)).thenReturn(false);

    // Act & Assert
    NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.handle(query));
    assertTrue(ex.getMessage().contains("Projeto"));
  }

  @Test
  void deveLancarDomainException_quandoProjetoIdForNulo() {
    // Arrange
    ListagemTarefasDoProjetoQuery query = new ListagemTarefasDoProjetoQuery(null);

    // Act & Assert
    assertThrows(DomainException.class, query::validate);
  }
  
  @Test
  void deveRetornarListaVazia_quandoProjetoNaoTemTarefas() {
    ListagemTarefasDoProjetoQuery query = new ListagemTarefasDoProjetoQuery(projetoId);

    when(projetoRepository.existsById(projetoId)).thenReturn(true);
    when(projetoRepository.findAllTarefasByProjetoId(projetoId)).thenReturn(List.of());

    List<TarefaDTO> tarefas = useCase.handle(query);

    assertTrue(tarefas.isEmpty());
  }

}
