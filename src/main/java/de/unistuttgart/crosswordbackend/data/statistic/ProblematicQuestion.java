package de.unistuttgart.crosswordbackend.data.statistic;

import de.unistuttgart.crosswordbackend.data.QuestionDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProblematicQuestion {

    int attempts;
    int correctAnswers;
    int wrongAnswers;

    QuestionDTO question;

    public ProblematicQuestion(final QuestionDTO question) {
        this.question = question;
    }

    public void addCorrectAnswer() {
        correctAnswers++;
        attempts++;
    }

    public void addWrongAnswer() {
        wrongAnswers++;
        attempts++;
    }
}
