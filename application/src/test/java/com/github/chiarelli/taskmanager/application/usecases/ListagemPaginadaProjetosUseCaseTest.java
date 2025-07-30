package com.github.chiarelli.taskmanager.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.repository.IProjectReaderRepository;
import com.github.chiarelli.taskmanager.application.usecases.queries.ListagemPaginadaGenericQuery;
import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.exception.DomainException;

@ExtendWith(MockitoExtension.class)
public class ListagemPaginadaProjetosUseCaseTest {

  @Mock
  IProjectReaderRepository repository;

  @InjectMocks
  ListagemPaginadaProjetosUseCase useCase;

  @Test
  void deveListarProjetosComPaginacaoCorreta() {
    // Arrange
    var query = new ListagemPaginadaGenericQuery<ProjetoDTO>(2, 5);
    var pageable = PageRequest.of(1, 5); // page=2 → index=1

    var projetoDTO = new ProjetoDTO();
      projetoDTO.setId(new ProjetoId());
      projetoDTO.setTitulo("Projeto A");
      projetoDTO.setDescricao("Descrição A");
      projetoDTO.setVersion(0L);
    var projetos = List.of(projetoDTO);

    var page = new PageImpl<>(projetos, pageable, 1);

    when(repository.findAllPaginated(pageable)).thenReturn(page);

    // Act
    var result = useCase.handle(query);

    // Assert
    verify(repository).findAllPaginated(pageable);
    assert (result.getContent().size() == 1);
    assert (result.getContent().get(0).getTitulo().equals("Projeto A"));
  }

  @Test
  void deveLancarExcecao_aoReceberParametrosInvalidos() {
    // Arrange
    var query = new ListagemPaginadaGenericQuery<ProjetoDTO>(0, 5); // página inválida

    // Act & Assert
    var violations = assertThrows(DomainException.class, () -> useCase.handle(query)).getViolations();
    
    assertThat(violations).containsKey("page");
    assertThat(violations.get("page")).isEqualTo("deve ser maior que ou igual à 1");
    
    // repository nao deve ser chamado
    verify(repository, never()).findAllPaginated(any());
  }

}
