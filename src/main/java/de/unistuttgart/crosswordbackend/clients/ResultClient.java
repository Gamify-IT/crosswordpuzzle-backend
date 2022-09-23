package de.unistuttgart.crosswordbackend.clients;

import de.unistuttgart.crosswordbackend.data.OverworldResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * This client's purpose is to send an OverworldResultDTO to the Overworld-Backend when a Player finished a Game.
 */
@FeignClient(value = "resultClient", url = "${overworld.url}/internal")
public interface ResultClient {
  /**
   * Submits the resultDTO to the Overworld-Backend
   *
   * @param resultDTO resultDTO which is sent to the overworld backend
   * @param accessToken access token for the path validation
   */
  @PostMapping("/submit-game-pass")
  void submit(@CookieValue("access_token") final String accessToken, final OverworldResultDTO resultDTO);
}
