package com.github.chiarelli.taskmanager.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import com.github.chiarelli.taskmanager.application.usecases.queries.BuscarTarefaPorIdQuery;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;

@ExtendWith(MockitoExtension.class)
public class BuscarTarefaPorIdUseCaseTest {

  @Mock
  private iProjetoRepository projetoRepository;

  @InjectMocks
  private BuscarTarefaPorIdUseCase useCase;

  private ProjetoId projetoId;
  private TarefaId tarefaId;

  @BeforeEach
  void setUp() {
    projetoId = new ProjetoId(UUID.randomUUID());
    tarefaId = new TarefaId(UUID.randomUUID());
  }

  @Test
  void deveRetornarTarefaDTO_quandoProjetoETarefaExistem() {
    // Arrange
    BuscarTarefaPorIdQuery query = new BuscarTarefaPorIdQuery(projetoId, tarefaId);

    Tarefa tarefa = new Tarefa(tarefaId, "Título de Teste", "Descrição de Teste", DataVencimentoVO.now(), 
        eStatusTarefaVO.EM_ANDAMENTO, ePrioridadeVO.BAIXA, Set.of(), Set.of());

    when(projetoRepository.existsById(projetoId)).thenReturn(true);
    when(projetoRepository.findTarefaByProjetoId(projetoId, tarefaId)).thenReturn(Optional.of(tarefa));

    // Act
    TarefaDTO dto = useCase.handle(query);

    // Assert
    assertNotNull(dto);
    assertEquals(tarefaId.getId(), dto.getId().getId());
    assertEquals("Título de Teste", dto.getTitulo());
    assertEquals("Descrição de Teste", dto.getDescricao());
    assertEquals(eStatusTarefaVO.EM_ANDAMENTO, dto.getStatus());
    assertEquals(ePrioridadeVO.BAIXA, dto.getPrioridade());
  }

  @Test
  void deveLancarNotFoundException_quandoProjetoNaoExiste() {
    // Arrange
    BuscarTarefaPorIdQuery query = new BuscarTarefaPorIdQuery(projetoId, tarefaId);

    when(projetoRepository.existsById(projetoId)).thenReturn(false);

    // Act & Assert
    NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.handle(query));
    assertTrue(ex.getMessage().contains("Projeto"));
  }

  @Test
  void deveLancarNotFoundException_quandoTarefaNaoExisteNoProjeto() {
    // Arrange
    BuscarTarefaPorIdQuery query = new BuscarTarefaPorIdQuery(projetoId, tarefaId);

    when(projetoRepository.existsById(projetoId)).thenReturn(true);
    when(projetoRepository.findTarefaByProjetoId(projetoId, tarefaId)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.handle(query));
    assertTrue(ex.getMessage().contains("Tarefa"));
  }

  @Test
  void deveLancarExcecaoQuandoProjetoETarefaIdsForemNulos() {
    BuscarTarefaPorIdQuery query = new BuscarTarefaPorIdQuery(null, null);

    assertThrows(DomainException.class, query::validate);
  }

}
