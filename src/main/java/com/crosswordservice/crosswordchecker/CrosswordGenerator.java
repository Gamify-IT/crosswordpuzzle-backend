package com.crosswordservice.crosswordchecker;

import com.crosswordservice.baseClasses.Crossword;
import com.crosswordservice.baseClasses.Intersection;
import com.crosswordservice.baseClasses.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.security.SecureRandom;

public class CrosswordGenerator {
    /**
     * Count of crosswords generated and compared by getScore() function
     */
    private int interations = 10;
    private int score = Integer.MIN_VALUE;
    private int startRows = 10;
    private int startColumns = 10;

    private SecureRandom random = new SecureRandom();

    /**
     * Count of tries of the algorithm to place a word.
     */
    private int maxTries = 100;

    public CrosswordGenerator(){

    }

    /**
     * Genereates a crossword with the given questions
     * @param questions Questions to create a crossword-puzzle with
     * @return a Crossword-puzzle
     */
    public Crossword generateCrossword(List<Question> questions){
        Crossword crossword = createCrossword(questions);
        for(int i = 0; i < interations; i++) {
            Crossword currentCrossword = createCrossword(questions);
            int crosswordScore = getScore(crossword);
            int currentCrosswordScore = getScore(currentCrossword);
            if(currentCrosswordScore>crosswordScore){
                if(currentCrossword.getAnswer().size()==0){
                    crossword = currentCrossword;
                }
            }
        }

        return crossword;
    }

    /**
     * Converts the Array from type question to an Array with the type
     * String. The String Array only contains the answers.
     * Then the simpleCrossword algorithm is called.
     * @param questions Question Array who will be converted
     * @return Crossword out of the given questions
     */
    private Crossword createCrossword(List<Question> questions) {
        ArrayList<String> answers = new ArrayList<>();
        questions.forEach(question -> {
            answers.add(question.getAnswer().toUpperCase());
        });
        Crossword crossword = simpleCrossword(answers);
        crossword.setQuestions((ArrayList<Question>) questions);
        crossword.setAnswer(answers);
        return crossword;
    }

    /**
     * Simple algorithm generate a crossword out of given answers.
     * @param answers List of answers which will be placed in a crossword if possible.
     * @return crossword with the given answers.
     */
    private Crossword simpleCrossword(ArrayList<String> answers){
        Crossword crossword = new Crossword(startColumns, startRows);
        int indexOfCurrentAnswer = 0;
        if(answers.size()>1) {
            indexOfCurrentAnswer = random.nextInt(0, answers.size() - 1);
        }
        crossword.placeWordHorizontal(0,0,answers.get(indexOfCurrentAnswer));
        answers.remove(indexOfCurrentAnswer);
        int tries = 0;
        while(answers.size()>0 && tries < maxTries){
            indexOfCurrentAnswer = 0;
            if(answers.size()>1){
                indexOfCurrentAnswer = random.nextInt(0,answers.size()-1);
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

    /**
     * Tries to place a word on the given crossword.
     * @param word word, which should be placed if possible.
     * @param crossword crossword where the word should be placed.
     * @return true if the word has been placed, otherwise false.
     */
    private boolean tryPlaceWord(String word, Crossword crossword) {
        ArrayList<Intersection> intersections = getIntersections(word, crossword);
        while(intersections.size()>0){
            int indexOfCurrentIntersection = 0;
            if(intersections.size()>1){
                indexOfCurrentIntersection = random.nextInt(0,intersections.size()-1);
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

    /**
     * Tries to place a word vertical with the given intersection.
     * @param intersection Intersection, on which the word should be placed if possible.
     * @param word word, which should be placed
     * @param crossword crossword where the world should be placed.
     * @return true if the word has been placed, otherwise false.
     */
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

    /**
     * Tries to place a word horizontal with the given intersection.
     * @param intersection Intersection, on which the word should be placed if possible.
     * @param word word, which should be placed
     * @param crossword crossword where the world should be placed.
     * @return true if the word has been placed, otherwise false.
     */
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

    /**
     * Calculates the intersections of the given word with the words on the crossword.
     * @param word word, where the intersections will be calculated.
     * @param crossword crossword, on which the intersections will be calculated.
     * @return List of intersections of the given word on the given crossword-puzzle.
     */
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

    /**
     * Checks, if the intersection is blocked by other words on the crossword-puzzle.
     * @param word word for the given intersection.
     * @param intersection intersection which should be checked.
     * @param crossword crossword which should be checked.
     * @return true if the intersection is possible.
     */
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

    /**
     * return 0
     * @param crossword crossword which score should be calculated.
     * @return 0
     */
    private int getScore(Crossword crossword){
        int score = 0;
        score += (crossword.getQuestions().size()-crossword.getAnswer().size())*100;
        return score;
    }
}
