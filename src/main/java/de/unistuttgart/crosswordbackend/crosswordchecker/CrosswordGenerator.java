package de.unistuttgart.crosswordbackend.crosswordchecker;

import de.unistuttgart.crosswordbackend.data.Crossword;
import de.unistuttgart.crosswordbackend.data.Intersection;
import de.unistuttgart.crosswordbackend.data.Question;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CrosswordGenerator {

  /**
   * Count of crosswords generated and compared by getScore() function
   */
  private static final int iterations = 10;
  private static final int startRows = 10;
  private static final int startColumns = 10;

  private static final String EMPTY = "empty";

  private final SecureRandom random = new SecureRandom();

  /**
   * Count of tries of the algorithm to place a word.
   */
  private static final int MAX_TRIES = 100;

  /**
   * Genereates a crossword with the given questions
   * @param questions Questions to create a crossword-puzzle with
   * @return a Crossword-puzzle
   */
  public Crossword generateCrossword(Set<Question> questions) {
    Crossword crossword = createCrossword(questions);
    for (int i = 0; i < iterations; i++) {
      Crossword currentCrossword = createCrossword(questions);
      int crosswordScore = getScore(crossword);
      int currentCrosswordScore = getScore(currentCrossword);
      if (currentCrosswordScore > crosswordScore && currentCrossword.getAnswer().isEmpty()) {
        crossword = currentCrossword;
      }
    }

    return crossword;
  }

  /**
   * Creates a crossword with the given questions (and answers).
   *
   * @param questions the questions to include
   * @return the generated crossword
   */
  private Crossword createCrossword(Set<Question> questions) {
    List<String> answers = questions
      .parallelStream()
      .map(Question::getAnswer)
      .map(String::toUpperCase)
      .collect(Collectors.toCollection(ArrayList::new));
    Crossword crossword = generateCrossword(answers);
    crossword.setQuestions(questions);
    crossword.setAnswer(answers);
    return crossword;
  }

  /**
   * Simple algorithm generate a crossword out of given answers.
   *
   * @param answers list of answers which will be placed in a crossword if possible
   * @return crossword with the given answers
   */
  private Crossword generateCrossword(List<String> answers) {
    Crossword crossword = new Crossword(startColumns, startRows);
    int indexOfCurrentAnswer = 0;
    if (answers.size() > 1) {
      indexOfCurrentAnswer = randomNextInt(0, answers.size() - 1);
    }
    crossword.placeWordHorizontal(0, 0, answers.get(indexOfCurrentAnswer));
    answers.remove(indexOfCurrentAnswer);
    int tries = 0;
    while (!answers.isEmpty() && tries < MAX_TRIES) {
      indexOfCurrentAnswer = 0;
      if (answers.size() > 1) {
        indexOfCurrentAnswer = randomNextInt(0, answers.size() - 1);
      }
      String currentAnswer = answers.get(indexOfCurrentAnswer);
      tries++;
      boolean placedWord = tryPlaceWord(currentAnswer, crossword);
      if (placedWord) {
        answers.remove(indexOfCurrentAnswer);
        tries = 0;
      }
    }
    return crossword;
  }

  /**
   * Tries to place a word on the given crossword.
   * @param word word which should be placed if possible
   * @param crossword where the word should be placed
   * @return whether the word has been placed
   */
  private boolean tryPlaceWord(String word, Crossword crossword) {
    List<Intersection> intersections = getIntersections(word, crossword);
    while (!intersections.isEmpty()) {
      int indexOfCurrentIntersection = 0;
      if (intersections.size() > 1) {
        indexOfCurrentIntersection = randomNextInt(0, intersections.size() - 1);
      }
      Intersection currentIntersection = intersections.get(indexOfCurrentIntersection);
      intersections.remove(indexOfCurrentIntersection);
      if (
        tryPlaceHorizontal(currentIntersection, word, crossword) ||
        tryPlaceVertical(currentIntersection, word, crossword)
      ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tries to place a word vertical with the given intersection.
   * @param intersection intersection on which the word should be placed if possible
   * @param word word which should be placed
   * @param crossword crossword where the world should be placed
   * @return whether the word has been placed
   */
  private boolean tryPlaceVertical(Intersection intersection, String word, Crossword crossword) {
    ArrayList<String> characters = new ArrayList<>(Arrays.asList(word.split("")));
    //Check if word collides with a word placed on the crossword
    for (int i = 0; i < characters.size(); i++) {
      int currentX = intersection.getX();
      int currentY = intersection.getY() - intersection.getPositionInWord() + i;
      if (
        crossword.checkInBounds(currentX, currentY) &&
        !(characters.get(i).equals(crossword.getTile(currentX, currentY))) &&
        !(crossword.getTile(currentX, currentY).equals(EMPTY))
      ) {
        return false;
      }
    }
    //check if the position in front of the word is empty
    if (
      intersection.getY() - intersection.getPositionInWord() - 1 >= 0 &&
      !(
        crossword.getTile(intersection.getX(), intersection.getY() - intersection.getPositionInWord() - 1).equals(EMPTY)
      )
    ) {
      return false;
    }
    crossword.placeWordVertical(intersection.getX(), intersection.getY() - intersection.getPositionInWord(), word);
    return true;
  }

  /**
   * Tries to place a word horizontal with the given intersection.
   * @param intersection intersection on which the word should be placed if possible
   * @param word word which should be placed
   * @param crossword where the world should be placed
   * @return whether the word has been placed
   */
  private boolean tryPlaceHorizontal(Intersection intersection, String word, Crossword crossword) {
    List<String> characters = new ArrayList<>(Arrays.asList(word.split("")));
    //Check if word collides with a word placed on the crossword
    for (int i = 0; i < characters.size(); i++) {
      int currentX = intersection.getX() - intersection.getPositionInWord() + i;
      int currentY = intersection.getY();
      if (
        crossword.checkInBounds(currentX, currentY) &&
        !(characters.get(i).equals(crossword.getTile(currentX, currentY))) &&
        !(crossword.getTile(currentX, currentY).equals(EMPTY))
      ) {
        return false;
      }
    }
    //check if the position in front of the word is empty
    if (
      intersection.getX() - intersection.getPositionInWord() - 1 >= 0 &&
      !(
        crossword.getTile(intersection.getX() - intersection.getPositionInWord() - 1, intersection.getY()).equals(EMPTY)
      )
    ) {
      return false;
    }
    crossword.placeWordHorizontal(intersection.getX() - intersection.getPositionInWord(), intersection.getY(), word);
    return true;
  }

  /**
   * Calculates the intersections of the given word with the words on the crossword.
   * @param word word where the intersections will be calculated
   * @param crossword crossword on which the intersections will be calculated
   * @return a list of intersections for the given word and crossword-puzzle.
   */
  private List<Intersection> getIntersections(String word, Crossword crossword) {
    List<Intersection> intersections = new ArrayList<>();
    for (int i = 0; i < word.length(); i++) {
      for (int x = 0; x < crossword.getColumns(); x++) {
        for (int y = 0; y < crossword.getRows(); y++) {
          if (crossword.getTile(x, y).equals(String.valueOf(word.charAt(i)))) {
            Intersection currentIntersection = new Intersection(x, y, i);
            intersections.add(currentIntersection);
          }
        }
      }
    }
    intersections.removeIf(intersection -> checkIntersection(word, intersection, crossword));

    return intersections;
  }

  /**
   * Checks, if the intersection is blocked by other words on the crossword-puzzle.
   * @param word word for the given intersection.
   * @param intersection intersection which should be checked.
   * @param crossword crossword which should be checked.
   * @return true if the intersection is possible.
   */
  private boolean checkIntersection(String word, Intersection intersection, Crossword crossword) {
    //Check intersection horizontal
    for (
      int i = Math.max(intersection.getX() - intersection.getPositionInWord(), 0);
      i < intersection.getX() + word.length() - intersection.getPositionInWord();
      i++
    ) {
      if (
        String
          .valueOf(word.charAt(i - intersection.getX() + intersection.getPositionInWord()))
          .equals(crossword.getTile(i, intersection.getY()))
      ) {
        return false;
      }
    }
    //Check intersection vertical
    for (
      int i = Math.max(intersection.getY() - intersection.getPositionInWord(), 0);
      i < intersection.getY() + word.length() - intersection.getPositionInWord();
      i++
    ) {
      if (
        String
          .valueOf(word.charAt(i - intersection.getY() + intersection.getPositionInWord()))
          .equals(crossword.getTile(intersection.getX(), i))
      ) {
        return false;
      }
    }
    return true;
  }

  /**
   * calculates a score for a crossword based on the amount of words placed on the crossword
   * @param crossword crossword which score should be calculated.
   * @return score of the crossword
   */
  private int getScore(Crossword crossword) {
    int score = 0;
    score += (crossword.getQuestions().size() - crossword.getAnswer().size()) * 100;
    return score;
  }

  private int randomNextInt(int min, int max) {
    return random.nextInt(max - min) + min;
  }
}
