package com.github.chiarelli.taskmanager.spring_boot_run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.github.chiarelli.taskmanager")
// @EnableMongoRepositories(basePackages = "com.github.chiarelli.taskmanager.infra.springdata.mongodb.repository")
public class TaskManagerSpringBootRunApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerSpringBootRunApplication.class, args);
	}

}
