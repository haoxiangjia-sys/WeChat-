package com.quiz.controller;

import com.quiz.model.Question;
import com.quiz.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")
    public List<Question> getQuestions(@RequestParam(defaultValue = "10") int count) {
        return questionService.getRandomQuestions(count);
    }
}
