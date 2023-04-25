package de.unistuttgart.crosswordbackend.mapper;

import de.unistuttgart.crosswordbackend.data.GameAnswer;
import de.unistuttgart.crosswordbackend.data.GameAnswerDTO;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameAnswerMapper {
    GameAnswerDTO gameAnswerToGameAnswerDTO(final GameAnswer gameAnswer);

    GameAnswer gameAnswerDTOToGameAnswer(final GameAnswerDTO gameAnswerDTO);

    Set<GameAnswerDTO> gameAnswersToGameAnswerDTOs(final Set<GameAnswer> gameAnswers);
}
