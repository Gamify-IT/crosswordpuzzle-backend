package de.unistuttgart.crosswordbackend.crosswordchecker;

import de.unistuttgart.crosswordbackend.data.Crossword;
import de.unistuttgart.crosswordbackend.data.Question;
import java.util.List;
import java.util.Set;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CrosswordChecker {

  /**
   * Checks if a crossword with the given questions can be created.
   *
   * @param questions the questions to include
   * @return whether all answers are placed on the crossword-puzzle
   */
  public boolean checkCrossword(Set<Question> questions) {
    CrosswordGenerator crosswordGenerator = new CrosswordGenerator();
    Crossword crossword = crosswordGenerator.generateCrossword(questions);
    return crossword.getAnswer().isEmpty();
  }
}
