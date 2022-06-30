package com.crosswordservice.baseClasses;

import javax.persistence.*;
import static javax.persistence.GenerationType.SEQUENCE;
@Entity
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
    private String question;
    @Column(nullable = false)
    private String answer;

    public Question(int internalId, String question, String answer) {
        this.internalId = internalId;
        this.question = question;
        this.answer = answer;
    }

    public Question() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
