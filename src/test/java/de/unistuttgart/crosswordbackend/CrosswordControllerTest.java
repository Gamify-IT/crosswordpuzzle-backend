package de.unistuttgart.crosswordbackend;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.unistuttgart.crosswordbackend.data.Question;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CrosswordControllerTest {

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
  MockMvc mockMvc;

  @MockBean
  JWTValidatorService jwtValidatorService;

  Cookie cookie = new Cookie("access_token", "testToken");

  private ObjectMapper objectMapper;

  @BeforeEach
  void createTestData() {
    objectMapper = new ObjectMapper();

    doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
  }

  @Test
  void testValidateCrossword() throws Exception {
    final Question question1 = new Question("Which language extends Javascript with type safety?", "Typescript");
    final Question question2 = new Question(
      "How is the system of rules called which defines well-formed expressions?",
      "Syntax"
    );
    final Question question3 = new Question(
      "Which loop allows to set the count of iterations in the head?",
      "for-loop"
    );
    final Question question4 = new Question("What is the abbreviation of the computing unit in a computer?", "CPU");

    final List<Question> questions = new ArrayList<>();
    questions.add(question1);
    questions.add(question2);
    questions.add(question3);
    questions.add(question4);

    final ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
    final String requestJson = ow.writeValueAsString(questions);

    final MvcResult result = mockMvc
      .perform(
        get("/crosswordpuzzle/validate-crossword")
          .cookie(cookie)
          .content(requestJson)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final String content = result.getResponse().getContentAsString();
    assertTrue(objectMapper.readValue(content, Boolean.class));
  }

  @Test
  void testWithoutCookie_ThrowsBadRequest() throws Exception {
    mockMvc
      .perform(get("/crosswordpuzzle/validate-crossword").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }
}
