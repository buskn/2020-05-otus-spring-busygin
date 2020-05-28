package ru.otus.hw1.question;

import ru.otus.hw1.meeting.Answer;
import ru.otus.hw1.meeting.Question;
import ru.otus.hw1.meeting.QuestionBlock;

import java.util.List;


/**
 * Реализация блока "вопрос-ответы"
 */
public class QuestionBlockImpl implements QuestionBlock {
    private final Question question;
    private final List<Answer> answers;
    private final boolean free;

    public QuestionBlockImpl(Question question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
        free = false;
    }

    public QuestionBlockImpl(Question question) {
        this.question = question;
        answers = null;
        free = true;
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    @Override
    public boolean isFreeFormAnswer() {
        return free;
    }

    @Override
    public List<Answer> getAnswers() {
        return answers;
    }
}
