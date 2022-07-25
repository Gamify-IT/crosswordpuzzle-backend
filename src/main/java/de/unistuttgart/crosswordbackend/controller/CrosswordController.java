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

  private final String configNotfound = "Configuration not found";

  @Autowired
  QuestionMapper questionMapper;

  @GetMapping("/validateCrossword")
  public boolean isValidCrosswordPuzzle(@RequestBody Set<QuestionDTO> questionDTOs) {
    log.debug("ValidateCrossword");
    CrosswordChecker crosswordChecker = new CrosswordChecker();
    List<Question> questions = new ArrayList<>(questionMapper.questionDTOsToQuestions(questionDTOs));
    return crosswordChecker.checkCrossword(questions);
  }
}
