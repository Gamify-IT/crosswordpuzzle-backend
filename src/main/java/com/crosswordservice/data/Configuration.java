package com.crosswordservice.data;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

/**
 * Configuration for a crosswordpuzzle with a name
 */
@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Configuration {
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;
    @Column(
            nullable = false,
            unique = true
    )
    String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<Question> questions;

    public Configuration(final String name, final Set<Question> questions){
        this.name = name;
        this.questions = questions;
    }

    public void addQuestion(final Question question) {
        this.questions.add(question);
    }

    public void removeQuestion(final Question question) {
        this.questions.remove(question);
    }
}
