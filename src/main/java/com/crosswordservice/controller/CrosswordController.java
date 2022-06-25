package com.crosswordservice.controller;

import com.crosswordservice.baseClasses.Question;
import com.crosswordservice.crosswordchecker.CrosswordChecker;
import com.crosswordservice.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CrosswordController {

    @Autowired
    QuestionRepository questionRepository;

    @PostMapping("/save-all-questions")
    public List<Question> saveAllQuestions(@RequestBody List<Question> questions) {
        System.out.println("try to save all "+questions.size()+" questions: ");
        questions.forEach(question -> {
            System.out.println(question.getId()+";"+question.getQuestion());
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

    @GetMapping("/get-questions/{id}")
    public List<Question> getAllQuestions(@PathVariable String id) {
        System.out.println("try to get all questions");
        List<Question> questions = questionRepository.findAll();
        questions.forEach(question -> {
            if(question.getLevel()!=id){
                questions.remove(question);
            }
        });
        return questions;
    }

    @GetMapping("/validateCrossword")
    public boolean getValidationCrossword(@RequestBody List<Question> questions){
        CrosswordChecker crosswordChecker = new CrosswordChecker();
        return crosswordChecker.checkCrossword(questions);
    }
}
