package com.crosswordservice.controller;

import com.crosswordservice.baseClasses.Configuration;
import com.crosswordservice.baseClasses.Question;
import com.crosswordservice.crosswordchecker.CrosswordChecker;
import com.crosswordservice.repositories.ConfigurationRepository;
import com.crosswordservice.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Rest Controller for the crossword-puzzle backend
 */
@RestController
public class CrosswordController {

    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    ConfigurationRepository configurationRepository;

    @PostMapping("/inputTestData")
    public List<Question> inputTestData(){
        Configuration config = new Configuration("test");
        Configuration config2 = new Configuration("UML");
        configurationRepository.save(config);
        configurationRepository.save(config2);

        List<Question> questions = new ArrayList<>();

        Question quest1 = new Question(config.getId(),"Which language extends Javascript with type safety?","Typescript");
        Question quest2 = new Question(config.getId(),"How is the system of rules called which defines well-formed expressions?","Syntax");
        Question quest3 = new Question(config.getId(),"Which loop allows to set the count of iterations in the head?","for-loop");
        Question quest4 = new Question(config.getId(),"What is the abbreviation of the computing unit in a computer?","CPU");
        Question quest5 = new Question(config2.getId(),"Which Diagram is used to describe the structure of a system?","class-diagram");
        Question quest6 = new Question(config2.getId(),"Which Diagram is used to describe a sequence?","sequence-diagram");
        Question quest7 = new Question(config2.getId(),"Which Diagram is used to describe the components of a system?","component-diagram");
        questions.add(quest1);
        questions.add(quest2);
        questions.add(quest3);
        questions.add(quest4);
        questions.add(quest5);
        questions.add(quest6);
        questions.add(quest7);
        questionRepository.saveAll(questions);

        return questions;
    }

    /**
     * Adds a configuration
     * @param configuration configuration which will be added
     * @return added configuration
     */
    @PostMapping("/configurations")
    public Configuration saveConfiguration(@RequestBody Configuration configuration){
        configurationRepository.save(configuration);
        return configuration;
    }

    /**
     * Adds questions to a configuration.
     * @param questions question which will be added to the configuration
     * @param name configuration name
     * @return added questions
     */
    @PostMapping("/questions/{name}")
    public List<Question> saveAllQuestions(@RequestBody List<Question> questions, @PathVariable String name) {
        Configuration config = configurationRepository.findByName(name);
        if(config == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"configuration not found");
        }
        questions.forEach(question -> {
            question.setInternalId(config.getId());
        });
        questionRepository.saveAll(questions);
        return questions;
    }

    /**
     * Updates the questions of a configuration.
     * @param questions questions with an answer
     * @param name configuration name
     * @return updated questions.
     */
    @PutMapping("/questions/{name}")
    public List<Question> updateAllQuestions(@RequestBody List<Question> questions, @PathVariable String name){
        Configuration config = configurationRepository.findByName(name);
        if(config == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"configuration not found");
        }
        questionRepository.deleteByInternalId(config.getId());
        questions.forEach(question -> {
            question.setInternalId(config.getId());
        });
        questionRepository.saveAll(questions);
        return questions;
    }

    /**
     * Deletes a question with the given id.
     * @param id Id of a question
     * @return deleted question
     */
    @DeleteMapping("/questions/{id}")
    public Question removeQuestion(@PathVariable Long id){
        Question question = questionRepository.getReferenceById(id);
        if(question == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"question not found");
        }
        questionRepository.deleteById(id);
        return question;
    }

    /**
     * Returns all questions
     * @return all questions
     */
    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return (List<Question>) questionRepository.findAll();
    }

    /**
     * Returns all questions of a configuration
     * @param name Configuration name
     * @return list of questions of a configuration
     */
    @GetMapping("/questions/{name}")
    public List<Question> getAllQuestionsByConfigurationName(@PathVariable String name) {
        Configuration config = configurationRepository.findByName(name);
        if(config == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"configuration not found");
        }
        List<Question> questions = questionRepository.findByInternalId(config.getId());
        return questions;
    }

    /**
     * Validates if out of the given questions a crossword-puzzle can be created
     * @param questions questions to create a crossword-puzzle
     * @return true, if a crossword-puzzle can be created, otherwise false.
     */
    @GetMapping("/validateCrossword")
    public boolean isValidCrosswordPuzzle(@RequestBody List<Question> questions){
        CrosswordChecker crosswordChecker = new CrosswordChecker();
        return crosswordChecker.checkCrossword(questions);
    }
}
