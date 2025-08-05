package com.github.chiarelli.taskmanager.presentation.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.github.chiarelli.taskmanager.domain.entity.ProjetoId;
import com.github.chiarelli.taskmanager.domain.entity.TarefaId;
import com.github.chiarelli.taskmanager.domain.model.Tarefa;
import com.github.chiarelli.taskmanager.domain.repository.iProjetoRepository;
import com.github.chiarelli.taskmanager.domain.vo.DataVencimentoVO;
import com.github.chiarelli.taskmanager.domain.vo.ePrioridadeVO;
import com.github.chiarelli.taskmanager.domain.vo.eStatusTarefaVO;
import com.github.chiarelli.taskmanager.infra.springdata.adapter.repository.MongoTestContainer;


@AutoConfigureMockMvc
public class TaskControllerTest extends MongoTestContainer {

  private static final String URL_BASE = "/api/v1/projects";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private iProjetoRepository projetoRepository;

  private ProjetoId projetoId;

  @AfterEach
  void limparBanco() {
    mongoTemplate.getDb().drop();
  }

  @BeforeEach
  void criarProjeto() throws Exception {
    var payload = """
        {
          "titulo": "Projeto de Integração",
          "descricao": "Testando endpoint com MongoDB real"
        }
        """;
    MvcResult result = mockMvc.perform(post(URL_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andReturn();

    String responseContent = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(responseContent);
    
    String uuid = jsonNode.get("id").asText();

    assertNotNull(uuid);

    projetoId = new ProjetoId(UUID.fromString(uuid));
  }
  
  private TarefaId criarTarefa() throws Exception {
    var dataVencimento = new Date().toInstant().atOffset(ZoneOffset.UTC);
    var payload = """
        {
          "titulo": "Título da tarefa",
          "descricao": "Descrição da tarefa",
          "status": "PENDENTE",
          "prioridade": "BAIXA",
          "data_vencimento": "%s"
        }
        """.formatted(dataVencimento.toString());

    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isCreated())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    String tarefaUuid = jsonNode.get("id").asText();

    assertNotNull(tarefaUuid);

    return new TarefaId(UUID.fromString(tarefaUuid));
  }

  @Test
  void deveCriarTarefaComSucesso() throws Exception {
    var dataVencimento = new Date().toInstant().atOffset(ZoneOffset.UTC);
    // Arrange
    var payload = """
        {
          "titulo": "Título da tarefa",
          "descricao": "Descrição da tarefa",
          "status": "PENDENTE",
          "prioridade": "BAIXA",
          "data_vencimento": "%s"
        }
        """.formatted(dataVencimento.toString());

    // Act
    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.titulo").value("Título da tarefa"))
        .andExpect(jsonPath("$.descricao").value("Descrição da tarefa"))
        .andExpect(jsonPath("$.status").value("PENDENTE"))
        .andExpect(jsonPath("$.prioridade").value("BAIXA"))
        .andExpect(jsonPath("$.data_vencimento").value(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dataVencimento)))
        .andExpect(jsonPath("$.comentarios_qt").value(0))
        .andReturn();

    // Assert
    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    String tarefaUuid = jsonNode.get("id").asText();

    // Verificar se persistiu no banco
    Optional<Tarefa> tarefa = projetoRepository.findTarefaByProjetoId(projetoId, new TarefaId(UUID.fromString(tarefaUuid)));
    
    assertTrue(tarefa.isPresent());

    var dtVenc = DataVencimentoVO.to(tarefa.get().getDataVencimento());

    assertEquals("Título da tarefa", tarefa.get().getTitulo());
    assertEquals("Descrição da tarefa", tarefa.get().getDescricao());
    assertEquals(eStatusTarefaVO.PENDENTE, tarefa.get().getStatus());
    assertEquals(ePrioridadeVO.BAIXA, tarefa.get().getPrioridade());
    assertEquals(dataVencimento.toString(), dtVenc.toString());
    assertEquals(0, tarefa.get().getComentarios().size());
  }

  @Test
  void deveRetornarErroAoEnviarTituloNuloOuVazio() throws Exception {
    var payload = """
        {
          "titulo": "",
          "descricao": "Descrição válida",
          "status": "PENDENTE",
          "prioridade": "MEDIA",
          "data_vencimento": "%s"
        }
        """.formatted(OffsetDateTime.now().toString());

    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.titulo").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    assertEquals("O título deve ter entre 8 e 100 caracteres", jsonNode.get("erros").get("titulo").asText());
  }

