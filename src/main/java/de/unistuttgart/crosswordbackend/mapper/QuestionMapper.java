package de.unistuttgart.crosswordbackend.mapper;

import de.unistuttgart.crosswordbackend.data.Question;
import de.unistuttgart.crosswordbackend.data.QuestionDTO;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionDTO questionToQuestionDTO(final Question question);

    Question questionDTOToQuestion(final QuestionDTO questionDTO);

    Set<Question> questionDTOsToQuestions(final Set<QuestionDTO> questionDTOs);
}
