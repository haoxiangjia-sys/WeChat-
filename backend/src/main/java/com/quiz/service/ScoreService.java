package com.quiz.service;

import com.quiz.model.Score;
import com.quiz.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public Score submitScore(String nickname, int score, int total) {
        Score s = new Score(nickname, score, total);
        return scoreRepository.save(s);
    }

    public List<Score> getLeaderboard(int limit) {
        return scoreRepository.findTopScores(limit);
    }
}
