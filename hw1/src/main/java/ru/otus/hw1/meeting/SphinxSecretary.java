package ru.otus.hw1.meeting;

import ru.otus.hw1.meeting.exception.QuestionBlockCreationException;
import ru.otus.hw1.utils.Counter;

/**
 * Класс для печати теста студентам
 */
public class SphinxSecretary {
    private final QuestionService service;

    public SphinxSecretary(QuestionService questionService) {
        this.service = questionService;
    }

    /**
     * Напечатать бланк тестирования
     */
    public void printTest() throws QuestionBlockCreationException {
        service.getAllQuestionBlocks().forEach(block -> {
            System.out.println(block.getQuestion().getText());
            if (block.isFreeFormAnswer()) {
                printAnswerBox();
            }
            else {
                Counter c = new Counter();
                block.getAnswers().forEach(ans ->
                        System.out.println(c.incAndGet() + ") " + ans.getText()));
                printAnswerPlace();
            }
            System.out.println();
        });
    }

    private void printAnswerPlace() {
        System.out.println("Your answer: ____");
    }

    private void printAnswerBox() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 40; j++) {
                System.out.print('_');
            }
            System.out.println();
        }
    }
}
