package de.unistuttgart.crosswordbackend.statistic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.crosswordbackend.CrosswordServiceApplication;
import de.unistuttgart.crosswordbackend.data.*;
import de.unistuttgart.crosswordbackend.data.statistic.ProblematicQuestion;
import de.unistuttgart.crosswordbackend.data.statistic.TimeSpentDistribution;
import de.unistuttgart.crosswordbackend.mapper.GameAnswerMapper;
import de.unistuttgart.crosswordbackend.mapper.GameResultMapper;
import de.unistuttgart.crosswordbackend.mapper.QuestionMapper;
import de.unistuttgart.crosswordbackend.repositories.ConfigurationRepository;
import de.unistuttgart.crosswordbackend.repositories.GameResultRepository;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import java.util.*;
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

@AutoConfigureMockMvc
@SpringBootTest(classes = CrosswordServiceApplication.class)
@Testcontainers
class StatisticTest {

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

    private final String API_URL = "/statistics";

    @MockBean
    JWTValidatorService jwtValidatorService;

    Cookie cookie = new Cookie("access_token", "testToken");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private GameResultMapper gameResultMapper;

    @Autowired
    private GameAnswerMapper gameAnswerMapper;

    private ObjectMapper objectMapper;

    private Configuration randomConfiguration;
    private Configuration staticConfiguration;
    int numberOfGameResultsOfStaticConfiguration;
    private QuestionDTO problematicQuestion;
    private QuestionDTO bestAnsweredQuestion;
    private List<GameResult> gameResults;

    @BeforeEach
    public void createBasicData() {
        gameResultRepository.deleteAll();
        configurationRepository.deleteAll();

        Set<Question> questions = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            questions.add(new Question("question" + i, "answer" + i));
        }

        randomConfiguration = new Configuration();
        randomConfiguration.setQuestions(questions);
        randomConfiguration.setName("randomConfiguration");
        randomConfiguration = configurationRepository.save(randomConfiguration);

