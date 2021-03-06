package de.unistuttgart.crosswordbackend.data;

import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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

  @Column(nullable = false)
  String name;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  Set<Question> questions;

  public Configuration(final String name, final Set<Question> questions) {
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
