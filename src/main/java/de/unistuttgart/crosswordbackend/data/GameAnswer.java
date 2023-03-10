package de.unistuttgart.crosswordbackend.data;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameAnswer {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    String answer;
    String correctAnswer;
    String question;
    boolean correct;
}
