package de.unistuttgart.crosswordbackend.data;

import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameResultDTO {

    Set<Question> wrongQuestions;
    Set<Question> correctQuestions;

    long duration;
    int correctTiles;
    int numberOfTiles;
    UUID configuration;
}
