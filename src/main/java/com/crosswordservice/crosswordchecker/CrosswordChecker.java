package com.crosswordservice.crosswordchecker;

import com.crosswordservice.baseClasses.Crossword;
import com.crosswordservice.baseClasses.Question;

import java.util.List;

public class CrosswordChecker {
    public CrosswordChecker(){

    }

    public boolean checkCrossword(List<Question> questions){
        GenerateCrossword crosswordGenerator = new GenerateCrossword();
        Crossword crossword = crosswordGenerator.generateCrossword(questions);
        if(crossword.getAnswer().size()==0){
            return true;
        }
        return false;
    }

}
