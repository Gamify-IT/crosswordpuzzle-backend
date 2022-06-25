package com.crosswordservice.crosswordchecker;

import com.crosswordservice.baseClasses.Crossword;
import com.crosswordservice.baseClasses.Intersection;
import com.crosswordservice.baseClasses.Question;

import java.util.*;

public class GenerateCrossword {
    private int interations = 10;
    private int score = Integer.MIN_VALUE;
    private int startRows = 10;
    private int startColumns = 10;
    private int maxTries = 100;
    private Random r = new Random();
    public GenerateCrossword(){

    }

    public Crossword generateCrossword(List<Question> questions){
        Crossword crossword = new Crossword(startRows,startColumns);
        for(int i = 0; i < interations; i++) {
            createCrossword(crossword, questions);
        }
        return crossword;
    }

    private void createCrossword(Crossword crossword, List<Question> questions) {
        ArrayList<String> answers = new ArrayList<>();
        questions.forEach(question -> {
            answers.add(question.getAnswer().toUpperCase());
        });
        crossword.setAnswer(answers);
        crossword.setQuestions((ArrayList<Question>) questions);
        Crossword currentCrossword = simpleCrossword(answers);
        int currentScore = getScore(currentCrossword);
        if(currentScore > score) {
            if (answers.size() == 0) {
                score = currentScore;
                crossword = currentCrossword;
            }
        }
    }

    private Crossword simpleCrossword(ArrayList<String> answers){
        Crossword crossword = new Crossword(startColumns, startRows);
        int indexOfCurrentAnswer = 0;
        if(answers.size()>1) {
            indexOfCurrentAnswer = r.nextInt(0, answers.size() - 1);
        }
        crossword.placeWordHorizontal(0,0,answers.get(indexOfCurrentAnswer));
        answers.remove(indexOfCurrentAnswer);
        int tries = 0;
        while(answers.size()>0 && tries < maxTries){
            indexOfCurrentAnswer = 0;
            if(answers.size()>1){
                indexOfCurrentAnswer = r.nextInt(0,answers.size()-1);
            }
            String currentAnswer = answers.get(indexOfCurrentAnswer);
            tries++;
            boolean placedWord = tryPlaceWord(currentAnswer,crossword);
            if(placedWord){
                answers.remove(indexOfCurrentAnswer);
                tries = 0;
            }
        }
        return crossword;
    }

    private boolean tryPlaceWord(String word, Crossword crossword) {
        ArrayList<Intersection> intersections = getIntersections(word, crossword);
        while(intersections.size()>0){
            int indexOfCurrentIntersection = 0;
            if(intersections.size()>1){
                indexOfCurrentIntersection = r.nextInt(0,intersections.size()-1);
            }
            Intersection currentIntersection = intersections.get(indexOfCurrentIntersection);
            intersections.remove(indexOfCurrentIntersection);
            if(tryPlaceHorizontal(currentIntersection, word, crossword)){
                return true;
            }

            if(tryPlaceVertical(currentIntersection, word, crossword)){
                return true;
            }
        }
        return false;
    }

    private boolean tryPlaceVertical(Intersection intersection, String word, Crossword crossword){
        ArrayList<String> characters = new ArrayList<>(Arrays.asList(word.split("")));
        for(int i = 0; i<characters.size(); i++){
            int currentX = intersection.getX();
            int currentY = intersection.getY()-intersection.getPositionInWord()+i;
            if(crossword.checkInBounds(currentX, currentY)) {
                if(!(characters.get(i).equals(crossword.getTile(currentX,currentY)))){
                    if(!(crossword.getTile(currentX,currentY).equals("empty"))){
                        return false;
                    }
                }
            }
        }
        if(intersection.getY()-intersection.getPositionInWord()-1>= 0){
            if(!(crossword.getTile(intersection.getX(),intersection.getY() - intersection.getPositionInWord() -1).equals("empty"))){
                return false;
            }
        }
        crossword.placeWordVertical(intersection.getX(), intersection.getY()-intersection.getPositionInWord(), word);
        return true;
    }

    private boolean tryPlaceHorizontal(Intersection intersection, String word, Crossword crossword){
        ArrayList<String> characters = new ArrayList<>(Arrays.asList(word.split("")));
        for(int i = 0; i<characters.size(); i++){
            int currentX = intersection.getX()-intersection.getPositionInWord()+i;
            int currentY = intersection.getY();
            if(crossword.checkInBounds(currentX, currentY)) {
                if(!(characters.get(i).equals(crossword.getTile(currentX,currentY)))){
                    if(!(crossword.getTile(currentX,currentY).equals("empty"))){
                        return false;
                    }
                }
            }
        }
        if(intersection.getX()-intersection.getPositionInWord()-1>= 0){
            if(!(crossword.getTile(intersection.getX() - intersection.getPositionInWord() -1,intersection.getY()).equals("empty"))){
                return false;
            }
        }
        crossword.placeWordHorizontal(intersection.getX()-intersection.getPositionInWord(), intersection.getY(), word);
        return true;
    }

    private ArrayList<Intersection> getIntersections(String word, Crossword crossword){
        ArrayList<Intersection> intersections = new ArrayList<>();
        for(int i = 0; i<word.length(); i++){
            for(int x = 0; x< crossword.getColumns(); x++){
                for(int y = 0; y< crossword.getRows(); y++){
                    if(crossword.getTile(x,y).equals(String.valueOf(word.charAt(i)))){
                        Intersection currentIntersection = new Intersection(x,y,i);
                        intersections.add(currentIntersection);
                    }
                }
            }
        }
        intersections.forEach(currentIntersection -> {
            if(checkIntesection(word, currentIntersection, crossword)){
                intersections.remove(currentIntersection);
            }
        });
        return intersections;
    }

    private boolean checkIntesection(String word, Intersection intersection, Crossword crossword){
        for(int i = Math.max(intersection.getX()-intersection.getPositionInWord(),0); i< intersection.getX()+word.length()-intersection.getPositionInWord();i++){
            if(String.valueOf(word.charAt(i-intersection.getX()+intersection.getPositionInWord())).equals(crossword.getTile(i,intersection.getY()))){
                return false;
            }
        }
        for(int i = Math.max(intersection.getY()-intersection.getPositionInWord(),0); i< intersection.getY()+word.length()-intersection.getPositionInWord();i++){
            if(String.valueOf(word.charAt(i-intersection.getY()+intersection.getPositionInWord())).equals(crossword.getTile(intersection.getX(),i))){
                return false;
            }
        }
        return true;
    }

    private int getScore(Crossword crossword){
        int score = 0;
        score += (crossword.getQuestions().size()-crossword.getAnswer().size())*100;
        return score;
    }
}
