package ru.otus.hw2.gui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw2.core.exception.UserInterfaceException;
import ru.otus.hw2.core.gui.UserInterface;
import ru.otus.hw2.core.question.Answer;
import ru.otus.hw2.core.question.Question;
import ru.otus.hw2.utils.debug.Debug;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Scanner;

@Service
@Slf4j
public class UserInterfaceImpl implements UserInterface {
    private final Scanner scanner;
    private final PrintWriter printer;

    public UserInterfaceImpl(
            @Value("#{T(java.lang.System).in}") InputStream input,
            @Value("#{T(java.lang.System).out}") OutputStream output,
            @Value("${hw2.ui.charset:UTF-8}") Charset charset
    ) {
        scanner = new Scanner(input, charset);
        printer = new PrintWriter(output, true, charset);
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

    @Debug
    @Override
    public void intro() {
        showSeparator();
        println("Welcome to test system!");
        showSeparator();
    }

    @Debug
    @Override
    public String askUserName() {
        String msg = "Enter your name: ";
        String name;
        do {
            print(msg);
            name = readLine();
            msg = "Please, enter a non-empty string\n";
        }
        while ("".equals(name));
        return name;
    }

    @Debug
    @Override
    public String askUserSurname() {
        String msg = "Enter your surname: ";
        String surname;
        do {
            print(msg);
            surname = readLine();
            msg = "Please, enter a non-empty string: ";
        }
        while ("".equals(surname));
        return surname;
    }

    @Debug
    @Override
    public void greet(String username) {
        println("Hello, " + username + "! Good luck!");
    }

    @Debug
    @Override
    public void showQuestion(Question question, int number) throws UserInterfaceException {
        showSeparator();
        printf("QUESTION %d: ", number);
        println(question.getText());
    }

    @Debug
    @Override
    public void showAnswer(Answer answer, int number) throws UserInterfaceException {
        printfln("%d) %s", number, answer.getText());
    }

    @Override
    public String receiveFreeFormResponse() throws UserInterfaceException {
        String message = "Enter a single custom string as your answer: ";
        String answer;
        do {
            print(message);
            answer = readLine();
            message = "Your answer seems to be empty. Please, try again: ";
        } while (answer.isEmpty());
        return answer;
    }

    @Override
    public int receiveTestFormResponse() throws UserInterfaceException {
        String message = "Enter a number of the right answer: ";
        String answer;
        do {
            print(message);
            answer = readLine();
            message = "Sorry, it's not an integer number. Please, try again: ";
        } while ( ! answer.matches("^[+-]?\\d+$") );
        return Integer.parseInt(answer);
    }

    @Override
    public void showTestAnswerBounds(int minNum, int maxNum) {
        printfln("Response must be an integer number in [%d, %d]", minNum, maxNum);
    }

    public void showSeparator() throws UserInterfaceException {
        println("==================================");
    }

    @Override
    public void congratulate(String username) {
        showSeparator();
        printfln("Congratulations, %s! You passed the test!", username);
        showSeparator();
    }

    @Override
    public void console(String username) {
        showSeparator();
        printfln("Sorry, %s, you failed! May be next time you succeed.", username);
        showSeparator();
    }

    @Override
    public void showRatio(double ratio) {
        printfln("** Your right answer ratio is %.2f **", ratio);
    }

}
