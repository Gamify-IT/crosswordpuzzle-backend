package de.unistuttgart.crosswordbackend.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class Crossword {

  private final List<List<String>> crosswordPuzzle = new ArrayList<>();
  private static final String EMPTY_TILE = "empty";
  private Set<Question> questions;

  private List<String> answer = new ArrayList<>();

  public Crossword() {
    addColumnRight();
  }

  public Crossword(final int columns, final int rows) {
    for (int i = 0; i < rows; i++) {
      addColumnRight();
    }
    for (int i = 0; i < columns; i++) {
      addRowBottom();
    }
  }

  public void addRowTop() {
    crosswordPuzzle.forEach(list -> list.add(0, EMPTY_TILE));
  }

  public void addRowBottom() {
    crosswordPuzzle.forEach(list -> list.add(EMPTY_TILE));
  }

  public void addColumnLeft() {
    ArrayList<String> newList = new ArrayList<>();
    for (int i = 0; i < getRows(); i++) {
      newList.add(EMPTY_TILE);
    }
    crosswordPuzzle.add(0, newList);
  }

  public void addColumnRight() {
    ArrayList<String> newList = new ArrayList<>();
    for (int i = 0; i < getRows(); i++) {
      newList.add(EMPTY_TILE);
    }
    crosswordPuzzle.add(newList);
  }

  public List<List<String>> getCrosswordPuzzle() {
    return crosswordPuzzle;
  }

  public int getRows() {
    if (!crosswordPuzzle.isEmpty()) {
      return crosswordPuzzle.get(0).size();
    }
    return 0;
  }

  public int getColumns() {
    return crosswordPuzzle.size();
  }

  private void setTile(final String character, final int x, final int y) {
    crosswordPuzzle.get(x).set(y, character);
  }

  public String getTile(final int x, final int y) {
    return crosswordPuzzle.get(x).get(y);
  }

  public void placeWordHorizontal(int x, final int y, final String word) {
    while (x <= 0) {
      addColumnLeft();
      x++;
    }

    while (x + word.length() > getColumns()) {
      addColumnRight();
    }

    setTile("Horizontal", x - 1, y);

    for (int i = 0; i < word.length(); i++) {
      setTile(String.valueOf(word.charAt(i)), x + i, y);
    }
  }

  public void placeWordVertical(final int x, int y, String word) {
    while (y <= 0) {
      addRowTop();
      y++;
    }

    while (y + word.length() > getRows()) {
      addRowBottom();
    }

    setTile("Vertical", x, y - 1);

    for (int i = 0; i < word.length(); i++) {
      setTile(String.valueOf(word.charAt(i)), x, y + i);
    }
  }

  public boolean checkInBounds(final int x, final int y) {
    if (x < 0 || x >= getColumns()) {
      return false;
    }
    return y < 0 && y >= getRows();
  }
}
