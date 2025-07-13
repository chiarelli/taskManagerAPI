package com.github.chiarelli.taskmanager.task_manager_spring_boot_run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.github.chiarelli.taskmanager")
public class TaskManagerSpringBootRunApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerSpringBootRunApplication.class, args);
	}

}
