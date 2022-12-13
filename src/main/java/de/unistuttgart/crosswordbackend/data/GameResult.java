package de.unistuttgart.crosswordbackend.data;

import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameResult {

    /**
     * A unique identifier for the game result.
     */
    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    long duration;
    int correctTiles;
    int numberOfTiles;

    UUID configuration;

    @ManyToMany
    Set<Question> wrongQuestions;

    @ManyToMany
    Set<Question> correctQuestions;

    public GameResult(
        final long duration,
        final int correctTiles,
        final int numberOfTiles,
        final UUID configuration,
        final Set<Question> wrongQuestions,
        final Set<Question> correctQuestions
    ) {
        this.duration = duration;
        this.correctTiles = correctTiles;
        this.numberOfTiles = numberOfTiles;
        this.configuration = configuration;
        this.wrongQuestions = wrongQuestions;
        this.correctQuestions = correctQuestions;
    }
}
