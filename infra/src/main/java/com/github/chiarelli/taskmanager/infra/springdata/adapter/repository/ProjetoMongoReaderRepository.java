package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;
import com.github.chiarelli.taskmanager.application.repository.IProjectReaderRepository;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.entity.ProjetoDocument;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.mapper.ProjetoMapper;
import com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository.ProjetoSpringDataMongoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjetoMongoReaderRepository implements IProjectReaderRepository {

  private final ProjetoSpringDataMongoRepository mongoRepository;

  @Override
  public Page<ProjetoDTO> findAllPaginated(Pageable pageable) {
    Page<ProjetoDocument> result = mongoRepository.findAll(pageable);

    return result.map(ProjetoMapper::toDTO);
  }

}
