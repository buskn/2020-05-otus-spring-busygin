package ru.otus.hw4.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw4.config.AppSettings;
import ru.otus.hw4.core.exception.QuestionBlockCreationException;
import ru.otus.hw4.core.gui.UserInterface;
import ru.otus.hw4.core.question.Answer;
import ru.otus.hw4.core.question.QuestionBlock;
import ru.otus.hw4.core.question.QuestionService;

/**
 * Класс для вывода в консоль теста
 */
@Service
@Slf4j
public class Sphinx {
    private final QuestionService service;
    private final UserInterface ui;
    private final AppSettings settings;

    private String username;

    public Sphinx(QuestionService service, UserInterface ui, AppSettings settings) {
        this.service = service;
        this.ui = ui;
        this.settings = settings;
    }

    /**
     * Провести тестирование
     */
    public void doTesting() throws QuestionBlockCreationException {
        login();
        double ratio = exam();
        if (ratio >= settings.getTesting().getAcceptableRatio()) {
            ui.congratulate(username);
        }
        else {
            ui.console(username);
        }
        ui.showRatio(ratio);
    }

    private void login() {
        ui.intro();
        String name = ui.askUserName();
        String surname = ui.askUserSurname();
        username = name + " " + surname;
        ui.greet(username);
    }

    private double exam() {
        int rightResponses = 0,
                questionNumber = 0;
        for (QuestionBlock block : service.getAllQuestionBlocks()) {
            if (examQuestion(block, ++questionNumber)) {
                rightResponses++;
            }
        }
        return computeTestResult(rightResponses, questionNumber);
    }

    private boolean examQuestion(QuestionBlock block, int number) {
        return block.isFreeForm() ? examFreeFormQuestion(block, number) : examTestFormQuestion(block, number);
    }

    private boolean examFreeFormQuestion(QuestionBlock block, int number) {
        ui.showQuestion(block.getQuestion(), number);
        String response = ui.receiveFreeFormResponse();
        return block.getAnswers().stream()
                .map(Answer::getText)
                .anyMatch(response::equalsIgnoreCase);
    }

    private boolean examTestFormQuestion(QuestionBlock block, int number) {
        showQuestionBlock(block, number);
        int response = getTestResponse(block);
        return block.getAnswers().get(response - 1).isCorrect();
    }

    private int getTestResponse(QuestionBlock block) {
        int response = ui.receiveTestFormResponse();
        while (response < 1 || response > block.getAnswers().size()) {
            ui.showTestAnswerBounds(1, block.getAnswers().size());
            response = ui.receiveTestFormResponse();
        }
        return response;
    }

    private void showQuestionBlock(QuestionBlock block, int number) {
        int answerCount = 0;
        ui.showQuestion(block.getQuestion(), number);
        for (Answer answer : block.getAnswers()) {
            ui.showAnswer(answer, ++answerCount);
        }
    }

    private double computeTestResult(int rightResponses, int totalQuestions) {
        return (double) rightResponses / totalQuestions;
    }
}
