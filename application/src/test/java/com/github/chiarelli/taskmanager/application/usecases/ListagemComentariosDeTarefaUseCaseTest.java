package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTO;
import com.github.chiarelli.taskmanager.application.dtos.ComentarioDTOWithAuthorId;
import com.github.chiarelli.taskmanager.application.exceptions.NotFoundException;
import com.github.chiarelli.taskmanager.application.repository.ITarefaReaderRepository;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemComentariosDeTarefaQuery;
import com.github.chiarelli.taskmanager.domain.entity.AutorId;
import com.github.chiarelli.taskmanager.domain.entity.ComentarioId;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;

@ExtendWith(MockitoExtension.class)
public class ListagemComentariosDeTarefaUseCaseTest {

  @Mock
  private ITarefaReaderRepository tarefaReaderRepository;

  @Mock
  private iProjetoRepository projetoRepository;

  @InjectMocks
  private ListagemComentariosDeTarefaUseCase useCase;

  @SuppressWarnings("null")
  @Test
  void deveListarComentariosOrdenadosPorDataOcorrenciaDesc() {
    // Arrange
    ProjetoId projetoId = new ProjetoId();
    TarefaId tarefaId = new TarefaId();

    ListagemComentariosDeTarefaQuery query = new ListagemComentariosDeTarefaQuery(projetoId, tarefaId, 1, 10);

    ComentarioId comentarioId = new ComentarioId();

    ComentarioDTOWithAuthorId comentario = new ComentarioDTOWithAuthorId(
        comentarioId,
        new Date(),
        "Título Comentário",
        "comentário teste",
        new AutorId("123"));

    when(projetoRepository.findTarefaByProjetoId(projetoId, tarefaId))
        .thenReturn(Optional.of(mock(Tarefa.class)));

    when(tarefaReaderRepository.findAllComentariosByTarefaId(eq(tarefaId), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(comentario)));

    // Act
    Page<ComentarioDTO> result = useCase.handle(query);

    // Assert
    assertThat(result).hasSize(1);
    
    ComentarioDTO dto = result.getContent().get(0);
    assertThat(dto.getId()).isEqualTo(comentarioId);
    assertThat(dto.getAutor().getId().getId()).isEqualTo("123");

    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(tarefaReaderRepository).findAllComentariosByTarefaId(eq(tarefaId), captor.capture());

    assertNotNull(captor.getValue().getSort().getOrderFor("dataOcorrencia"));
    assertThat(captor.getValue().getSort().getOrderFor("dataOcorrencia").getDirection())
        .isEqualTo(Sort.Direction.DESC);
    assertThat(captor.getValue().getPageNumber()).isEqualTo(0);
    assertThat(captor.getValue().getPageSize()).isEqualTo(10);
  }

  @Test
  void deveLancarNotFoundExceptionQuandoTarefaNaoExistir() {
    // Arrange
    ProjetoId projetoId = new ProjetoId(UUID.randomUUID());
    TarefaId tarefaId = new TarefaId(UUID.randomUUID());

    ListagemComentariosDeTarefaQuery query = new ListagemComentariosDeTarefaQuery(projetoId, tarefaId, 1, 10);

    when(projetoRepository.findTarefaByProjetoId(projetoId, tarefaId))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(NotFoundException.class, () -> useCase.handle(query));
  }

  @Test
  void deveLancarExcecaoQuandoQueryInvalida() {
    // Arrange
    ProjetoId projetoId = new ProjetoId(UUID.randomUUID());
    TarefaId tarefaId = new TarefaId(UUID.randomUUID());

    ListagemComentariosDeTarefaQuery query = new ListagemComentariosDeTarefaQuery(projetoId, tarefaId, 0, 0);

    Map<String, Object> violations = assertThrows(DomainException.class, () -> useCase.handle(query)).getViolations();

    assertThat(violations).hasSize(2);
    assertThat(violations).containsEntry("page", "deve ser maior que ou igual à 1");
    assertThat(violations).containsEntry("pageSize", "deve ser maior que ou igual à 1");
  }

  @Test
  void deveConverterParaPageableCorretamente() {
    ListagemComentariosDeTarefaQuery query = new ListagemComentariosDeTarefaQuery(
        new ProjetoId(),
        new TarefaId(),
        2,
        20
    );

    Pageable pageable = query.toPageable();

    assertEquals(1, pageable.getPageNumber()); // 2 - 1
    assertEquals(20, pageable.getPageSize());
  }
}
