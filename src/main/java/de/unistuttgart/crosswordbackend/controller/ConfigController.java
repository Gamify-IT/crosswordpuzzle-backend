package de.unistuttgart.crosswordbackend.controller;

import static de.unistuttgart.crosswordbackend.data.Roles.LECTURER_ROLE;

import de.unistuttgart.crosswordbackend.data.ConfigurationDTO;
import de.unistuttgart.crosswordbackend.data.QuestionDTO;
import de.unistuttgart.crosswordbackend.mapper.ConfigurationMapper;
import de.unistuttgart.crosswordbackend.mapper.QuestionMapper;
import de.unistuttgart.crosswordbackend.repositories.ConfigurationRepository;
import de.unistuttgart.crosswordbackend.service.ConfigService;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
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
    JWTValidatorService jwtValidatorService;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    ConfigurationMapper configurationMapper;

    @Autowired
    ConfigService configService;

    @GetMapping("")
    public List<ConfigurationDTO> getConfigurations(@CookieValue("access_token") final String accessToken) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get all configurations");
        return configurationMapper.configurationsToConfigurationDTOs(configurationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ConfigurationDTO getConfiguration(
            @CookieValue("access_token") final String accessToken,
            @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get configuration {}", id);
        return configurationMapper.configurationToConfigurationDTO(configService.getConfiguration(id));
    }

    @GetMapping("/{id}/volume")
    public ConfigurationDTO getAllConfiguration(
            @CookieValue("access_token") final String accessToken,
            @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get configuration {}", id);
        return configurationMapper.configurationToConfigurationDTO(
                configService.getAllConfigurations(id, accessToken)
        );
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ConfigurationDTO createConfiguration(
        @RequestBody final ConfigurationDTO configurationDTO,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("create configuration {}", configurationDTO);
        return configService.saveConfiguration(configurationDTO);
    }

    @PutMapping("/{id}")
    public ConfigurationDTO updateConfiguration(
        @PathVariable final UUID id,
        @RequestBody final ConfigurationDTO configurationDTO,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("update configuration {} with {}", id, configurationDTO);
        return configService.updateConfiguration(id, configurationDTO);
    }

    @DeleteMapping("/{id}")
    public ConfigurationDTO deleteConfiguration(
        @PathVariable final UUID id,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("delete configuration {}", id);
        return configService.deleteConfiguration(id);
    }

    @PostMapping("/{id}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionDTO addQuestionToConfiguration(
        @PathVariable final UUID id,
        @RequestBody final QuestionDTO questionDTO,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("add question {} to configuration {}", questionDTO, id);
        return configService.addQuestionToConfiguration(id, questionDTO);
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    public QuestionDTO removeQuestionFromConfiguration(
        @PathVariable final UUID id,
        @PathVariable final UUID questionId,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("remove question {} from configuration {}", questionId, id);
        return configService.removeQuestionFromConfiguration(id, questionId);
    }

    @PutMapping("/{id}/questions/{questionId}")
    public QuestionDTO updateQuestionFromConfiguration(
        @PathVariable final UUID id,
        @PathVariable final UUID questionId,
        @RequestBody final QuestionDTO questionDTO,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("update question {} with {} for configuration {}", questionId, questionDTO, id);
        return configService.updateQuestionFromConfiguration(id, questionId, questionDTO);
    }

    @PostMapping("/{id}/clone")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID cloneConfiguration(@CookieValue("access_token") final String accessToken, @PathVariable final UUID id) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, List.of("lecturer"));
        return configService.cloneConfiguration(id);
    }
}
