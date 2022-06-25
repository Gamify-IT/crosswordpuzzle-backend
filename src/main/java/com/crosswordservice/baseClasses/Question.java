package com.crosswordservice.baseClasses;


import javax.persistence.*;
@Entity
@Table(name = "questions")
public class Question {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(nullable = false)
    private String level;
    @Column(nullable = false)
    private String question;
    @Column(nullable = false)
    private String answer;




    public Question(String level, String question, String answer) {
        this.level = level;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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
