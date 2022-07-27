package de.unistuttgart.crosswordbackend;

import static org.junit.jupiter.api.Assertions.*;
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
import de.unistuttgart.crosswordbackend.mapper.QuestionMapper;
import de.unistuttgart.crosswordbackend.repositories.ConfigurationRepository;
import de.unistuttgart.crosswordbackend.repositories.QuestionRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest(classes = CrosswordServiceApplication.class)
class CrosswordServiceApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private ConfigurationRepository configurationRepository;

  @Autowired
  private ConfigurationMapper configurationMapper;

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
  }

  @AfterEach
  void deleteTestData() {
    configurationRepository.deleteAll();
    questionRepository.deleteAll();
  }

  @Test
  void getConfigurations() throws Exception {
    final MvcResult result = mockMvc
      .perform(get(API_URL).contentType(MediaType.APPLICATION_JSON))
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
      .perform(get(API_URL + "/" + initialConfigDTO.getId()).contentType(MediaType.APPLICATION_JSON))
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
      .perform(get(API_URL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  void testCreateConfigurations() throws Exception {
    QuestionDTO quest1 = new QuestionDTO("Question", "Answer");
    ConfigurationDTO config1 = new ConfigurationDTO("config1", Set.of(quest1));

    objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(config1);

    MvcResult result = mockMvc
      .perform(post(API_URL).content(requestJson).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    String content = result.getResponse().getContentAsString();
    ConfigurationDTO createdConfiguration = objectMapper.readValue(content, ConfigurationDTO.class);
    assertEquals(config1.getName(), createdConfiguration.getName());
    assertEquals(config1.getName(), configurationRepository.findById(createdConfiguration.getId()).get().getName());
  }

  @Test
  void testUpdateConfigurations() throws Exception {
    QuestionDTO quest1 = new QuestionDTO("Question", "Answer");
    ConfigurationDTO config1 = new ConfigurationDTO("config1", Set.of(quest1));

    objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(config1);

    MvcResult result = mockMvc
      .perform(
        put(API_URL + "/" + initialConfigDTO.getId()).content(requestJson).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    String content = result.getResponse().getContentAsString();
    ConfigurationDTO updatedConfiguration = objectMapper.readValue(content, ConfigurationDTO.class);
    assertEquals(config1.getName(), updatedConfiguration.getName());
    Assertions.assertEquals(
      config1.getName(),
      configurationRepository.findById(updatedConfiguration.getId()).get().getName()
    );
  }

  @Test
  void testDeleteConfiguration() throws Exception {
    MvcResult result = mockMvc
      .perform(delete(API_URL + "/" + initialConfig.getId()))
      .andExpect(status().isOk())
      .andReturn();

    String content = result.getResponse().getContentAsString();
    Configuration deletedConfiguration = objectMapper.readValue(content, Configuration.class);
    assertFalse(configurationRepository.existsById(deletedConfiguration.getId()));
    assertEquals(initialConfig.getName(), deletedConfiguration.getName());
  }

  @Test
  void testAddQuestions() throws Exception {
    QuestionDTO question1 = new QuestionDTO("Question1", "Answer1");
    ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(question1);

    MvcResult result = mockMvc
      .perform(
        post(API_URL + "/" + initialConfig.getId() + "/questions/")
          .content(requestJson)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andReturn();

    String content = result.getResponse().getContentAsString();
    QuestionDTO createdQuestion = objectMapper.readValue(content, QuestionDTO.class);
    assertEquals(question1.getQuestionText(), createdQuestion.getQuestionText());
    assertEquals(question1.getAnswer(), questionRepository.findById(createdQuestion.getId()).get().getAnswer());
  }

  @Test
  void testUpdateQuestions() throws Exception {
    QuestionDTO question1 = new QuestionDTO("Question1", "Answer1");

    ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(question1);
    List<UUID> ids = new ArrayList<>();
    initialConfig
      .getQuestions()
      .forEach(question -> {
        ids.add(question.getId());
      });
    UUID idOfQuestion = ids.get(0);
    MvcResult result = mockMvc
      .perform(
        put(API_URL + "/" + initialConfig.getId() + "/questions/" + idOfQuestion)
          .content(requestJson)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    String content = result.getResponse().getContentAsString();
    QuestionDTO updatedQuestion = objectMapper.readValue(content, QuestionDTO.class);
    assertEquals(question1.getQuestionText(), updatedQuestion.getQuestionText());
    assertEquals(question1.getAnswer(), questionRepository.findById(updatedQuestion.getId()).get().getAnswer());
  }

  @Test
  void testUpdateQuestions_DoesNotExist_ThrowsNotFoundException() throws Exception {
    QuestionDTO question1 = new QuestionDTO("Question1", "Answer1");

    ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(question1);

    mockMvc
      .perform(
        put(API_URL + "/" + initialConfig.getId() + "/questions/" + UUID.randomUUID())
          .content(requestJson)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteQuestions() throws Exception {
    AtomicReference<QuestionDTO> question = new AtomicReference<>(new QuestionDTO());
    initialConfigDTO
      .getQuestions()
      .forEach(questionDTO -> {
        question.set(questionDTO);
      });

    MvcResult result = mockMvc
      .perform(delete(API_URL + "/" + initialConfigDTO.getId() + "/questions/" + question.get().getId()))
      .andExpect(status().isOk())
      .andReturn();

    String content = result.getResponse().getContentAsString();
    QuestionDTO deletedQuestion = objectMapper.readValue(content, QuestionDTO.class);
    assertFalse(questionRepository.existsById(question.get().getId()));
    assertEquals(question.get().getQuestionText(), deletedQuestion.getQuestionText());
  }

  @Test
  void testDeleteQuestions_DoesNotExist_ThrowsNotFoundException() throws Exception {
    AtomicReference<QuestionDTO> question = new AtomicReference<>(new QuestionDTO());
    initialConfigDTO
      .getQuestions()
      .forEach(questionDTO -> {
        question.set(questionDTO);
      });

    mockMvc
      .perform(delete(API_URL + "/" + initialConfigDTO.getId() + "/questions/" + UUID.randomUUID()))
      .andExpect(status().isNotFound());
  }

  @Test
  void testValidateCrossword() throws Exception {
    Question question1 = new Question("Which language extends Javascript with type safety?", "Typescript");
    Question question2 = new Question(
      "How is the system of rules called which defines well-formed expressions?",
      "Syntax"
    );
    Question question3 = new Question("Which loop allows to set the count of iterations in the head?", "for-loop");
    Question question4 = new Question("What is the abbreviation of the computing unit in a computer?", "CPU");

    List<Question> questions = new ArrayList<>();
    questions.add(question1);
    questions.add(question2);
    questions.add(question3);
    questions.add(question4);

    ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(questions);

    MvcResult result = mockMvc
      .perform(get("/crosswordpuzzle/validate-crossword").content(requestJson).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String content = result.getResponse().getContentAsString();
    assertTrue(objectMapper.readValue(content, Boolean.class));
  }
}
