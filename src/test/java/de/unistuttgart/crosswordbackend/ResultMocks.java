package de.unistuttgart.crosswordbackend;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ResultMocks {

  public static void setupMockBooksResponse(final WireMockServer mockService) {
    mockService.stubFor(
      WireMock
        .post(WireMock.urlEqualTo("/internal/submit-game-pass"))
        .willReturn(
          WireMock
            .aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )
    );
  }
}
