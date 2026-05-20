package com.quiz.repository;

import com.quiz.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    @Query(value = "SELECT * FROM scores ORDER BY score DESC, created_at ASC LIMIT :limit", nativeQuery = true)
    List<Score> findTopScores(int limit);
}
