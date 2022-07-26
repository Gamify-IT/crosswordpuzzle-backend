package de.unistuttgart.crosswordbackend.controller;

import de.unistuttgart.crosswordbackend.data.Configuration;
import de.unistuttgart.crosswordbackend.data.ConfigurationDTO;
import de.unistuttgart.crosswordbackend.data.Question;
import de.unistuttgart.crosswordbackend.data.QuestionDTO;
import de.unistuttgart.crosswordbackend.mapper.ConfigurationMapper;
import de.unistuttgart.crosswordbackend.mapper.QuestionMapper;
import de.unistuttgart.crosswordbackend.repositories.ConfigurationRepository;
import de.unistuttgart.crosswordbackend.service.ConfigService;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configurations")
@Slf4j
public class ConfigController {

  @Autowired
  ConfigurationRepository configurationRepository;

  @Autowired
  QuestionMapper questionMapper;

  @Autowired
  ConfigurationMapper configurationMapper;

  @Autowired
  ConfigService configService;

  @GetMapping("")
  public List<ConfigurationDTO> getConfigurations() {
    log.debug("get all configurations");
    return configurationMapper.configurationsToConfigurationDTOs(configurationRepository.findAll());
  }

  @GetMapping("/{id}")
  public ConfigurationDTO getConfiguration(@PathVariable final UUID id) {
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
