package com.crosswordservice.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.UUID;

import static javax.persistence.GenerationType.SEQUENCE;

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
}