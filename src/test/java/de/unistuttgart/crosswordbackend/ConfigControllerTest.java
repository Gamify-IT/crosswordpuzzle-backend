package de.unistuttgart.crosswordbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unistuttgart.crosswordbackend.data.Configuration;
import de.unistuttgart.crosswordbackend.data.ConfigurationDTO;
import de.unistuttgart.crosswordbackend.data.Question;
import de.unistuttgart.crosswordbackend.data.QuestionDTO;
import de.unistuttgart.crosswordbackend.mapper.ConfigurationMapper;
import de.unistuttgart.crosswordbackend.repositories.ConfigurationRepository;
import de.unistuttgart.crosswordbackend.repositories.QuestionRepository;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@SpringBootTest(classes = CrosswordServiceApplication.class)
@Testcontainers
class ConfigControllerTest {

  @Container
  public static PostgreSQLContainer postgresDB = new PostgreSQLContainer("postgres:14-alpine")
          .withDatabaseName("postgres")
          .withUsername("postgres")
          .withPassword("postgres");

  @DynamicPropertySource
  public static void properties(final DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
    registry.add("spring.datasource.username", postgresDB::getUsername);
    registry.add("spring.datasource.password", postgresDB::getPassword);
  }

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  JWTValidatorService jwtValidatorService;

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private ConfigurationRepository configurationRepository;

  @Autowired
  private ConfigurationMapper configurationMapper;

  Cookie cookie = new Cookie("access_token", "testToken");

  private final String API_URL = "/configurations";
  private ObjectMapper objectMapper;
  private Configuration initialConfig;
  private ConfigurationDTO initialConfigDTO;

  @BeforeEach
  void createTestData() {
    configurationRepository.deleteAll();
    questionRepository.deleteAll();
    final Question question1 = new Question("QuestionInit1", "AnswerInit1");
    final Question question2 = new Question("QuestionInit2", "AnswerInit2");
    final Configuration config = new Configuration("configInit1", Set.of(question1, question2));
    initialConfig = configurationRepository.save(config);
    initialConfigDTO = configurationMapper.configurationToConfigurationDTO(initialConfig);

    objectMapper = new ObjectMapper();

    doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
    when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
  }

  @AfterEach
  void deleteTestData() {
    configurationRepository.deleteAll();
    questionRepository.deleteAll();
  }

