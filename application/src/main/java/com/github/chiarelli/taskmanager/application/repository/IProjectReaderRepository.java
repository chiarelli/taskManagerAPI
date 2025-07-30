package com.github.chiarelli.taskmanager.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.chiarelli.taskmanager.application.dtos.ProjetoDTO;

public interface IProjectReaderRepository {

  Page<ProjetoDTO> findAllPaginated(Pageable pageable);

}
