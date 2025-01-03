package com.jessevanvliet.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.Data;

@Data
@AllArgsConstructor // -> For UnitTest
@NoArgsConstructor
public class QuestionResponse {
    private String id;
    private String question;
    private List<String> answers;
    private String category;
    private String difficulty;
}