  // @Test
  void deveRetornarErroAoEnviarDescricaoNula() throws Exception {
    var payload = """
        {
          "titulo": "Título válido",
          "descricao": null,
          "status": "PENDENTE",
          "prioridade": "MEDIA",
          "data_vencimento": "%s"
        }
        """.formatted(OffsetDateTime.now().toString());

    mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deveRetornarErroAoEnviarStatusNulo() throws Exception {
    var payload = """
        {
          "titulo": "Título válido",
          "descricao": "Descrição válida",
          "status": null,
          "prioridade": "MEDIA",
          "data_vencimento": "%s"
        }
        """.formatted(OffsetDateTime.now().toString());

    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.status").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    assertEquals("O status da tarefa deve ser informado", jsonNode.get("erros").get("status").asText());
  }

  @Test
  void deveRetornarErroAoEnviarStatusInvalido() throws Exception {
    var payload = """
        {
          "titulo": "Título válido",
          "descricao": "Descrição válida",
          "status": "INVALIDO",
          "prioridade": "BAIXA",
          "data_vencimento": "%s"
        }
        """.formatted(OffsetDateTime.now().toString());

    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.status").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);

    assertEquals("Valor inválido. Use um dos valores permitidos: [PENDENTE, EM_ANDAMENTO, CONCLUIDA]", jsonNode.get("erros").get("status").asText());
  }

  @Test
  void deveRetornarErroAoEnviarPrioridadeInvalida() throws Exception {
    var payload = """
        {
          "titulo": "Título válido",
          "descricao": "Descrição válida",
          "status": "PENDENTE",
          "prioridade": "URGENTE",
          "data_vencimento": "%s"
        }
        """.formatted(OffsetDateTime.now().toString());

    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.prioridade").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    assertEquals("Valor inválido. Use um dos valores permitidos: [ALTA, MEDIA, BAIXA]", jsonNode.get("erros").get("prioridade").asText());  
  }

  @Test
  void deveRetornarErroAoOmitirDataVencimento() throws Exception {
    var payload = """
        {
          "titulo": "Título válido",
          "descricao": "Descrição válida",
          "status": "PENDENTE",
          "prioridade": "BAIXA"
        }
        """;

    var result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.dataVencimento").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    assertEquals("A data de vencimento deve ser informada", jsonNode.get("erros").get("dataVencimento").asText());
  }

  @Test
  void deveRetornarErroAoEnviarJsonMalFormatado() throws Exception {
    var payload = """
        {
          "titulo": "Título válido",
          "descricao": "Descrição válida",
          "status": "PENDENTE"
          "prioridade": "BAIXA", // vírgula faltando antes
          "data_vencimento": "%s"
        }
        """.formatted(OffsetDateTime.now().toString());

    var result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.invalid_json").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    assertEquals("JSON inválido", jsonNode.get("erros").get("invalid_json").asText());
  }

  @Test
  void deveRetornarErroAoEnviarTituloComComprimentoInvalido() throws Exception {
    var payload = """
        {
          "titulo": "A",
          "descricao": "Descrição válida",
          "status": "PENDENTE",
          "prioridade": "MEDIA",
          "data_vencimento": "%s"
        }
        """.formatted(OffsetDateTime.now().toString());

    var result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.titulo").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    assertEquals("O título deve ter entre 8 e 100 caracteres", jsonNode.get("erros").get("titulo").asText());

  }

  @Test
  void deveRetornarErroAoEnviarTituloComMaisDe100Caracteres() throws Exception {
    String tituloLongo = "T".repeat(101); // 101 caracteres

    var payload = """
        {
          "titulo": "%s",
          "descricao": "Descrição válida",
          "status": "PENDENTE",
          "prioridade": "MEDIA",
          "data_vencimento": "%s"
        }
        """.formatted(tituloLongo, OffsetDateTime.now().toString());

    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.titulo").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);

    assertEquals("O título deve ter entre 8 e 100 caracteres", jsonNode.get("erros").get("titulo").asText());
  }

  @Test
  void deveRetornarErroAoEnviarDescricaoComMaisDe500Caracteres() throws Exception {
    String descricaoLonga = "D".repeat(256); // 256 caracteres

    var payload = """
        {
          "titulo": "Título válido",
          "descricao": "%s",
          "status": "PENDENTE",
          "prioridade": "MEDIA",
          "data_vencimento": "%s"
        }
        """.formatted(descricaoLonga, OffsetDateTime.now().toString());

    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.descricao").exists())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);

    assertEquals("A descrição não pode ter mais de 255 caracteres", jsonNode.get("erros").get("descricao").asText());
  }

  @Test
  void deveRetornarTarefaPorIdComSucesso() throws Exception {
    // Arrange
    // Criar tarefa e adicionar ao projeto
    var dataVencimento = new Date().toInstant().atOffset(ZoneOffset.UTC);
    var payload = """
        {
          "titulo": "Título da tarefa",
          "descricao": "Descrição da tarefa",
          "status": "PENDENTE",
          "prioridade": "BAIXA",
          "data_vencimento": "%s"
        }
        """.formatted(dataVencimento.toString());

    var result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isCreated())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    // Pegar id da tarefa
    String tarefaUuid = jsonNode.get("id").asText();
        
    // Act
    mockMvc.perform(get("%s/%s/tasks/%s".formatted(URL_BASE, projetoId, tarefaUuid)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(tarefaUuid))
        .andExpect(jsonPath("$.titulo").value("Título da tarefa"))
        .andExpect(jsonPath("$.descricao").value("Descrição da tarefa"))
        .andExpect(jsonPath("$.status").value("PENDENTE"))
        .andExpect(jsonPath("$.prioridade").value("BAIXA"))
        .andExpect(jsonPath("$.data_vencimento").value(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dataVencimento)))
        .andExpect(jsonPath("$.comentarios_qt").value(0));
  }

