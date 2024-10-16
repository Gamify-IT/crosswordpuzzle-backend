package de.unistuttgart.crosswordbackend.data;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

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

    /**
     * duration in seconds
     */
    long duration;
    int correctTiles;
    int numberOfTiles;

    String playerId;

    UUID configuration;

    @CreationTimestamp
    Date timeOfRun;

    @OneToMany(cascade = CascadeType.ALL)
    Set<GameAnswer> answers;

    /**
     * The score achieved in the game.
     */
    int score;

    /**
     * The reward-coins that the player achieved in the current round.
     */
    int rewards;

    public GameResult(
        final long duration,
        final int correctTiles,
        final int numberOfTiles,
        final UUID configuration,
        final Set<GameAnswer> answers
    ) {
        this.duration = duration;
        this.correctTiles = correctTiles;
        this.numberOfTiles = numberOfTiles;
        this.configuration = configuration;
        this.answers = answers;
    }
}
