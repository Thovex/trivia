const API_BASE_URL = "http://localhost:8080/api";

let currentQuestion = null;

let answered = false;
let isPenalized = false;

let points = 0;

let timer;
let timeLeft = 10;

function clearResult(elem) {
    elem.innerText = "";
}

function decodeHtml(html) {
    const txt = document.createElement("textarea");
    txt.innerHTML = html;
    return txt.value;
}

function startTimer() {
    timeLeft = 10; // Reset time
    const timerDisplay = document.getElementById("timer");

    clearInterval(timer);

    timer = setInterval(() => {
        timerDisplay.innerText = `Time Left: ${timeLeft}s`;

        if (timeLeft <= 0) {
            clearInterval(timer);
            handleTimeout(); // Handle timeout scenario
        }

        timeLeft--;
    }, 1000);
}

function handleTimeout() {
    points = Math.max(0, points - 1);
    isPenalized = true;
    updatePointsDisplay();
    fetchQuestion();
}

// Update Points Display
function updatePointsDisplay() {
    document.getElementById("points").innerText = `Points: ${points}`;
}

async function fetchQuestion() {
    try {
        const nextButton = document.getElementById("next-button");
        setButtonState(nextButton, true, "Pick an Answer");

        timeLeft = 10; // Reset time
        const timerDisplay = document.getElementById("timer");
        timerDisplay.innerText = `Time Left: ${timeLeft}s`;

        answered = false; // Reset answer status
        isPenalized = false; // Reset penalty status for the new question

        const response = await fetch(`${API_BASE_URL}/questions`);
        if (!response.ok) {
            throw new Error("Failed to fetch questions from API.");
        }

        const questions = await response.json();
        if (!questions || questions.length === 0) {
            throw new Error("No questions available.");
        }

        currentQuestion = questions[0];

        // Display the question text
        document.getElementById("question").innerText = decodeHtml(currentQuestion.question);

        // Reset answers
        const answersDiv = document.getElementById("answers");
        clearResult(answersDiv);
        clearResult(document.getElementById("result"));

        currentQuestion.answers.forEach((answer) => {
            const button = document.createElement("button");
            button.innerText = decodeHtml(answer);
            button.onclick = () => checkAnswer(button, answer); // Pass button as parameter
            answersDiv.appendChild(button);
        });

        startTimer();

    } catch (error) {
        console.error(error);
        document.getElementById("question").innerText = "Failed to load question. Please try again!";
        setButtonState(document.getElementById("next-button"), false, "Retry");
    }
}

async function checkAnswer(button, answer) {
    if (answered) return; // Prevent multiple clicks
    clearInterval(timer);

    const nextButton = document.getElementById("next-button");
    setButtonState(nextButton, true, "Checking..."); // Disable during check

    const response = await fetch(
        `${API_BASE_URL}/checkanswers?questionId=${currentQuestion.id}&answer=${encodeURIComponent(answer)}`,
        { method: "POST" }
    );
    const result = await response.json();

    if (result.success) {
        button.style.backgroundColor = "green";
        button.style.color = "white";
        button.innerText = `✅ ${button.innerText}`;
        answered = true;

        if (!isPenalized) {
            points += 1;
        }
    } else {
        button.style.backgroundColor = "gray";
        button.style.color = "white";
        button.innerText = `❌ ${button.innerText}`;
        button.disabled = true; // Disable the wrong answer button
        points = Math.max(0, points - 1);
        isPenalized = true;
    }

    // Update points display
    updatePointsDisplay();

    // Enable the "Next Question" button only if an answer is selected
    if (answered) {
        setButtonState(nextButton, false, "Next Question");
        nextButton.onclick = () => fetchQuestion();
    } else {
        setButtonState(nextButton, true, "Wrong... Try Again!");
    }
}

function setButtonState(button, disabled, text) {
    button.disabled = disabled;
    button.innerText = text;
    button.style.backgroundColor = disabled ? "gray" : "#007bff";
    button.style.cursor = disabled ? "not-allowed" : "pointer";
}

fetchQuestion();
updatePointsDisplay();
