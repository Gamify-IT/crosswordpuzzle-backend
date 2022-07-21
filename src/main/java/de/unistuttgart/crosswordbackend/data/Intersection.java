package de.unistuttgart.crosswordbackend.data;

import lombok.Data;

/**
 * Saves a intersection with a x and a y coordinate and a position in word
 */
@Data
public class Intersection {
    private int x;
    private int y;
    private int positionInWord;
    public Intersection(int x, int y, int positionInWord){
        this.x = x;
        this.y = y;
        this.positionInWord = positionInWord;
    }
}
