package com.quiz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 500)
    private String options;  // JSON array string: ["A.xx","B.xx","C.xx","D.xx"]

    @Column(nullable = false, length = 1)
    private String answer;   // A / B / C / D

    @Column(length = 50)
    private String category;

    public Question() {}

    public Question(String content, String options, String answer, String category) {
        this.content = content;
        this.options = options;
        this.answer = answer;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
