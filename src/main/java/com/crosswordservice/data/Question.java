package com.crosswordservice.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import static javax.persistence.GenerationType.SEQUENCE;

/**
 * Class to save a question with the id of the configuration, the question
 *  and the answer
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "questions")
public class Question {
    @Id
    @SequenceGenerator(
            name = "question_sequence",
            sequenceName = "question_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "question_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @Column(nullable = false)
    private long internalId;
    @Column(nullable = false)
    private String questionText;
    @Column(nullable = false)
    private String answer;

    public Question(long internalId, String questionText, String answer) {
        this.internalId = internalId;
        this.questionText = questionText;
        this.answer = answer;
    }

    public Question(String questionText, String answer) {
        this.questionText = questionText;
        this.answer = answer;
    }
}
