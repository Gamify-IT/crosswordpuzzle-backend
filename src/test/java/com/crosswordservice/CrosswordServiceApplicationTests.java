package com.crosswordservice;

import com.crosswordservice.baseClasses.Configuration;
import com.crosswordservice.baseClasses.Question;
import com.crosswordservice.controller.CrosswordController;
import com.crosswordservice.repositories.ConfigurationRepository;
import com.crosswordservice.repositories.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(classes = CrosswordServiceApplication.class)
public class CrosswordServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private CrosswordController controller;

    @Test
    void testCreateConfigurations() throws Exception {
        Configuration config1 = new Configuration("config1");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(config1);


        MvcResult result = mockMvc.perform(post("/configurations")
                .content(requestJson).contentType(MediaType.APPLICATION_JSON
                ))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Configuration createdConfiguration = mapper.readValue(content, Configuration.class);
        assertEquals(config1.getName(), createdConfiguration.getName());
        assertEquals(config1.getName(), configurationRepository.findById(createdConfiguration.getId()).get().getName());
        configurationRepository.deleteById(createdConfiguration.getId());
    }

    @Test
    void testDeleteConfiguration() throws Exception {
        Configuration config1 = new Configuration("config1");
        config1 = configurationRepository.save(config1);
        Question question1 = new Question(config1.getId(),"Question1","Answer1");
        question1 =questionRepository.save(question1);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);


        MvcResult result = mockMvc.perform(delete("/configurations/"+config1.getName()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Configuration deletedConfiguration = mapper.readValue(content, Configuration.class);
        assertFalse(questionRepository.existsById(question1.getId()));
        assertFalse(configurationRepository.existsById(config1.getId()));
        assertEquals(config1.getName(),deletedConfiguration.getName());
    }

    @Test
    void testAddQuestions() throws Exception {
        Configuration config1 = new Configuration("config1");
        config1 = configurationRepository.save(config1);
        Question question1 = new Question();
        question1.setQuestion("Question");
        question1.setAnswer("Answer");

        List<Question> questions = new ArrayList<>();
        questions.add(question1);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(questions);


        MvcResult result = mockMvc.perform(post("/questions/"+config1.getName())
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<Question> createdQuestion = Arrays.asList(mapper.readValue(content, Question[].class));
        assertEquals(question1.getQuestion(), createdQuestion.get(0).getQuestion());
        assertEquals(question1.getAnswer(), questionRepository.findById(createdQuestion.get(0).getId()).getAnswer());
        questionRepository.deleteById(createdQuestion.get(0).getId());
        configurationRepository.deleteById(config1.getId());
    }

    @Test
    void testUpdateQuestions() throws Exception {
        Configuration config1 = new Configuration("config1");
        config1 = configurationRepository.save(config1);
        Question question1 = new Question(config1.getId(),"Question1","Answer1");
        Question question2 = new Question("Question2","Answer2");

        questionRepository.save(question1);
        List<Question> questions = new ArrayList<>();
        questions.add(question2);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(questions);


        MvcResult result = mockMvc.perform(put("/questions/"+config1.getName())
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<Question> updatedQuestion = Arrays.asList(mapper.readValue(content, Question[].class));
        assertEquals(question2.getQuestion(), updatedQuestion.get(0).getQuestion());
        assertEquals(question2.getAnswer(), questionRepository.findById(updatedQuestion.get(0).getId()).getAnswer());
        questionRepository.deleteById(updatedQuestion.get(0).getId());
        configurationRepository.deleteById(config1.getId());
    }

    @Test
    void testDeleteQuestions() throws Exception {
        Configuration config1 = new Configuration("config1");
        config1 = configurationRepository.save(config1);
        Question question1 = new Question(config1.getId(),"Question1","Answer1");

        question1 =questionRepository.save(question1);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);


        MvcResult result = mockMvc.perform(delete("/questions/"+question1.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Question deletedQuestion = mapper.readValue(content, Question.class);
        assertFalse(questionRepository.existsById(question1.getId()));
        assertEquals(question1.getQuestion(),deletedQuestion.getQuestion());
        configurationRepository.deleteById(config1.getId());
    }

    @Test
    void testGetAllQuestions() throws Exception {
        Configuration config1 = new Configuration("config1");
        config1 = configurationRepository.save(config1);
        Question question1 = new Question(config1.getId(),"Question1","Answer1");
        Question question2 = new Question(config1.getId(),"Question2","Answer2");

        question1 = questionRepository.save(question1);
        question2 = questionRepository.save(question2);

        List<Question> questionList = questionRepository.findAll();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);


        MvcResult result = mockMvc.perform(get("/questions/"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<Question> allQuestions = Arrays.asList(mapper.readValue(content, Question[].class));
        assertEquals(questionList.get(0).getQuestion(), allQuestions.get(0).getQuestion());
        assertEquals(questionList.get(0).getAnswer(), questionRepository.findById(allQuestions.get(0).getId()).getAnswer());
        assertEquals(questionList.get(1).getQuestion(), allQuestions.get(1).getQuestion());
        assertEquals(questionList.get(1).getAnswer(), questionRepository.findById(allQuestions.get(1).getId()).getAnswer());

        configurationRepository.deleteById(config1.getId());
        questionRepository.deleteById(question1.getId());
        questionRepository.deleteById(question2.getId());
    }

    @Test
    void testGetQuestions() throws Exception {
        Configuration config1 = new Configuration("config1");
        config1 = configurationRepository.save(config1);
        Question question1 = new Question(config1.getId(),"Question1","Answer1");
        Question question2 = new Question(config1.getId(),"Question2","Answer2");

        question1 = questionRepository.save(question1);
        question2 = questionRepository.save(question2);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);


        MvcResult result = mockMvc.perform(get("/questions/"+config1.getName()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<Question> allQuestions = Arrays.asList(mapper.readValue(content, Question[].class));
        assertEquals(question1.getQuestion(), allQuestions.get(0).getQuestion());
        assertEquals(question1.getAnswer(), questionRepository.findById(allQuestions.get(0).getId()).getAnswer());
        assertEquals(question2.getQuestion(), allQuestions.get(1).getQuestion());
        assertEquals(question2.getAnswer(), questionRepository.findById(allQuestions.get(1).getId()).getAnswer());

        controller.removeConfiguration(config1.getName());
    }

    @Test
    void testTestData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        MvcResult result = mockMvc.perform(post("/inputTestData"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<Question> allQuestions = Arrays.asList(mapper.readValue(content, Question[].class));
        assertEquals("Which language extends Javascript with type safety?", allQuestions.get(0).getQuestion());
        assertEquals("Typescript", questionRepository.findById(allQuestions.get(0).getId()).getAnswer());
        assertEquals("How is the system of rules called which defines well-formed expressions?", allQuestions.get(1).getQuestion());
        assertEquals("Syntax", questionRepository.findById(allQuestions.get(1).getId()).getAnswer());

        controller.removeConfiguration("test");
        controller.removeConfiguration("uml");
    }

    @Test
    void testValidateCrossword() throws Exception {
        Question question1 = new Question("Which language extends Javascript with type safety?","Typescript");
        Question question2 = new Question("How is the system of rules called which defines well-formed expressions?","Syntax");
        Question question3 = new Question("Which loop allows to set the count of iterations in the head?","for-loop");
        Question question4 = new Question("What is the abbreviation of the computing unit in a computer?","CPU");

        List<Question> questions = new ArrayList<>();
        questions.add(question1);
        questions.add(question2);
        questions.add(question3);
        questions.add(question4);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(questions);


        MvcResult result = mockMvc.perform(get("/validateCrossword")
                        .content(requestJson).contentType(MediaType.APPLICATION_JSON
                        ))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Boolean resultValid = mapper.readValue(content, Boolean.class);
        assertTrue(resultValid);
    }

    @Test
    void testMain(){

        CrosswordServiceApplication.main(new String[] {});
    }
}
