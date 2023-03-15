package de.unistuttgart.crosswordbackend.data;

import java.util.UUID;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Class to save a question with the id of the configuration, the question
 *  and the answer
 */
@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @Column(nullable = false)
    String questionText;

    @Column(nullable = false)
    String answer;

    public Question(final String questionText, final String answer) {
        this.questionText = questionText;
        this.answer = answer;
    }

    @Override
    public Object clone() {
        return new Question(this.questionText, this.answer);
    }
}
