package de.unistuttgart.crosswordbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import de.unistuttgart.crosswordbackend.data.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = { WireMockConfig.class })
@Testcontainers
public class GameResultControllerTest {

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
        registry.add("overworld.url", () -> "http://localhost:9561");
    }

    private final String API_URL = "/results";

    @MockBean
    JWTValidatorService jwtValidatorService;

    Cookie cookie = new Cookie("access_token", "testToken");

    @Autowired
    private MockMvc mockMvc;

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
        final GameResultDTO gameResultDTO = new GameResultDTO(
            24,
            24,
            UUID.randomUUID(),
            20000,
            Set.of(new GameAnswerDTO("answer", "correctAnswer", "question", false))
        );
        final String bodyValue = objectMapper.writeValueAsString(gameResultDTO);
        final MvcResult result = mockMvc
            .perform(post(API_URL).cookie(cookie).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        final GameResultDTO createdGameResultDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            GameResultDTO.class
        );

        assertEquals(gameResultDTO.getCorrectTiles(), createdGameResultDTO.getCorrectTiles());
        assertEquals(gameResultDTO.getNumberOfTiles(), createdGameResultDTO.getNumberOfTiles());
        assertEquals(gameResultDTO.getConfiguration(), createdGameResultDTO.getConfiguration());
    }

    @Test
    void testWithoutCookie_ThrowsBadRequest() throws Exception {
        mockMvc.perform(post(API_URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}
