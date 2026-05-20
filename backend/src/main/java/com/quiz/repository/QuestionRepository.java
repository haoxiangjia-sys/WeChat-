package com.quiz.repository;

import com.quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM questions ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<Question> findRandomQuestions(int count);
}
