package com.crosswordservice.baseClasses;

import java.util.ArrayList;
import java.util.Arrays;

public class Crossword {
    private final ArrayList<ArrayList<String>> crossword = new ArrayList<>();
    private final String emptyTile = "empty";
    private ArrayList<Question> questions = new ArrayList<>();

    private ArrayList<String> answer = new ArrayList<>();
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
        crossword.forEach(list -> {
            list.add(0,emptyTile);
        });
    }
    public void addRowBottom(){
        crossword.forEach(list -> {
            list.add(emptyTile);
        });
    }
    public void addColumnLeft(){

        ArrayList<String> newList = new ArrayList<>();
        for(int i = 0; i<getRows(); i++){
            newList.add(emptyTile);
        }
        crossword.add(0,newList);
    }
    public void addColumnRight(){
        ArrayList<String> newList = new ArrayList<>();
        for(int i = 0; i<getRows(); i++){
            newList.add(emptyTile);
        }
        crossword.add(newList);
    }

    public ArrayList<ArrayList<String>> getCrossword(){
        return crossword;
    }

    public int getRows(){
        if(crossword.size()>0){
            return crossword.get(0).size();
        }
        return 0;
    }

    public int getColumns(){
        return crossword.size();
    }

    private void setTile(String character, int x, int y){
        crossword.get(x).set(y,character);
    }

    public String getTile(int x, int y){
        return crossword.get(x).get(y);
    }

    public void placeWordHorizontal(int x, int y, String word){
        while(x<0){
            addColumnLeft();
            x++;
        }

        while(x+word.length()>getColumns()){
            addColumnRight();
        }
        for(int i = 0; i<word.length() ; i++){
            setTile(String.valueOf(word.charAt(i)),x+i,y);
        }
    }

    public void placeWordVertical(int x, int y, String word) {
        while(y<0){
            addRowTop();
            y++;
        }

        while(y+word.length()>getRows()){
            addRowBottom();
        }
        for(int i = 0; i<word.length() ; i++){
            setTile(String.valueOf(word.charAt(i)),x,y+i);
        }
    }

    public boolean checkInBounds(int x, int y){
        if(x<0 || x>= getColumns()){
            return false;
        }
        if(y<0 || y>= getRows()){
            return false;
        }
        return true;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList<String> answer) {
        this.answer = answer;
    }

    public void print(){
        for(int y = 0; y<getRows(); y++){
            for(int x = 0; x<getColumns(); x++){
                if(getTile(x,y).length()==1){
                    System.out.print("    ");
                }
                System.out.print(getTile(x,y)+";");

            }
            System.out.println();
        }
    }
}
