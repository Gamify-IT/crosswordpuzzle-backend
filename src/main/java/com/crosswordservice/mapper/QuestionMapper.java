package com.crosswordservice.mapper;

import com.crosswordservice.data.Question;
import com.crosswordservice.data.QuestionDTO;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionDTO questionToQuestionDTO(final Question question);

    Question questionDTOToQuestion(final QuestionDTO questionDTO);

    Set<Question> questionDTOsToQuestions(final Set<QuestionDTO> questionDTOs);

    Set<QuestionDTO> questionsToQuestionDTOs(final Set<Question> questions);
}
