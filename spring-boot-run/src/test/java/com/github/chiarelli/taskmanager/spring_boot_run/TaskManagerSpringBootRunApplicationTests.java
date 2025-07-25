package com.github.chiarelli.taskmanager.spring_boot_run;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.github.chiarelli.taskmanager.infra.springdata.adapter.repository.ProjetoMongoRepository;

// @SpringBootTest
// @Import(ProjetoMongoRepository.class)
class TaskManagerSpringBootRunApplicationTests {

	@Autowired ProjetoMongoRepository projetoRepo;

	// @Test
	void contextLoads() {
		System.out.println("################");
		System.out.println(projetoRepo.toString());
	}

}
