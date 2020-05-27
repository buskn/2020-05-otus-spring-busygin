package ru.otus.hw1.meeting;

import ru.otus.hw1.utils.Counter;

import java.util.List;

/**
 * Класс для печати теста студентам
 */
public class SphinxSecretary {
    private QuestionService service;

    public SphinxSecretary(QuestionService service) {
        this.service = service;
    }

    /**
     * Напечатать бланк тестирования
     */
    public void printTest() {
        service.getAllQuestionBlocks().forEach(block -> {
            System.out.println(block.getQuestion());
            if (block.isFreeFormAnswer()) {
                printBlankLines();
            }
            else {
                Counter c = new Counter();
                block.getAnswers().forEach(ans ->
                        System.out.println(c.incAndGet() + ") " + ans.getText()));
            }
        });
    }

    private void printBlankLines() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 40; j++) {
                System.out.print('_');
            }
            System.out.println();
        }
    }
}
