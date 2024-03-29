package de.unistuttgart.crosswordbackend.controller;

import static de.unistuttgart.crosswordbackend.data.Roles.LECTURER_ROLE;

import de.unistuttgart.crosswordbackend.crosswordchecker.CrosswordChecker;
import de.unistuttgart.crosswordbackend.data.QuestionDTO;
import de.unistuttgart.crosswordbackend.mapper.QuestionMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import io.swagger.v3.oas.annotations.Operation;
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
    JWTValidatorService jwtValidatorService;

    @Autowired
    QuestionMapper questionMapper;

    @Operation(summary = "validates if a crossword-puzzle can be created out of the given questions")
    @GetMapping("/validate-crossword")
    public boolean isValidCrosswordPuzzle(
        @RequestBody final Set<QuestionDTO> questionDTOs,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER_ROLE);
        log.debug("The user wants to validate if \"{}\" is a valid crossword.", questionDTOs);
        return new CrosswordChecker().checkCrossword(questionMapper.questionDTOsToQuestions(questionDTOs));
    }
}
