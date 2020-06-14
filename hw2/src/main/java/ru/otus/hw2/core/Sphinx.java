package ru.otus.hw2.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw2.core.exception.QuestionBlockCreationException;
import ru.otus.hw2.core.gui.UserInterface;
import ru.otus.hw2.core.question.Answer;
import ru.otus.hw2.core.question.QuestionBlock;
import ru.otus.hw2.core.question.QuestionService;

/**
 * Класс для вывода в консоль теста
 */
@Service
public class Sphinx {
    private final QuestionService service;
    private final UserInterface ui;
    private final double acceptableRatio;

    public Sphinx(QuestionService service, UserInterface ui,
                  @Value("${hw2.testing.acceptableRatio:0.7}") double acceptableRatio) {
        this.service = service;
        this.ui = ui;
        this.acceptableRatio = acceptableRatio;
    }

    /**
     * Провести тестирование
     */
    public void doTesting() throws QuestionBlockCreationException {
        int rightResponses = 0,
                questionNumber = 0;
        for (QuestionBlock block : service.getAllQuestionBlocks()) {
            if (exam(block, ++questionNumber)) {
                rightResponses++;
            }
        }
        computeTestResult(rightResponses, questionNumber);
    }

    private boolean exam(QuestionBlock block, int number) {
        ui.showSeparator();
        return block.isFreeForm() ? examFreeForm(block, number) : examTestForm(block, number);
    }

    private boolean examFreeForm(QuestionBlock block, int number) {
        ui.showQuestion(block.getQuestion(), number);
        String response = ui.receiveFreeFormResponse();
        return block.getAnswers().stream()
                .map(Answer::getText)
                .anyMatch(response::equalsIgnoreCase);
    }

    private boolean examTestForm(QuestionBlock block, int number) {
        show(block, number);
        int response = getTestResponse(block);
        return block.getAnswers().get(response - 1).isCorrect();
    }

    private int getTestResponse(QuestionBlock block) {
        int response = ui.receiveTestFormResponse();
        while (response < 1 || response > block.getAnswers().size()) {
            ui.showMessage("Response must be an integer number in ["
                    + 1 + "," + block.getAnswers().size() + "]");
            response = ui.receiveTestFormResponse();
        }
        return response;
    }

    private void show(QuestionBlock block, int number) {
        int answerCount = 0;
        ui.showQuestion(block.getQuestion(), number);
        for (Answer answer : block.getAnswers()) {
            ui.showAnswer(answer, ++answerCount);
        }
    }

    private void computeTestResult(int rightResponses, int totalQuestions) {
        ui.showSeparator();
        double ratio = (double) rightResponses / totalQuestions;
        if (ratio >= acceptableRatio) {
            ui.showMessage("CONGRATULATIONS! You passed the test!");
        }
        else {
            ui.showMessage("Sorry, you failed. May be next time you will succeed!");
        }
        ui.showMessage("Your ratio is " + ratio);
    }
}
