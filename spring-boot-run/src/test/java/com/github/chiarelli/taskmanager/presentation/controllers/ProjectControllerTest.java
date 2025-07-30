package com.github.chiarelli.taskmanager.presentation.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.chiarelli.taskmanager.infra.springdata.adapter.repository.MongoTestContainer;
import com.github.chiarelli.taskmanager.spring_boot_run.TaskManagerSpringBootRunApplication;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(classes = TaskManagerSpringBootRunApplication.class)
@AutoConfigureMockMvc
public class ProjectControllerTest extends MongoTestContainer {

  private static final String URL_BASE = "/api/v1/projects";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MongoTemplate mongoTemplate;

  @AfterEach
  void limparBanco() {
    mongoTemplate.getDb().drop();
  }

  @Test
  void criarProjeto_deveRetornar201ECriarProjetoComTituloEDescricao() throws Exception {
    // Arrange
    String payload = """
        {
          "titulo": "Projeto de Integração",
          "descricao": "Testando endpoint com MongoDB real"
        }
        """;

    // Act & Assert
    mockMvc.perform(post(URL_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.titulo").value("Projeto de Integração"))
        .andExpect(jsonPath("$.descricao").value("Testando endpoint com MongoDB real"));

    // Verifica no banco
    List<Document> projetos = mongoTemplate.getCollection("projetos").find().into(new ArrayList<>());

    assertThat(projetos).hasSize(1);
    assertThat(projetos.get(0).get("titulo")).isEqualTo("Projeto de Integração");
    assertThat(projetos.get(0).get("descricao")).isEqualTo("Testando endpoint com MongoDB real");
  }

  @Test
  void buscarProjetoPorId_deveRetornar200EProjetoCorrespondente() throws Exception {
    // Arrange
    String titulo = "Projeto teste";
    String descricao = "Descrição do projeto teste";

    // Criar projeto primeiro via POST
    MvcResult postResult = mockMvc.perform(post(URL_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "titulo": "%s",
              "descricao": "%s"
            }
            """.formatted(titulo, descricao)))
        .andExpect(status().isCreated())
        .andReturn();

    // Extrair ID do projeto criado (assumindo que vem como JSON)
    String jsonResponse = postResult.getResponse().getContentAsString();
    String projectId = JsonPath.read(jsonResponse, "$.id");

    // Act + Assert: Buscar projeto pelo ID
    mockMvc.perform(get(URL_BASE + "/{projectId}", projectId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(projectId))
        .andExpect(jsonPath("$.titulo").value(titulo))
        .andExpect(jsonPath("$.descricao").value(descricao));
  }

  @Test
  void atualizarProjeto_deveRetornar200EAtualizarDadosComControleDeVersao() throws Exception {
    // Arrange: criar um projeto inicial via POST
    String tituloOriginal = "Projeto Inicial";
    String descricaoOriginal = "Descrição Original";

    MvcResult postResult = mockMvc.perform(post(URL_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "titulo": "%s",
              "descricao": "%s"
            }
            """.formatted(tituloOriginal, descricaoOriginal)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = postResult.getResponse().getContentAsString();
    String projectId = JsonPath.read(responseBody, "$.id");
    int version = JsonPath.read(responseBody, "$.version");

    // Act: atualizar o projeto via PUT com novos dados
    String tituloAtualizado = "Projeto Atualizado";
    String descricaoAtualizada = "Descrição Atualizada";

    mockMvc.perform(put(URL_BASE + "/{projectId}", projectId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "titulo": "%s",
              "descricao": "%s",
              "version": %d
            }
            """.formatted(tituloAtualizado, descricaoAtualizada, version)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(projectId))
        .andExpect(jsonPath("$.titulo").value(tituloAtualizado))
        .andExpect(jsonPath("$.descricao").value(descricaoAtualizada))
        .andExpect(jsonPath("$.version").value(version + 1)); // deve incrementar
  }

  @Test
  void atualizarProjeto_idempotente_naoIncrementaVersion() throws Exception {
    // Arrange: criar projeto inicial
    var createResult = mockMvc.perform(post(URL_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"titulo":"Projeto", "descricao":"Descricao"}
            """))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = createResult.getResponse().getContentAsString();
    String projectId = JsonPath.read(responseBody, "$.id");
    int version = JsonPath.read(responseBody, "$.version");

    // Act & Assert: enviar PUT com mesmos dados e mesma versão
    mockMvc.perform(put(URL_BASE + "/{projectId}", projectId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"titulo":"Projeto", "descricao":"Descricao", "version": %d}
            """.formatted(version)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.version").value(version)); // NÃO incrementa
  }

  @Test
  void atualizarProjeto_versionInvalida_deveRetornar409() throws Exception {
    // Arrange: criar projeto inicial
    var createResult = mockMvc.perform(post(URL_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"titulo":"Projeto", "descricao":"Descricao"}
            """))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = createResult.getResponse().getContentAsString();
    String projectId = JsonPath.read(responseBody, "$.id");

    // Enviar PUT com versão inválida (ex: 999)
    mockMvc.perform(put(URL_BASE + "/{projectId}", projectId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"titulo":"Projeto Alterado", "descricao":"Descricao Alterada", "version": 999}
            """))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.erros.conflito").exists());
  }

  @Test
  void removerProjeto_deveRetornar204() throws Exception {
    // Arrange: cria um projeto
    var createResult = mockMvc.perform(post(URL_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"titulo":"Projeto para Remover", "descricao":"Descrição"}
            """))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = createResult.getResponse().getContentAsString();
    String projectId = JsonPath.read(responseBody, "$.id");

    // Act & Assert: envia DELETE e espera 204
    mockMvc.perform(delete(URL_BASE + "/{projectId}", projectId))
        .andExpect(status().isNoContent());

    // Opcional: confirma que não existe mais (404)
    mockMvc.perform(get(URL_BASE + "/{projectId}", projectId))
        .andExpect(status().isNotFound());
  }

  @Test
  void removerProjeto_idempotente_deveRetornar204MesmoSeJaRemovido() throws Exception {
    // Arrange: cria um projeto
    var createResult = mockMvc.perform(post(URL_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"titulo":"Projeto X", "descricao":"Descricao X"}
            """))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = createResult.getResponse().getContentAsString();
    String projectId = JsonPath.read(responseBody, "$.id");

    // Primeira remoção
    mockMvc.perform(delete(URL_BASE + "/{projectId}", projectId))
        .andExpect(status().isNoContent());

    // Segunda tentativa (idempotente)
    mockMvc.perform(delete(URL_BASE + "/{projectId}", projectId))
        .andExpect(status().isNoContent());
  }

}
