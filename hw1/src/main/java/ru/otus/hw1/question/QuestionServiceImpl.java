package ru.otus.hw1.question;

import org.springframework.core.io.Resource;
import ru.otus.hw1.meeting.QuestionBlock;
import ru.otus.hw1.meeting.QuestionService;

import java.util.List;

public class QuestionServiceImpl implements QuestionService {
    private Resource questionSrc;

    public QuestionServiceImpl(Resource questionSrc) {
        this.questionSrc = questionSrc;
    }

    @Override
    public List<QuestionBlock> getAllQuestionBlocks() {
        return null;
    }
}
