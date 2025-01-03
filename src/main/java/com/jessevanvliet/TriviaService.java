package com.jessevanvliet;

import com.jessevanvliet.data.Question;
import com.jessevanvliet.data.QuestionResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TriviaService {

    private static final Logger logger = LoggerFactory.getLogger(TriviaService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String TRIVIA_API_URL = "https://opentdb.com/api.php?amount=10";

    public final Map<String, Question> questionStore = new ConcurrentHashMap<>();

    public List<QuestionResponse> getQuestions() {
        logger.info("Fetching questions from the API...");

        var response = restTemplate.getForObject(TRIVIA_API_URL, Map.class);

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        List<QuestionResponse> questionResponses = new ArrayList<>();

        if (results != null) {
            for (Map<String, Object> result : results) {
                logger.debug("Processing question: {}", result.get("question"));

                String id = UUID.randomUUID().toString();

                Question question = new Question();
                question.setQuestion((String) result.get("question"));
                question.setCorrectAnswer((String) result.get("correct_answer"));
                question.setIncorrectAnswers((List<String>) result.get("incorrect_answers"));
                question.setCategory((String) result.get("category"));
                question.setDifficulty((String) result.get("difficulty"));

                questionStore.put(id, question);

                List<String> allAnswers = new ArrayList<>(question.getIncorrectAnswers());
                allAnswers.add(question.getCorrectAnswer());
                Collections.shuffle(allAnswers);

                QuestionResponse questionResponse = new QuestionResponse(
                    id,
                    question.getQuestion(),
                    allAnswers,
                    question.getCategory(),
                    question.getDifficulty()
                );

                questionResponses.add(questionResponse);
            }
            logger.info("Successfully fetched and processed {} questions.", results.size());
        } else {
            logger.error("Failed to fetch questions from API.");
        }

        return questionResponses;
    }

    public ResponseEntity<Map<String, Object>> checkAnswer(String questionId, String answer) {
        Map<String, Object> response = new HashMap<>();

        Question question = questionStore.get(questionId);
        if (question == null) {
            response.put("success", false);
            response.put("error", "Invalid question ID.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answer);
        response.put("success", isCorrect);

        if (!isCorrect) {
            response.put("error", "Incorrect answer.");
        }

        return ResponseEntity.ok(response);
    }
}
