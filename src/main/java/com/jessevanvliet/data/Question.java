package com.jessevanvliet.data;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class Question {
    private final String id = UUID.randomUUID().toString();
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers;
    private String category;
    private String difficulty;
    private String type;
}