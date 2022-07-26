package de.unistuttgart.crosswordbackend.controller;

import de.unistuttgart.crosswordbackend.crosswordchecker.CrosswordChecker;
import de.unistuttgart.crosswordbackend.data.Question;
import de.unistuttgart.crosswordbackend.data.QuestionDTO;
import de.unistuttgart.crosswordbackend.mapper.QuestionMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Rest Controller for the crossword-puzzle backend
 */
@RestController
@RequestMapping("/crosswordpuzzle")
@Slf4j
public class CrosswordController {

  @Autowired
  QuestionMapper questionMapper;

  @GetMapping("/validate-crossword")
  public boolean isValidCrosswordPuzzle(@RequestBody Set<QuestionDTO> questionDTOs) {
    log.debug("The user wants to validate if \"{}\" is a valid crossword.", questionDTOs);
    return new CrosswordChecker().checkCrossword(questionMapper.questionDTOsToQuestions(questionDTOs));
  }
}
