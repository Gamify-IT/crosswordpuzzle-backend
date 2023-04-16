package de.unistuttgart.crosswordbackend.mapper;

import de.unistuttgart.crosswordbackend.data.GameResult;
import de.unistuttgart.crosswordbackend.data.GameResultDTO;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameResultMapper {
    GameResultDTO gameResultToGameResultDTO(final GameResult gameResult);

    GameResult gameResultDTOToGameResult(final GameResultDTO gameResultDTO);

    Set<GameResult> gameResultDTOsToGameResults(final Set<GameResultDTO> gameResultDTOs);
}
