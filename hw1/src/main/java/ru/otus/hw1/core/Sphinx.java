package ru.otus.hw1.core;

import ru.otus.hw1.core.exception.QuestionBlockCreationException;
import ru.otus.hw1.utils.Counter;

/**
 * Класс для вывода в консоль теста
 */
public class Sphinx {
    private final QuestionService service;

    public Sphinx(QuestionService questionService) {
        this.service = questionService;
    }

    /**
     * Напечатать бланк тестирования
     */
    public void printTest() throws QuestionBlockCreationException {
        service.getAllQuestionBlocks().forEach(block -> {
            if (block.isFreeFormAnswer()) {
                printFreeFormBlock(block);
            }
            else {
                printTestFormBlock(block);
            }
            System.out.println();
        });
    }

    private void printFreeFormBlock(QuestionBlock block) {
        System.out.println(block.getQuestion().getText());
        printAnswerBox();
    }

    private void printTestFormBlock(QuestionBlock block) {
        System.out.println(block.getQuestion().getText());
        Counter c = new Counter();
        block.getAnswers().forEach(ans ->
                System.out.println(c.incAndGet() + ") " + ans.getText()));
        printAnswerPlace();
    }

    private void printAnswerPlace() {
        System.out.println("Your answer: ____");
    }

    private void printAnswerBox() {
        for (int i = 0; i < 4; i++) {
            System.out.println("_____________________________");
        }
    }
}
