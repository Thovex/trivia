package com.jessevanvliet.assignment;

import com.jessevanvliet.TriviaController;
import com.jessevanvliet.TriviaService;
import com.jessevanvliet.data.QuestionResponse;
import com.jessevanvliet.data.Question;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AssignmentApplicationTests {

	@Mock
	private TriviaService triviaService;

	@InjectMocks
	private TriviaController triviaController;

	@Test
	void contextLoads() {
		assertTrue(true);
	}

	// Get Questions
	@Test
	void getQuestions_ShouldReturnListOfQuestions() {
		QuestionResponse mockQuestion = new QuestionResponse(
				"1",
				"What is the capital of France?",
				List.of("Paris", "London", "Berlin", "Rome"),
				"Geography",
				"easy"
		);
		when(triviaService.getQuestions()).thenReturn(List.of(mockQuestion)); // Mock service response

		List<QuestionResponse> response = triviaController.getQuestions();

		//System.out.println("Response: " + response); // Debug log

		assertNotNull(response);
		assertEquals(1, response.size());
		assertEquals("What is the capital of France?", response.get(0).getQuestion());

		verify(triviaService, times(1)).getQuestions();
	}

	// Check Correct Answer
	@Test
	void checkAnswer_ShouldReturnSuccess_WhenAnswerIsCorrect() {
		Map<String, Object> mockResponse = Map.of(
				"success", true,
				"error", ""
		);
		when(triviaService.checkAnswer("1", "Paris"))
				.thenReturn(ResponseEntity.ok(mockResponse)); // Mock service response

		ResponseEntity<Map<String, Object>> response = triviaController.checkAnswer("1", "Paris");

		//System.out.println("Response: " + response.getBody()); // Debug log

		assertNotNull(response);
		assertTrue((Boolean) response.getBody().get("success")); // Success should be true
		assertEquals("", response.getBody().get("error")); // Error should be empty string

		verify(triviaService, times(1)).checkAnswer("1", "Paris");
	}

	// Check Wrong Answer
	@Test
	void checkAnswer_ShouldReturnFailure_WhenAnswerIsWrong() {
		Map<String, Object> mockResponse = Map.of(
				"success", false,
				"error", "Incorrect answer."
		);
		when(triviaService.checkAnswer("1", "London"))
				.thenReturn(ResponseEntity.ok(mockResponse)); // Mock service response

		ResponseEntity<Map<String, Object>> response = triviaController.checkAnswer("1", "London");

		//System.out.println("Response: " + response.getBody()); // Debug log

		assertNotNull(response);
		assertFalse((Boolean) response.getBody().get("success")); // Success should be false
		assertEquals("Incorrect answer.", response.getBody().get("error")); // Error should match

		verify(triviaService, times(1)).checkAnswer("1", "London");
	}

	// Check Answer Correct
	@Test
	void checkAnswer_ServiceShouldReturnTrue_WhenAnswerIsCorrect() {
		TriviaService triviaService = new TriviaService();
		Question question = new Question();
		question.setQuestion("What is the capital of France?");
		question.setCorrectAnswer("Paris");
		question.setIncorrectAnswers(List.of("London", "Berlin", "Rome"));
		question.setCategory("Geography");
		question.setDifficulty("easy");

		triviaService.questionStore.put(question.getId(), question); // Mock in-memory data

		ResponseEntity<Map<String, Object>> result = triviaService.checkAnswer(question.getId(), "Paris");

		assertTrue((Boolean) result.getBody().get("success"));

		Object error = result.getBody().get("error");
		assertTrue(error == null || error.equals(""));
	}

	// 6. Test Service - Check Answer Wrong
	@Test
	void checkAnswer_ServiceShouldReturnFalse_WhenAnswerIsWrong() {
		TriviaService triviaService = new TriviaService();
		Question question = new Question();
		question.setQuestion("What is the capital of France?");
		question.setCorrectAnswer("Paris");
		question.setIncorrectAnswers(List.of("London", "Berlin", "Rome"));
		question.setCategory("Geography");
		question.setDifficulty("easy");

		triviaService.questionStore.put(question.getId(), question); // Mock in-memory data

		ResponseEntity<Map<String, Object>> result = triviaService.checkAnswer(question.getId(), "Berlin");

		assertFalse((Boolean) result.getBody().get("success"));
		assertEquals("Incorrect answer.", result.getBody().get("error")); // Error should match
	}

	// Check Invalid ID
	@Test
	void checkAnswer_ServiceShouldReturnFalse_WhenQuestionIdIsInvalid() {
		TriviaService triviaService = new TriviaService(); // Use actual service

		ResponseEntity<Map<String, Object>> result = triviaService.checkAnswer("999", "Paris");

		assertFalse((Boolean) result.getBody().get("success")); // Success should be false
		assertEquals("Invalid question ID.", result.getBody().get("error")); // Error should match
	}
}