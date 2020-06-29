package ru.otus.hw4.gui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw4.core.exception.UserInterfaceException;
import ru.otus.hw4.core.gui.UserInterface;
import ru.otus.hw4.core.question.Answer;
import ru.otus.hw4.core.question.Question;

@Service
@Slf4j
public class UserInterfaceImpl implements UserInterface {
    private final IO io;

    public UserInterfaceImpl(IO io) {
        this.io = io;
    }

    @Override
    public void intro() {
        showSeparator();
        io.interPrintln("welcome");
        showSeparator();
    }

    @Override
    public String askUserName() {
        String msg = "enter.name";
        String name;
        do {
            io.interPrint(msg);
            name = io.readLine();
            msg = "enter.nonempty.string";
        }
        while ("".equals(name));
        return name;
    }

    @Override
    public String askUserSurname() {
        String msg = "enter.surname";
        String surname;
        do {
            io.interPrint(msg);
            surname = io.readLine();
            msg = "enter.nonempty.string";
        }
        while ("".equals(surname));
        return surname;
    }

    @Override
    public void greet(String username) {
        io.interPrintln("greet", username);
    }

    @Override
    public void showQuestion(Question question, int number) throws UserInterfaceException {
        showSeparator();
        io.interPrint("question.number", number);
        io.interPrintln(question.getText());
    }

    @Override
    public void showAnswer(Answer answer, int number) throws UserInterfaceException {
        io.interPrint("answer.number", number);
        io.interPrintln(answer.getText());
    }

    @Override
    public String receiveFreeFormResponse() throws UserInterfaceException {
        String message = "enter.free";
        String answer;
        do {
            io.interPrint(message);
            answer = io.readLine();
            message = "reenter.free";
        } while (answer.isEmpty());
        return answer;
    }

    @Override
    public int receiveTestFormResponse() throws UserInterfaceException {
        String message = "enter.test";
        String answer;
        do {
            io.interPrint(message);
            answer = io.readLine();
            message = "reenter.test";
        } while ( ! answer.matches("^[+-]?\\d+$") );
        return Integer.parseInt(answer);
    }

    @Override
    public void showTestAnswerBounds(int minNum, int maxNum) {
        io.interPrintln("reenter.test.bound", minNum, maxNum);
    }

    public void showSeparator() throws UserInterfaceException {
        io.println("==================================");
    }

    @Override
    public void congratulate(String username) {
        showSeparator();
        io.interPrintln("congratulations", username);
        showSeparator();
    }

    @Override
    public void console(String username) {
        showSeparator();
        io.interPrintln("consolation", username);
        showSeparator();
    }

    @Override
    public void showRatio(double ratio) {
        io.interPrintln("ratio", ratio);
    }

}
