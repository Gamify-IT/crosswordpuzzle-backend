package com.crosswordservice.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


/**
 * Class to save a question with the id of the configuration, the question
 *  and the answer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionDTO {
    UUID id;
    String questionText;
    String answer;

    public QuestionDTO(final String questionText, final String answer) {
        this.questionText = questionText;
        this.answer = answer;
    }
}
