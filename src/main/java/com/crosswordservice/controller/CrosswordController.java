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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class CrosswordController {

    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    ConfigurationRepository configurationRepository;

    @PostMapping("/create-config")
    public Configuration saveConfiguration(@RequestBody Configuration configuration){
        configurationRepository.save(configuration);
        return configuration;
    }

    @PostMapping("/save-all-questions/{name}")
    public List<Question> saveAllQuestions(@RequestBody List<Question> questions, @PathVariable String name) {
        System.out.println("try to save all "+questions.size()+" questions: ");
        Configuration config = configurationRepository.findByName(name);
        questions.forEach(question -> {
            question.setInternalId(config.getId());
        });
        questionRepository.saveAll(questions);
        return questions;
    }

    @PutMapping("/update-all-questions/{name}")
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

    @DeleteMapping("/removeQuestion/{id}")
    public Question removeQuestion(@PathVariable Long id){
        Question question = questionRepository.getReferenceById(id);
        questionRepository.deleteById(id);
        return question;
    }

    @GetMapping("/get-all-questions")
    public List<Question> getAllQuestions() {
        System.out.println("try to get all questions");
        return (List<Question>) questionRepository.findAll();
    }

    @GetMapping("/get-questions/{name}")
    public List<Question> getAllQuestions(@PathVariable String name) {
        System.out.println("try to get all questions");

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
