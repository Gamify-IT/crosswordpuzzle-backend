package com.crosswordservice.controller;

import com.crosswordservice.data.Question;
import com.crosswordservice.crosswordchecker.CrosswordChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;



/**
 * Rest Controller for the crossword-puzzle backend
 */
@RestController
@RequestMapping("api/v1/minigames/crosswordpuzzle/crosswordpuzzle")
@Slf4j
public class CrosswordController {
    private final String configNotfound = "Configuration not found";

    @GetMapping("/validateCrossword")
    public boolean isValidCrosswordPuzzle(@RequestBody List<Question> questions){
        log.debug("ValidateCrossword");
        CrosswordChecker crosswordChecker = new CrosswordChecker();
        return crosswordChecker.checkCrossword(questions);
    }
}