  @Test
  void getConfigurations() throws Exception {
    final MvcResult result = mockMvc
      .perform(get(API_URL).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final List<ConfigurationDTO> configurations = Arrays.asList(
      objectMapper.readValue(result.getResponse().getContentAsString(), ConfigurationDTO[].class)
    );

    assertSame(1, configurations.size());
    assertEquals(initialConfigDTO.getName(), configurations.get(0).getName());
    assertEquals(initialConfigDTO.getId(), configurations.get(0).getId());
  }

  @Test
  void getSpecificConfigurations() throws Exception {
    final MvcResult result = mockMvc
      .perform(get(API_URL + "/" + initialConfigDTO.getId()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final ConfigurationDTO configuration = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      ConfigurationDTO.class
    );

    assertEquals(initialConfigDTO.getName(), configuration.getName());
    assertEquals(initialConfigDTO.getId(), configuration.getId());
  }

  @Test
  void getSpecificConfiguration_DoesNotExist_ThrowsNotFound() throws Exception {
    mockMvc
      .perform(get(API_URL + "/" + UUID.randomUUID()).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  void testCreateConfigurations() throws Exception {
    final QuestionDTO quest1 = new QuestionDTO("Question", "Answer");
    final ConfigurationDTO config1 = new ConfigurationDTO("config1", Set.of(quest1));

    objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    final String requestJson = ow.writeValueAsString(config1);

    final MvcResult result = mockMvc
      .perform(post(API_URL).cookie(cookie).content(requestJson).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final ConfigurationDTO createdConfiguration = objectMapper.readValue(content, ConfigurationDTO.class);
    assertEquals(config1.getName(), createdConfiguration.getName());
    assert createdConfiguration.getId() != null;
    assertEquals(config1.getName(), configurationRepository.findById(createdConfiguration.getId()).get().getName());
  }

  @Test
  void testUpdateConfigurations() throws Exception {
    final QuestionDTO quest1 = new QuestionDTO("Question", "Answer");
    final ConfigurationDTO config1 = new ConfigurationDTO("config1", Set.of(quest1));

    objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    final String requestJson = ow.writeValueAsString(config1);

    final MvcResult result = mockMvc
      .perform(
        put(API_URL + "/" + initialConfigDTO.getId())
          .cookie(cookie)
          .content(requestJson)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final ConfigurationDTO updatedConfiguration = objectMapper.readValue(content, ConfigurationDTO.class);
    assertEquals(config1.getName(), updatedConfiguration.getName());
    assert updatedConfiguration.getId() != null;
    Assertions.assertEquals(
      config1.getName(),
      configurationRepository.findById(updatedConfiguration.getId()).get().getName()
    );
  }

  @Test
  void testDeleteConfiguration() throws Exception {
    final MvcResult result = mockMvc
      .perform(delete(API_URL + "/" + initialConfig.getId()).cookie(cookie))
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final Configuration deletedConfiguration = objectMapper.readValue(content, Configuration.class);
    assertFalse(configurationRepository.existsById(deletedConfiguration.getId()));
    assertEquals(initialConfig.getName(), deletedConfiguration.getName());
  }

  @Test
  void testAddQuestions() throws Exception {
    final QuestionDTO question1 = new QuestionDTO("Question1", "Answer1");
    final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    final String requestJson = ow.writeValueAsString(question1);

    final MvcResult result = mockMvc
      .perform(
        post(API_URL + "/" + initialConfig.getId() + "/questions/")
          .cookie(cookie)
          .content(requestJson)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final QuestionDTO createdQuestion = objectMapper.readValue(content, QuestionDTO.class);
    assertEquals(question1.getQuestionText(), createdQuestion.getQuestionText());
    assertEquals(question1.getAnswer(), questionRepository.findById(createdQuestion.getId()).get().getAnswer());
  }

  @Test
  void testUpdateQuestions() throws Exception {
    final QuestionDTO question1 = new QuestionDTO("Question1", "Answer1");

    final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    final String requestJson = ow.writeValueAsString(question1);
    final List<UUID> ids = new ArrayList<>();
    initialConfig
      .getQuestions()
      .forEach(question -> {
        ids.add(question.getId());
      });
    final UUID idOfQuestion = ids.get(0);
    final MvcResult result = mockMvc
      .perform(
        put(API_URL + "/" + initialConfig.getId() + "/questions/" + idOfQuestion)
          .cookie(cookie)
          .content(requestJson)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final QuestionDTO updatedQuestion = objectMapper.readValue(content, QuestionDTO.class);
    assertEquals(question1.getQuestionText(), updatedQuestion.getQuestionText());
    assertEquals(question1.getAnswer(), questionRepository.findById(updatedQuestion.getId()).get().getAnswer());
  }

  @Test
  void testUpdateQuestions_DoesNotExist_ThrowsNotFoundException() throws Exception {
    final QuestionDTO question1 = new QuestionDTO("Question1", "Answer1");

    final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    final String requestJson = ow.writeValueAsString(question1);

    mockMvc
      .perform(
        put(API_URL + "/" + initialConfig.getId() + "/questions/" + UUID.randomUUID())
          .cookie(cookie)
          .content(requestJson)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteQuestions() throws Exception {
    final AtomicReference<QuestionDTO> question = new AtomicReference<>(new QuestionDTO());
    initialConfigDTO.getQuestions().forEach(question::set);

    final MvcResult result = mockMvc
      .perform(delete(API_URL + "/" + initialConfigDTO.getId() + "/questions/" + question.get().getId()).cookie(cookie))
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    final QuestionDTO deletedQuestion = objectMapper.readValue(content, QuestionDTO.class);
    assertFalse(questionRepository.existsById(question.get().getId()));
    assertEquals(question.get().getQuestionText(), deletedQuestion.getQuestionText());
  }

  @Test
  void testDeleteQuestions_DoesNotExist_ThrowsNotFoundException() throws Exception {
    final AtomicReference<QuestionDTO> question = new AtomicReference<>(new QuestionDTO());
    initialConfigDTO.getQuestions().forEach(question::set);

    mockMvc
      .perform(delete(API_URL + "/" + initialConfigDTO.getId() + "/questions/" + UUID.randomUUID()).cookie(cookie))
      .andExpect(status().isNotFound());
  }
}
