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

import java.io.ObjectInputFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
        configurationRepository.save(config);

        List<Question> questions = new ArrayList<>();

        Question quest1 = new Question(config.getId(),"Which language extends Javascript with type safety?","Typescript");
        Question quest2 = new Question(config.getId(),"How is the system of rules called which defines well-formed expressions?","Syntax");
        Question quest3 = new Question(config.getId(),"Which loop allows to set the count of iterations in the head?","for-loop");
        Question quest4 = new Question(config.getId(),"What is the abbreviation of the computing unit in a computer?","CPU");
        questions.add(quest1);
        questions.add(quest2);
        questions.add(quest3);
        questions.add(quest4);
        questionRepository.saveAll(questions);

        return questions;
    }

    @PostMapping("/configuration")
    public Configuration saveConfiguration(@RequestBody Configuration configuration){
        configurationRepository.save(configuration);
        return configuration;
    }

    @PostMapping("/questions/{name}")
    public List<Question> saveAllQuestions(@RequestBody List<Question> questions, @PathVariable String name) {
        Configuration config = configurationRepository.findByName(name);
        questions.forEach(question -> {
            question.setInternalId(config.getId());
        });
        questionRepository.saveAll(questions);
        return questions;
    }

    @PutMapping("/questions/{name}")
    public List<Question> updateAllQuestions(@RequestBody List<Question> questions, @PathVariable String name){
        Configuration config = configurationRepository.findByName(name);
        if(config == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"id not found");
        }
        questionRepository.deleteByInternalId(config.getId());
        questions.forEach(question -> {
            question.setInternalId(config.getId());
        });
        questionRepository.saveAll(questions);
        return questions;
    }

    @DeleteMapping("/question/{id}")
    public Question removeQuestion(@PathVariable Long id){
        Question question = questionRepository.getReferenceById(id);
        questionRepository.deleteById(id);
        return question;
    }

    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return (List<Question>) questionRepository.findAll();
    }

    @GetMapping("/questions/{name}")
    public List<Question> getAllQuestions(@PathVariable String name) {
        Configuration config = configurationRepository.findByName(name);

        List<Question> questions = questionRepository.findByInternalId(config.getId());

        return questions;
    }

    @GetMapping("/validateCrossword")
    public boolean getValidationCrossword(@RequestBody List<Question> questions){
        CrosswordChecker crosswordChecker = new CrosswordChecker();
        return crosswordChecker.checkCrossword(questions);
    }
}
