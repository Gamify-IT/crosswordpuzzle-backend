package de.unistuttgart.crosswordbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import de.unistuttgart.crosswordbackend.data.Configuration;
import de.unistuttgart.crosswordbackend.data.ConfigurationDTO;
import de.unistuttgart.crosswordbackend.data.GameResultDTO;
import de.unistuttgart.crosswordbackend.data.Question;
import de.unistuttgart.crosswordbackend.mapper.ConfigurationMapper;
import de.unistuttgart.crosswordbackend.repositories.ConfigurationRepository;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WireMockConfig.class })
public class GameResultControllerTest {

  private final String API_URL = "/results";

  @MockBean
  JWTValidatorService jwtValidatorService;

  Cookie cookie = new Cookie("access_token", "testToken");

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ConfigurationMapper configurationMapper;

  @Autowired
  private ConfigurationRepository configurationRepository;

  @Autowired
  private WireMockServer mockResultsService;

  private ObjectMapper objectMapper;
  private Configuration initialConfig;
  private ConfigurationDTO initialConfigDTO;
  private Question initialQuestion1;
  private Question initialQuestion2;

  @BeforeEach
  public void createBasicData() throws IOException {
    ResultMocks.setupMockBooksResponse(mockResultsService);
    configurationRepository.deleteAll();
    initialQuestion1 = new Question("question1", "answer1");
    initialQuestion2 = new Question("question2", "answer2");

    final Configuration configuration = new Configuration("config1", Set.of(initialQuestion1, initialQuestion2));

    initialConfig = configurationRepository.save(configuration);
    initialConfigDTO = configurationMapper.configurationToConfigurationDTO(initialConfig);

    initialQuestion1 =
      initialConfig
        .getQuestions()
        .stream()
        .filter(question -> question.getQuestionText().equals(initialQuestion1.getQuestionText()))
        .findAny()
        .get();
    initialQuestion2 =
      initialConfig
        .getQuestions()
        .stream()
        .filter(question -> question.getQuestionText().equals(initialQuestion2.getQuestionText()))
        .findAny()
        .get();

    objectMapper = new ObjectMapper();

    doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
    when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
  }

  @AfterEach
  void deleteBasicData() {
    configurationRepository.deleteAll();
  }

  @Test
  void saveGameResult() throws Exception {
    final GameResultDTO gameResultDTO = new GameResultDTO(24, 24, UUID.randomUUID());
    final String bodyValue = objectMapper.writeValueAsString(gameResultDTO);
    final MvcResult result = mvc
      .perform(post(API_URL).cookie(cookie).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final GameResultDTO createdGameResultDTO = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      GameResultDTO.class
    );

    assertEquals(gameResultDTO, createdGameResultDTO);
  }
}
