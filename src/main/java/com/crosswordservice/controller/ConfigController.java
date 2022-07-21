package com.crosswordservice.controller;

import com.crosswordservice.data.Configuration;
import com.crosswordservice.data.ConfigurationDTO;
import com.crosswordservice.data.Question;
import com.crosswordservice.data.QuestionDTO;
import com.crosswordservice.mapper.ConfigurationMapper;
import com.crosswordservice.mapper.QuestionMapper;
import com.crosswordservice.repositories.ConfigurationRepository;
import com.crosswordservice.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/v1/minigames/crosswordpuzzle/configurations")
@Slf4j
public class ConfigController {
    private final String configNotFound = "Configuration not found";

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    ConfigurationMapper configurationMapper;

    @Autowired
    ConfigService configService;

    @PostMapping("/inputTestData")
    public List<ConfigurationDTO> inputTestData(){
        Set<Question> questions1 = new HashSet<>();
        Set<Question> questions2 = new HashSet<>();

        Question quest1 = new Question("Which language extends Javascript with type safety?","Typescript");
        Question quest2 = new Question("How is the system of rules called which defines well-formed expressions?","Syntax");
        Question quest3 = new Question("Which loop allows to set the count of iterations in the head?","for-loop");
        Question quest4 = new Question("What is the abbreviation of the computing unit in a computer?","CPU");
        Question quest5 = new Question("Which Diagram is used to describe the structure of a system?","class-diagram");
        Question quest6 = new Question("Which Diagram is used to describe a sequence?","sequence-diagram");
        Question quest7 = new Question("Which Diagram is used to describe the components of a system?","component-diagram");
        questions1.add(quest1);
        questions1.add(quest2);
        questions1.add(quest3);
        questions1.add(quest4);
        questions2.add(quest5);
        questions2.add(quest6);
        questions2.add(quest7);

        Configuration config1 = new Configuration("test", questions1);
        Configuration config2 = new Configuration("uml", questions2);
        config1 = configurationRepository.save(config1);
        config2 = configurationRepository.save(config2);

        List<Configuration> configs = new ArrayList<>();
        configs.add(config1);
        configs.add(config2);
        return configurationMapper.configurationsToConfigurationDTOs(configs);
    }

    @GetMapping("")
    public List<ConfigurationDTO> getConfigurations(){
        log.debug("get all configurations");
        return configurationMapper.configurationsToConfigurationDTOs(configurationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ConfigurationDTO getConfiguration(@PathVariable final UUID id){
        log.debug("get configuration {}", id);
        return configurationMapper.configurationToConfigurationDTO(configService.getConfiguration(id));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ConfigurationDTO createConfiguration(@RequestBody final ConfigurationDTO configurationDTO) {
        log.debug("create configuration {}", configurationDTO);
        return configService.saveConfiguration(configurationDTO);
    }

    @PutMapping("/{id}")
    public ConfigurationDTO updateConfiguration(
            @PathVariable final UUID id,
            @RequestBody final ConfigurationDTO configurationDTO
    ) {
        log.debug("update configuration {} with {}", id, configurationDTO);
        return configService.updateConfiguration(id, configurationDTO);
    }

    @DeleteMapping("/{id}")
    public ConfigurationDTO deleteConfiguration(@PathVariable final UUID id) {
        log.debug("delete configuration {}", id);
        return configService.deleteConfiguration(id);
    }

    @PostMapping("/{id}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionDTO addQuestionToConfiguration(
            @PathVariable final UUID id,
            @RequestBody final QuestionDTO questionDTO
    ) {
        log.debug("add question {} to configuration {}", questionDTO, id);
        return configService.addQuestionToConfiguration(id, questionDTO);
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    public QuestionDTO removeQuestionFromConfiguration(@PathVariable final UUID id, @PathVariable final UUID questionId) {
        log.debug("remove question {} from configuration {}", questionId, id);
        return configService.removeQuestionFromConfiguration(id, questionId);
    }

    @PutMapping("/{id}/questions/{questionId}")
    public QuestionDTO updateQuestionFromConfiguration(
            @PathVariable final UUID id,
            @PathVariable final UUID questionId,
            @RequestBody final QuestionDTO questionDTO
    ) {
        log.debug("update question {} with {} for configuration {}", questionId, questionDTO, id);
        return configService.updateQuestionFromConfiguration(id, questionId, questionDTO);
    }
}
