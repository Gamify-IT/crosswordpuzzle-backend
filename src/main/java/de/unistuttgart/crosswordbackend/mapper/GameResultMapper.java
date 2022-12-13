package de.unistuttgart.crosswordbackend.mapper;

import de.unistuttgart.crosswordbackend.data.GameResult;
import de.unistuttgart.crosswordbackend.data.GameResultDTO;
import de.unistuttgart.crosswordbackend.data.Question;
import de.unistuttgart.crosswordbackend.data.QuestionDTO;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface GameResultMapper {
    GameResultDTO gameResultToGameResultDTO(final GameResult gameResult);

    GameResult gameResultDTOToGameResult(final GameResultDTO gameResultDTO);

    Set<GameResult> gameResultDTOsToGameResults(final Set<GameResultDTO> gameResultDTOs);
}
