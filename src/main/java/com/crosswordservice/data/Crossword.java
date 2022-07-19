package com.crosswordservice.data;

import java.util.ArrayList;
import java.util.List;

/**
 * creates a Crosswordpuzzle with the needed funtions  as a 2d String array
 */
public class Crossword {
    private final ArrayList<ArrayList<String>> crosswordPuzzle = new ArrayList<>();
    private static final String emptyTile = "empty";
    private List<Question> questions = new ArrayList<>();

    private List<String> answer = new ArrayList<>();
    public Crossword() {
        addColumnRight();
    }

    public Crossword(int columns, int rows){
        for(int i = 0; i<rows; i++){
            addColumnRight();
        }
        for(int i = 0; i<columns; i++){
            addRowBottom();
        }
    }

    public void addRowTop(){
        crosswordPuzzle.forEach(list ->
            list.add(0,emptyTile)
        );
    }
    public void addRowBottom(){
        crosswordPuzzle.forEach(list ->
            list.add(emptyTile)
        );
    }
    public void addColumnLeft(){

        ArrayList<String> newList = new ArrayList<>();
        for(int i = 0; i<getRows(); i++){
            newList.add(emptyTile);
        }
        crosswordPuzzle.add(0,newList);
    }
    public void addColumnRight(){
        ArrayList<String> newList = new ArrayList<>();
        for(int i = 0; i<getRows(); i++){
            newList.add(emptyTile);
        }
        crosswordPuzzle.add(newList);
    }

    public List<ArrayList<String>> getCrosswordPuzzle(){
        return crosswordPuzzle;
    }

    public int getRows(){
        if(crosswordPuzzle.isEmpty()){
            return crosswordPuzzle.get(0).size();
        }
        return 0;
    }

    public int getColumns(){
        return crosswordPuzzle.size();
    }

    private void setTile(String character, int x, int y){
        crosswordPuzzle.get(x).set(y,character);
    }

    public String getTile(int x, int y){
        return crosswordPuzzle.get(x).get(y);
    }

    public void placeWordHorizontal(int x, int y, String word){
        while(x<=0){
            addColumnLeft();
            x++;
        }

        while(x+word.length()>getColumns()){
            addColumnRight();
        }

        setTile("Horizontal",x-1,y);

        for(int i = 0; i<word.length() ; i++){
            setTile(String.valueOf(word.charAt(i)),x+i,y);
        }
    }

    public void placeWordVertical(int x, int y, String word) {
        while(y<=0){
            addRowTop();
            y++;
        }

        while(y+word.length()>getRows()){
            addRowBottom();
        }

        setTile("Vertical",x,y-1);

        for(int i = 0; i<word.length() ; i++){
            setTile(String.valueOf(word.charAt(i)),x,y+i);
        }
    }

    public boolean checkInBounds(int x, int y){
        if(x<0 || x>= getColumns()){
            return false;
        }
        return y<0 && y>= getRows();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }
}