        gameResults = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            GameResultDTO gameResultDTO = new GameResultDTO();
            gameResultDTO.setConfiguration(randomConfiguration.getId());
            gameResultDTO.setDuration((long) (Math.random() * 100));
            Set<GameAnswer> answers = new HashSet<>();
            int correctTiles = 0;
            int numberOfTiles = 0;
            for (Question question : randomConfiguration.getQuestions()) {
                if (new Random().nextInt(10) > 3) {
                    answers.add(
                        new GameAnswer(
                            UUID.randomUUID(),
                            question.getAnswer(),
                            question.getAnswer(),
                            question.getQuestionText(),
                            true
                        )
                    );
                    correctTiles += question.getAnswer().length() - 1;
                    numberOfTiles += question.getAnswer().length() - 1;
                } else {
                    answers.add(
                        new GameAnswer(
                            UUID.randomUUID(),
                            "wrong Answer",
                            question.getAnswer(),
                            question.getQuestionText(),
                            false
                        )
                    );
                    numberOfTiles += question.getAnswer().length() - 1;
                }
            }
            gameResultDTO.setCorrectTiles(correctTiles);
            gameResultDTO.setNumberOfTiles(numberOfTiles);
            gameResultDTO.setAnswers(gameAnswerMapper.gameAnswersToGameAnswerDTOs(answers));
            GameResult gameResult = gameResultRepository.save(
                gameResultMapper.gameResultDTOToGameResult(gameResultDTO)
            );
            gameResults.add(gameResult);
        }

        questions = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            questions.add(new Question("question" + i, "answer" + i));
        }

        staticConfiguration = new Configuration();
        staticConfiguration.setQuestions(questions);
        staticConfiguration.setName("staticConfiguration");
        staticConfiguration = configurationRepository.save(staticConfiguration);

        List<Question> questionList = questions.stream().toList();

        GameResultDTO gameResultDTO1 = new GameResultDTO();
        gameResultDTO1.setConfiguration(staticConfiguration.getId());
        gameResultDTO1.setDuration((long) (Math.random() * 100));
        Set<GameAnswer> answers1 = new HashSet<>();
        int correctTiles1 = 0;
        int numberOfTiles1 = 0;

        answers1.add(
            new GameAnswer(
                UUID.randomUUID(),
                questionList.get(0).getAnswer(),
                questionList.get(0).getAnswer(),
                questionList.get(0).getQuestionText(),
                true
            )
        );
        correctTiles1 += questionList.get(0).getAnswer().length() - 1;
        numberOfTiles1 += questionList.get(0).getAnswer().length() - 1;

        for (int i = 1; i < questionList.size(); i++) {
            answers1.add(
                new GameAnswer(
                    UUID.randomUUID(),
                    "wrong Answer",
                    questionList.get(i).getAnswer(),
                    questionList.get(i).getQuestionText(),
                    false
                )
            );
            numberOfTiles1 += questionList.get(i).getAnswer().length() - 1;
        }

        gameResultDTO1.setCorrectTiles(correctTiles1);
        gameResultDTO1.setNumberOfTiles(numberOfTiles1);
        gameResultDTO1.setAnswers(gameAnswerMapper.gameAnswersToGameAnswerDTOs(answers1));

        GameResultDTO gameResultDTO2 = new GameResultDTO();
        gameResultDTO2.setConfiguration(staticConfiguration.getId());
        gameResultDTO2.setDuration((long) (Math.random() * 100));
        Set<GameAnswer> answers2 = new HashSet<>();
        int correctTiles2 = 0;
        int numberOfTiles2 = 0;
        answers2.add(
            new GameAnswer(
                UUID.randomUUID(),
                questionList.get(0).getAnswer(),
                questionList.get(0).getAnswer(),
                questionList.get(0).getQuestionText(),
                true
            )
        );
        correctTiles2 += questionList.get(0).getAnswer().length() - 1;
        numberOfTiles2 += questionList.get(0).getAnswer().length() - 1;

        for (int i = 1; i < questionList.size(); i++) {
            answers2.add(
                new GameAnswer(
                    UUID.randomUUID(),
                    "wrong Answer",
                    questionList.get(i).getAnswer(),
                    questionList.get(i).getQuestionText(),
                    false
                )
            );
            numberOfTiles2 += questionList.get(i).getAnswer().length() - 1;
        }
        gameResultDTO2.setCorrectTiles(correctTiles2);
        gameResultDTO2.setNumberOfTiles(numberOfTiles2);
        gameResultDTO2.setAnswers(gameAnswerMapper.gameAnswersToGameAnswerDTOs(answers2));

        GameResultDTO gameResultDTO3 = new GameResultDTO();
        gameResultDTO3.setConfiguration(staticConfiguration.getId());
        gameResultDTO3.setDuration((long) (Math.random() * 100));
        Set<GameAnswer> answers3 = new HashSet<>();
        int correctTiles3 = 0;
        int numberOfTiles3 = 0;
        answers3.add(
            new GameAnswer(
                UUID.randomUUID(),
                questionList.get(0).getAnswer(),
                questionList.get(0).getAnswer(),
                questionList.get(0).getQuestionText(),
                true
            )
        );
        correctTiles3 += questionList.get(0).getAnswer().length() - 1;
        numberOfTiles3 += questionList.get(0).getAnswer().length() - 1;

        for (int i = 1; i < questionList.size(); i++) {
            answers3.add(
                new GameAnswer(
                    UUID.randomUUID(),
                    "wrong Answer",
                    questionList.get(i).getAnswer(),
                    questionList.get(i).getQuestionText(),
                    false
                )
            );
            numberOfTiles3 += questionList.get(i).getAnswer().length() - 1;
        }
        gameResultDTO3.setCorrectTiles(correctTiles3);
        gameResultDTO3.setNumberOfTiles(numberOfTiles3);
        gameResultDTO3.setAnswers(gameAnswerMapper.gameAnswersToGameAnswerDTOs(answers3));

        GameResultDTO gameResultDTO4 = new GameResultDTO();
        gameResultDTO4.setConfiguration(staticConfiguration.getId());
        gameResultDTO4.setDuration((long) (Math.random() * 100));
        Set<GameAnswer> answers4 = new HashSet<>();
        int correctTiles4 = 0;
        int numberOfTiles4 = 0;
        answers4.add(
            new GameAnswer(
                UUID.randomUUID(),
                questionList.get(0).getAnswer(),
                questionList.get(0).getAnswer(),
                questionList.get(0).getQuestionText(),
                true
            )
        );
        correctTiles4 += questionList.get(0).getAnswer().length() - 1;
        numberOfTiles4 += questionList.get(0).getAnswer().length() - 1;

        for (int i = 1; i < questionList.size(); i++) {
            answers4.add(
                new GameAnswer(
                    UUID.randomUUID(),
                    "wrong Answer",
                    questionList.get(i).getAnswer(),
                    questionList.get(i).getQuestionText(),
                    false
                )
            );
            numberOfTiles4 += questionList.get(i).getAnswer().length() - 1;
        }
        gameResultDTO4.setCorrectTiles(correctTiles4);
        gameResultDTO4.setNumberOfTiles(numberOfTiles4);
        gameResultDTO4.setAnswers(gameAnswerMapper.gameAnswersToGameAnswerDTOs(answers4));

        numberOfGameResultsOfStaticConfiguration = 4;

        problematicQuestion = questionMapper.questionToQuestionDTO(questionList.get(5));
        bestAnsweredQuestion = questionMapper.questionToQuestionDTO(questionList.get(0));

        gameResultRepository.saveAll(
            List.of(
                gameResultMapper.gameResultDTOToGameResult(gameResultDTO1),
                gameResultMapper.gameResultDTOToGameResult(gameResultDTO2),
                gameResultMapper.gameResultDTOToGameResult(gameResultDTO3),
                gameResultMapper.gameResultDTOToGameResult(gameResultDTO4)
            )
        );

        objectMapper = new ObjectMapper();
        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
    }

    @Test
    void testGetProblematicQuestions() throws Exception {
        final MvcResult result = mvc
            .perform(
                get(API_URL + "/" + staticConfiguration.getId() + "/problematic-questions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final List<ProblematicQuestion> problematicQuestions = Arrays.asList(
            objectMapper.readValue(result.getResponse().getContentAsString(), ProblematicQuestion[].class)
        );

        for (int i = 0; i < problematicQuestions.size() - 1; i++) {
            assertTrue(
                problematicQuestions.get(i).getWrongAnswers() >= problematicQuestions.get(i + 1).getWrongAnswers()
            );
        }

        System.out.println(problematicQuestions);

        assertFalse(
            problematicQuestions
                .stream()
                .map(ProblematicQuestion::getQuestion)
                .anyMatch(question -> question.equals(bestAnsweredQuestion))
        );
        assertEquals(problematicQuestion, problematicQuestions.get(0).getQuestion());
        assertSame(5, problematicQuestions.size());
    }

    @Test
    void testGetTimeSpentDistribution() throws Exception {
        final MvcResult result = mvc
            .perform(
                get(API_URL + "/" + staticConfiguration.getId() + "/time-spent")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final List<TimeSpentDistribution> timeSpentDistributions = Arrays.asList(
            objectMapper.readValue(result.getResponse().getContentAsString(), TimeSpentDistribution[].class)
        );
        long amountOfGameResults = timeSpentDistributions.stream().map(TimeSpentDistribution::getCount).count();
        assertEquals(numberOfGameResultsOfStaticConfiguration, amountOfGameResults);
    }
}
