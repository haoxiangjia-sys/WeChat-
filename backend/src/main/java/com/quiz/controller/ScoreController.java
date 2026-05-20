package com.quiz.controller;

import com.quiz.model.Score;
import com.quiz.service.ScoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/scores")
    public Score submitScore(@RequestBody Map<String, Object> body) {
        String nickname = (String) body.get("nickname");
        int score = (int) body.get("score");
        int total = (int) body.get("total");
        return scoreService.submitScore(nickname, score, total);
    }

    @GetMapping("/leaderboard")
    public List<Score> getLeaderboard(@RequestParam(defaultValue = "20") int limit) {
        return scoreService.getLeaderboard(limit);
    }
}
