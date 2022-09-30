package de.unistuttgart.crosswordbackend.data;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OverworldResultDTO {

    static final String GAME = "CROSSWORDPUZZLE";
    UUID configurationId;
    long score;
    String userId;
}
