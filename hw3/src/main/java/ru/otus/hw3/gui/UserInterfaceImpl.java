package ru.otus.hw3.gui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw3.config.AppSettings;
import ru.otus.hw3.core.exception.UserInterfaceException;
import ru.otus.hw3.core.gui.UserInterface;
import ru.otus.hw3.core.question.Answer;
import ru.otus.hw3.core.question.Question;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

@Service
@Slf4j
public class UserInterfaceImpl implements UserInterface {
    private final Scanner scanner;
    private final PrintWriter printer;
    private final MessageService ms;

    public UserInterfaceImpl(
            @Value("#{T(java.lang.System).in}") InputStream input,
            @Value("#{T(java.lang.System).out}") OutputStream output,
            MessageService ms,
            AppSettings settings
    ) {
        scanner = new Scanner(input, settings.ui.charset);
        printer = new PrintWriter(output, true, settings.ui.charset);
        this.ms = ms;
    }

    private void print(String str) {
        printer.print(str);
        printer.flush();
    }

    private void println(String str) {
        printer.println(str);
        printer.flush();
    }

    private void printf(String fmt, Object ... objs) {
        printer.printf(fmt, objs);
        printer.flush();
    }

    private void printfln(String fmt, Object ... objs) {
        printer.printf(fmt + "\n", objs);
        printer.flush();
    }

    private String readLine() {
        return scanner.nextLine().trim();
    }

    @Override
    public void intro() {
        showSeparator();
        println(ms.get("welcome"));
        showSeparator();
    }

    @Override
    public String askUserName() {
        String msg = ms.get("enter.name");
        String name;
        do {
            print(msg);
            name = readLine();
            msg = ms.get("enter.nonempty.string") + "\n";
        }
        while ("".equals(name));
        return name;
    }

    @Override
    public String askUserSurname() {
        String msg = ms.get("enter.surname");
        String surname;
        do {
            print(msg);
            surname = readLine();
            msg = ms.get("enter.nonempty.string") + "\n";
        }
        while ("".equals(surname));
        return surname;
    }

    @Override
    public void greet(String username) {
        println(ms.get("greet", username));
    }

    @Override
    public void showQuestion(Question question, int number) throws UserInterfaceException {
        showSeparator();
        print(ms.get("question.number", number));
        println(question.getText());
    }

    @Override
    public void showAnswer(Answer answer, int number) throws UserInterfaceException {
        println(ms.get("answer.number", number, answer.getText()));
    }

    @Override
    public String receiveFreeFormResponse() throws UserInterfaceException {
        String message = ms.get("enter.free");
        String answer;
        do {
            print(message);
            answer = readLine();
            message = ms.get("reenter.free");
        } while (answer.isEmpty());
        return answer;
    }

    @Override
    public int receiveTestFormResponse() throws UserInterfaceException {
        String message = ms.get("enter.test");
        String answer;
        do {
            print(message);
            answer = readLine();
            message = ms.get("reenter.test");
        } while ( ! answer.matches("^[+-]?\\d+$") );
        return Integer.parseInt(answer);
    }

    @Override
    public void showTestAnswerBounds(int minNum, int maxNum) {
        println(ms.get("reenter.test.bound", minNum, maxNum));
    }

    public void showSeparator() throws UserInterfaceException {
        println("==================================");
    }

    @Override
    public void congratulate(String username) {
        showSeparator();
        println(ms.get("congratulations", username));
        showSeparator();
    }

    @Override
    public void console(String username) {
        showSeparator();
        println(ms.get("consolation", username));
        showSeparator();
    }

    @Override
    public void showRatio(double ratio) {
        println(ms.get("ratio", ratio));
    }

}
