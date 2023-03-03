package de.unistuttgart.crosswordbackend.service;

import de.unistuttgart.crosswordbackend.clients.ResultClient;
import de.unistuttgart.crosswordbackend.data.*;
import de.unistuttgart.crosswordbackend.mapper.GameResultMapper;
import de.unistuttgart.crosswordbackend.repositories.GameResultRepository;
import feign.FeignException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * This service handles the logic for the GameResultController.class
 */
@Service
@Slf4j
public class GameResultService {

    @Autowired
    ResultClient resultClient;

    @Autowired
    GameResultRepository gameResultRepository;

    @Autowired
    GameResultMapper gameResultMapper;

    /**
     * Creates a OverworldResultDTO and sends it to the overworld backend.
     *
     * @param gameResultDTO extern gameResultDTO
     * @param userId Id of the user
     * @param accessToken accessToken of the user
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public void submitGameResult(final GameResultDTO gameResultDTO, final String userId, final String accessToken) {
        if (gameResultDTO == null || userId == null || accessToken == null) {
            throw new IllegalArgumentException("gameResultDTO or userId is null");
        }
        if (gameResultDTO.getNumberOfTiles() < gameResultDTO.getCorrectTiles()) {
            throw new IllegalArgumentException("number of correct tiles is bigger than the number of tiles");
        }
        final int score = 100 * gameResultDTO.getCorrectTiles() / gameResultDTO.getNumberOfTiles();
        final OverworldResultDTO overworldResultDTO = new OverworldResultDTO(
            gameResultDTO.getConfiguration(),
            score,
            userId
        );
        GameResult gameResult = gameResultMapper.gameResultDTOToGameResult(gameResultDTO);
        gameResult.setUserId(userId);
        gameResultRepository.save(gameResult);

        try {
            resultClient.submit(accessToken, overworldResultDTO);
        } catch (final FeignException.BadGateway badGateway) {
            final String warning =
                "The Overworld backend is currently not available. The result was NOT saved. Please try again later.";
            log.warn(warning, badGateway);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, warning);
        } catch (final FeignException.NotFound notFound) {
            final String warning = String.format("The result could not be saved. Unknown User '%s'.", userId);
            log.warn(warning, notFound);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, warning);
        }
    }
}
