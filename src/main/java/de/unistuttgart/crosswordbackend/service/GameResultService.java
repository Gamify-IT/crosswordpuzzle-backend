package de.unistuttgart.crosswordbackend.service;

import de.unistuttgart.crosswordbackend.clients.ResultClient;
import de.unistuttgart.crosswordbackend.data.GameResultDTO;
import de.unistuttgart.crosswordbackend.data.OverworldResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GameResultService {

    @Autowired
    ResultClient resultClient;

    public void submitGameResult(GameResultDTO gameResult, String userId, String accessToken){
        int score = 100 * gameResult.getCorrectTiles() / gameResult.getNumberOfTiles();
        OverworldResultDTO overworldResultDTO = new OverworldResultDTO("CROSSWORDPUZZLE", gameResult.getConfiguration(), score, userId);
        resultClient.submit(accessToken, overworldResultDTO);
    }
}
