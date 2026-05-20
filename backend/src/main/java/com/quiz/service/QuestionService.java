package com.quiz.service;

import com.quiz.model.Question;
import com.quiz.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getRandomQuestions(int count) {
        return questionRepository.findRandomQuestions(count);
    }
}
