package com.jessevanvliet;

import com.jessevanvliet.data.QuestionResponse;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TriviaController {
    private final TriviaService triviaService;

    public TriviaController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @GetMapping("/questions")
    public List<QuestionResponse> getQuestions() {
        return triviaService.getQuestions();
    }

    @PostMapping("/checkanswers")
    public ResponseEntity<Map<String, Object>> checkAnswer(@RequestParam String questionId, @RequestParam String answer) {
        return triviaService.checkAnswer(questionId, answer);
    }
}