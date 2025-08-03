package com.github.chiarelli.taskmanager.infra.springdata.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.chiarelli.taskmanager.spring_boot_run.TaskManagerSpringBootRunApplication;

@SpringBootTest(classes = TaskManagerSpringBootRunApplication.class)
@Testcontainers
public abstract class MongoTestContainer {

  @Autowired
  protected MongoTemplate mongoTemplate;

  @Container
  static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.5");

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    String originalUri = mongo.getReplicaSetUrl();

    // Adiciona parâmetros ao final da URI
    String uriComTTL = originalUri + "&maxIdleTimeMS=500&maxLifeTimeMS=2000&maxPoolSize=20";

    registry.add("spring.data.mongodb.uri", () -> uriComTTL);
  }

}