  @Test
  void deveRetornarNotFoundQuandoTarefaNaoExistir() throws Exception {
    var tarefaId   = new TarefaId();

    mockMvc.perform(get("%s/%s/tasks/%s".formatted(URL_BASE, projetoId, tarefaId)))
        .andExpect(status().isNotFound())
        // .andDo(print())
        .andExpect(jsonPath("$.erros.not_found").exists())
        .andExpect(jsonPath("$.erros.not_found").value("Tarefa %s nao encontrada no projeto %s".formatted(tarefaId, projetoId)));
  }

  @Test
  void deveRetornarNotFoundQuandoProjetoNaoExistir() throws Exception {
    // Criar tarefa e adicionar ao projeto
    var dataVencimento = new Date().toInstant().atOffset(ZoneOffset.UTC);
    var payload = """
        {
          "titulo": "Título da tarefa",
          "descricao": "Descrição da tarefa",
          "status": "PENDENTE",
          "prioridade": "BAIXA",
          "data_vencimento": "%s"
        }
        """.formatted(dataVencimento.toString());

    var result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isCreated())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);

    // Pegar id da tarefa
    String tarefaUuid = jsonNode.get("id").asText();

    var projetoIdNaoExistente = new ProjetoId();

    mockMvc.perform(get("%s/%s/tasks/%s".formatted(URL_BASE, projetoIdNaoExistente, tarefaUuid)))
        .andExpect(status().isNotFound())
        // .andDo(print())
        .andExpect(jsonPath("$.erros.not_found").exists())
        .andExpect(jsonPath("$.erros.not_found").value("Projeto %s nao encontrado".formatted(projetoIdNaoExistente)));
  }

  @Test
  void deveRetornarBadRequestQuandoUuidForInvalido() throws Exception {
    String uuidInvalido = "abc";

    mockMvc.perform(get("%s/%s/tasks/%s".formatted(URL_BASE, uuidInvalido, uuidInvalido)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void deveAtualizarTarefaComSucesso() throws Exception {
    TarefaId tarefaId = criarTarefa();
    OffsetDateTime novaDataVencimento = new Date().toInstant()
                                            .atOffset(ZoneOffset.UTC)
                                            .plusDays(5);

    String dataVencimentoFormatada = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(novaDataVencimento);
    
    var payload = """
        {
          "titulo": "Novo título",
          "descricao": "Nova descrição",
          "data_vencimento": "%s",
          "version": 1
        }
        """.formatted(dataVencimentoFormatada);

    mockMvc.perform(put("/api/v1/projects/{projectId}/tasks/{taskId}", projetoId, tarefaId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(tarefaId.toString()))
        .andExpect(jsonPath("$.titulo").value("Novo título"))
        .andExpect(jsonPath("$.descricao").value("Nova descrição"))
        .andExpect(jsonPath("$.status").value("PENDENTE"))
        .andExpect(jsonPath("$.comentarios_qt").isNumber())
        .andExpect(jsonPath("$.data_vencimento").value(dataVencimentoFormatada));
  }

  @Test
  void deveRetornarNotFoundQuandoTarefaNaoExistirAtualizacao() throws Exception {
    var tarefaId   = new TarefaId();

    var payload = """
        {
          "titulo": "Título válido",
          "descricao": "Descrição válida",
          "status": "PENDENTE",
          "prioridade": "MEDIA",
          "data_vencimento": "%s",
          "version": 1
        }
        """.formatted(OffsetDateTime.now().plusDays(1));

    mockMvc.perform(put("/api/v1/projects/{projectId}/tasks/{taskId}", projetoId, tarefaId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.error").value("Tarefa %s não pertence ao projeto %s".formatted(tarefaId, projetoId)));
  }

  @Test
  void deveRetornarErroQuandoDadosObrigatoriosNaoForemInformados() throws Exception {
    TarefaId tarefaId = criarTarefa();

    var payload = """
        {
          "titulo": "",
          "descricao": "",
          "data_vencimento": null,
          "version": null
        }
        """;
    // Body = {"erros":{"prioridade":"A prioridade da tarefa deve ser informada","dataVencimento":"A data de vencimento deve ser informada","titulo":"O título deve ter entre 8 e 100 caracteres"}}
    mockMvc.perform(put("/api/v1/projects/{projectId}/tasks/{taskId}", projetoId, tarefaId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.titulo").value("O título deve ter entre 8 e 100 caracteres"))
        .andExpect(jsonPath("$.erros.dataVencimento").value("A data de vencimento deve ser informada"));
  }

  @Test
  void deveRetornarErroQuandoVersionForNula() throws Exception {
    TarefaId tarefaId = criarTarefa();

    OffsetDateTime novaDataVencimento = new Date().toInstant()
                                            .atOffset(ZoneOffset.UTC)
                                            .plusDays(5);

    String dataVencimentoFormatada = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(novaDataVencimento);

    var payload = """
        {
          "titulo": "Novo título",
          "descricao": "Nova descrição",
          "prioridade": "ALTA",
          "data_vencimento": "%s",
          "version": null
        }
        """.formatted(dataVencimentoFormatada);

    mockMvc.perform(put("/api/v1/projects/{projectId}/tasks/{taskId}", projetoId, tarefaId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.version").exists());
  }

  @Test
  void deveRetornar200QuandoStatusAlteradoComSucesso() throws Exception {
    TarefaId tarefaId = criarTarefa();

    var body = """
        {
            "projetoId": "%s",
            "tarefaId": "%s",
            "status": "CONCLUIDA",
            "version": 1
        }
        """.formatted(projetoId, tarefaId);

    mockMvc.perform(patch("/api/v1/projects/{projectId}/tasks/{taskId}/status", projetoId, tarefaId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONCLUIDA"));
  }

  @Test
  void deveRetornar400QuandoDadosInvalidos() throws Exception {
    UUID projetoId = UUID.randomUUID();
    UUID tarefaId = UUID.randomUUID();

    var body = """
        {
            "projetoId": "%s",
            "tarefaId": "%s",
            "status": null,
            "version": -1
        }
        """.formatted(projetoId, tarefaId);
    
    mockMvc.perform(patch("/api/v1/projects/{projectId}/tasks/{taskId}/status", projetoId, tarefaId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.status").value("O status da tarefa deve ser informado"))
        .andExpect(jsonPath("$.erros.version").value("A versão é obrigatória"));
  }

  @Test
  void deveExcluirTarefaComSucesso() throws Exception {
    var dataVencimento = new Date().toInstant().atOffset(ZoneOffset.UTC);
    var payload = """
        {
          "titulo": "Título da tarefa",
          "descricao": "Descrição da tarefa",
          "status": "CONCLUIDA",
          "prioridade": "BAIXA",
          "data_vencimento": "%s"
        }
        """.formatted(dataVencimento.toString());

    MvcResult result = mockMvc.perform(post("%s/%s/tasks".formatted(URL_BASE, projetoId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isCreated())
        .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    
    String tarefaUuid = jsonNode.get("id").asText();

    assertNotNull(tarefaUuid);

    TarefaId tarefaId = new TarefaId(UUID.fromString(tarefaUuid));

    // Act & Assert
    mockMvc.perform(delete("/api/v1/projects/{projectId}/tasks/{taskId}", projetoId, tarefaId))
        .andExpect(status().isNoContent());

    // Verificação extra: garantir que a tarefa não está mais no repositório
    Optional<Tarefa> tarefa = projetoRepository.findTarefaByProjetoId(projetoId, tarefaId);
    
    assertTrue(tarefa.isEmpty());
  }

  @Test
  void naoDeveExcluirTarefaComStatusPendente() throws Exception {
    // Arrange
    TarefaId tarefaId = criarTarefa(); // método helper para criar tarefa válida

    // Act & Assert
    mockMvc.perform(delete("/api/v1/projects/{projectId}/tasks/{taskId}", projetoId, tarefaId))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.error").value("Tarefa com status pendente não pode ser excluida."))
        ;

    // Verificação extra: garantir que a tarefa não está mais no repositório
    Optional<Tarefa> tarefa = projetoRepository.findTarefaByProjetoId(projetoId, tarefaId);
    
    assertTrue(tarefa.isPresent());
  }

  @Test
  void deveRetornar204MesmoSeTarefaNaoExistir() throws Exception {
    UUID tarefaInexistente = UUID.randomUUID();

    mockMvc.perform(delete("/api/v1/projects/{projectId}/tasks/{taskId}", projetoId, tarefaInexistente))
        .andExpect(status().isNoContent());
  }

  @Test
  void deveRetornar400QuandoJsonEstiverMalFormado() throws Exception {
    mockMvc.perform(delete("/api/v1/projects/{projectId}/tasks/{taskId}", projetoId, "id-malformado"))
        .andExpect(status().isBadRequest());
  }

}
