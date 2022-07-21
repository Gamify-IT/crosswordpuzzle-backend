package com.crosswordservice.service;

import com.crosswordservice.data.Configuration;
import com.crosswordservice.data.ConfigurationDTO;
import com.crosswordservice.data.Question;
import com.crosswordservice.data.QuestionDTO;
import com.crosswordservice.mapper.ConfigurationMapper;
import com.crosswordservice.mapper.QuestionMapper;
import com.crosswordservice.repositories.ConfigurationRepository;
import com.crosswordservice.repositories.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ConfigService {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    ConfigurationMapper configurationMapper;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    QuestionRepository questionRepository;

    /**
     * Search a configuration by given id
     *
     * @throws ResponseStatusException when configuration by configurationName could not be found
     * @param id the id of the configuration searching for
     * @return the found configuration
     */
    public Configuration getConfiguration(final UUID id) {
        return configurationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no configuration with id %s.", id))
                );
    }

    /**
     * Save a configuration
     *
     * @param configurationDTO configuration that should be saved
     * @return the saved configuration as DTO
     */
    public ConfigurationDTO saveConfiguration(final ConfigurationDTO configurationDTO) {
        final Configuration savedConfiguration = configurationRepository.save(
                configurationMapper.configurationDTOToConfiguration(configurationDTO)
        );
        return configurationMapper.configurationToConfigurationDTO(savedConfiguration);
    }

    /**
     * Update a configuration
     *
     * @throws ResponseStatusException when configuration with the id does not exist
     * @param id the id of the configuration that should be updated
     * @param configurationDTO configuration that should be updated
     * @return the updated configuration as DTO
     */
    public ConfigurationDTO updateConfiguration(final UUID id, final ConfigurationDTO configurationDTO) {
        final Configuration configuration = getConfiguration(id);
        configuration.setQuestions(questionMapper.questionDTOsToQuestions(configurationDTO.getQuestions()));
        configuration.setName(configurationDTO.getName());
        final Configuration updatedConfiguration = configurationRepository.save(configuration);
        return configurationMapper.configurationToConfigurationDTO(updatedConfiguration);
    }

    /**
     * Delete a configuration
     *
     * @throws ResponseStatusException when configuration with the id does not exist
     * @param id the id of the configuration that should be updated
     * @return the deleted configuration as DTO
     */
    public ConfigurationDTO deleteConfiguration(final UUID id) {
        final Configuration configuration = getConfiguration(id);
        configurationRepository.delete(configuration);
        return configurationMapper.configurationToConfigurationDTO(configuration);
    }

    /**
     * Add a question to specific configuration
     *
     * @throws ResponseStatusException when configuration with the id does not exist
     * @param id the id of the configuration where a question should be added
     * @param questionDTO the question that should be added
     * @return the added question as DTO
     */
    public QuestionDTO addQuestionToConfiguration(final UUID id, final QuestionDTO questionDTO) {
        final Configuration configuration = getConfiguration(id);
        final Question question = questionRepository.save(questionMapper.questionDTOToQuestion(questionDTO));
        configuration.addQuestion(question);
        configurationRepository.save(configuration);
        return questionMapper.questionToQuestionDTO(question);
    }

    /**
     * Delete a question from a specific configuration
     *
     * @throws ResponseStatusException when configuration with the id or question with id does not exist
     * @param id the id of the configuration where a question should be removed
     * @param questionId the id of the question that should be deleted
     * @return the deleted question as DTO
     */
    public QuestionDTO removeQuestionFromConfiguration(final UUID id, final UUID questionId) {
        final Configuration configuration = getConfiguration(id);
        final Question question = getQuestionInConfiguration(questionId, configuration)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("Question with ID %s does not exist in configuration %s.", questionId, configuration)
                        )
                );
        configuration.removeQuestion(question);
        configurationRepository.save(configuration);
        questionRepository.delete(question);
        return questionMapper.questionToQuestionDTO(question);
    }

    /**
     * Update a question from a specific configuration
     *
     * @throws ResponseStatusException when configuration with the id or question with id does not exist
     * @param id the id of the configuration where a question should be updated
     * @param questionId the id of the question that should be updated
     * @param questionDTO the content of the question that should be updated
     * @return the updated question as DTO
     */
    public QuestionDTO updateQuestionFromConfiguration(
            final UUID id,
            final UUID questionId,
            final QuestionDTO questionDTO
    ) {
        final Configuration configuration = getConfiguration(id);
        if (getQuestionInConfiguration(questionId, configuration).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Question with ID %s does not exist in configuration %s.", questionId, configuration)
            );
        }
        final Question question = questionMapper.questionDTOToQuestion(questionDTO);
        question.setId(questionId);
        final Question savedQuestion = questionRepository.save(question);
        return questionMapper.questionToQuestionDTO(savedQuestion);
    }

    /**
     *
     * @throws ResponseStatusException when question with the id in the given configuration does not exist
     * @param questionId id of searched question
     * @param configuration configuration in which the question is part of
     * @return an optional of the question
     */
    private Optional<Question> getQuestionInConfiguration(final UUID questionId, final Configuration configuration) {
        return configuration
                .getQuestions()
                .parallelStream()
                .filter(filteredQuestion -> filteredQuestion.getId().equals(questionId))
                .findAny();
    }
}
