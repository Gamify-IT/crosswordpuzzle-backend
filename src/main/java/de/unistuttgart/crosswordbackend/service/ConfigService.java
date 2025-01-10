package de.unistuttgart.crosswordbackend.service;

import de.unistuttgart.crosswordbackend.Constants;
import de.unistuttgart.crosswordbackend.clients.OverworldClient;
import de.unistuttgart.crosswordbackend.data.*;
import de.unistuttgart.crosswordbackend.mapper.ConfigurationMapper;
import de.unistuttgart.crosswordbackend.mapper.QuestionMapper;
import de.unistuttgart.crosswordbackend.repositories.ConfigurationRepository;
import de.unistuttgart.crosswordbackend.repositories.QuestionRepository;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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


    @Autowired
    private OverworldClient overworldClient;

    @Autowired
    private JWTValidatorService jwtValidatorService;

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
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("There is no configuration with ID %s.", id)
                )
            );
    }

    /**
     * Search a configuration by given id and get volume level from overworld-backend
     *
     * @param id the id of the configuration searching for
     * @param accessToken the users access token
     * @return the found configuration
     * @throws ResponseStatusException  when configuration by configurationName could not be found
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public Configuration getAllConfigurations(final UUID id, final String accessToken) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        final String userId = jwtValidatorService.extractUserId(accessToken);

        KeybindingDTO keyBindingVolumeLevel = overworldClient.getKeybindingStatistic(userId, "VOLUME_LEVEL", accessToken);
        Integer volumeLevel;
        if (keyBindingVolumeLevel.getKey() == null || keyBindingVolumeLevel.getKey().isEmpty()) {
            volumeLevel = 0;
        }
        else {
            try {
                volumeLevel = Integer.parseInt(keyBindingVolumeLevel.getKey());
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid volume level format.");
            }
        }

        if (id.equals(UUID.fromString(Constants.tutorialUuid))) {
            return getTutorialConfiguration();
        }

        Configuration config = configurationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("There is no configuration with id %s.", id)
                        )
                );
        config.setVolumeLevel(volumeLevel);
        return configurationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("There is no configuration with id %s.", id)
                        )
                );
    }

    /**
     * Creates the game configuration for the tutorial
     *
     * @return tutorial configuration
     */
    private Configuration getTutorialConfiguration() {
        Question one = new Question("What is the abbreviation of the computing unit in a computer?", "CPU");
        one.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Question two = new Question("What is one possible value of a Boolean variable?", "true");
        two.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Question three = new Question("How dou you call the sensitivity of change of a function?", "derivative");
        three.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        Question four = new Question("What are the smallest components that make up a computer called?", "transistors");
        four.setId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
        Question five =  new Question("What does the abbreviation 'OS' stand for?", "operating system");
        five.setId(UUID.fromString("00000000-0000-0000-0000-000000000005"));

        Set<Question> tutorialQuestions = Set.of(one, two, three, four, five);
        Configuration tutorialConfig = new Configuration("tutorial", tutorialQuestions);
        tutorialConfig.setVolumeLevel(1);

        return tutorialConfig;
    }

    /**
     * Save a configuration
     *
     * @param configurationDTO configuration that should be saved
     * @return the saved configuration as DTO
     */
    public ConfigurationDTO saveConfiguration(final ConfigurationDTO configurationDTO) {
        return configurationMapper.configurationToConfigurationDTO(
            configurationRepository.save(configurationMapper.configurationDTOToConfiguration(configurationDTO))
        );
    }

    /**
     * Update a configuration
     *
     * @throws ResponseStatusException (404) when configuration with the id does not exist
     * @param id the id of the configuration that should be updated
     * @param configurationDTO configuration that should be updated
     * @return the updated configuration as DTO
     */
    public ConfigurationDTO updateConfiguration(final UUID id, final ConfigurationDTO configurationDTO) {
        final Configuration configuration = getConfiguration(id);
        configuration.setQuestions(questionMapper.questionDTOsToQuestions(configurationDTO.getQuestions()));
        configuration.setName(configurationDTO.getName());
        return configurationMapper.configurationToConfigurationDTO(configurationRepository.save(configuration));
    }

    /**
     * Delete a configuration
     *
     * @throws ResponseStatusException (404) when configuration with the id does not exist
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
     * @throws ResponseStatusException (404) when configuration with the id does not exist
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
     * @throws ResponseStatusException (404) when configuration with the id or question with id does not exist
     * @param id the id of the configuration where a question should be removed
     * @param questionId the id of the question that should be deleted
     * @return the deleted question as DTO
     */
    public QuestionDTO removeQuestionFromConfiguration(final UUID id, final UUID questionId) {
        final Configuration configuration = getConfiguration(id);
        final Question question = getQuestionInConfiguration(questionId, configuration);
        configuration.removeQuestion(question);
        configurationRepository.save(configuration);
        questionRepository.delete(question);
        return questionMapper.questionToQuestionDTO(question);
    }

    /**
     * Update a question from a specific configuration
     *
     * @throws ResponseStatusException (404) when configuration with the id or question with id does not exist
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
        final Question question = getQuestionInConfiguration(questionId, configuration);
        question.setQuestionText(questionDTO.getQuestionText());
        question.setAnswer(questionDTO.getAnswer());
        return questionMapper.questionToQuestionDTO(questionRepository.save(question));
    }

    /**
     *
     * @throws ResponseStatusException (404) when question with the id in the given configuration does not exist
     * @param questionId id of searched question
     * @param configuration configuration in which the question is part of
     * @return an optional of the question
     */
    private Question getQuestionInConfiguration(final UUID questionId, final Configuration configuration) {
        return configuration
            .getQuestions()
            .parallelStream()
            .filter(filteredQuestion -> filteredQuestion.getId().equals(questionId))
            .findAny()
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Question with ID %s does not exist in configuration %s.", questionId, configuration)
                )
            );
    }

    public UUID cloneConfiguration(final UUID id) {
        final Configuration config = configurationRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Configuration with id %s not found", id)
                )
            );
        final Configuration cloneConfig = config.clone();
        final Configuration idConfig = configurationRepository.save(cloneConfig);
        return idConfig.getId();
    }
}
