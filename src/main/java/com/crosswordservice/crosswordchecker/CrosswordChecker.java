package com.crosswordservice.crosswordchecker;

import com.crosswordservice.data.Crossword;
import com.crosswordservice.data.Question;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class CrosswordChecker {
    /**
     * creates a crossword with the given questions and return if every answer is placed
     * on the crossword-puzzle
     * @param questions questions with answers for a crossword-puzzle
     * @return if all answers are placed on the crossword-puzzle return true else return false
     */
    public boolean checkCrossword(List<Question> questions){
        CrosswordGenerator crosswordGenerator = new CrosswordGenerator();
        Crossword crossword = crosswordGenerator.generateCrossword(questions);
        return crossword.getAnswer().isEmpty();
    }

}
