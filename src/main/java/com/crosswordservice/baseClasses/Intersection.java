package com.crosswordservice.baseClasses;

public class Intersection {
    private int x;
    private int y;
    private int positionInWord;
    public Intersection(int x, int y, int positionInWord){
        this.x = x;
        this.y = y;
        this.positionInWord = positionInWord;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPositionInWord() {
        return positionInWord;
    }
}
