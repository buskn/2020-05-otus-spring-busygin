package ru.otus.hw2.gui;

import org.springframework.stereotype.Service;
import ru.otus.hw2.core.exception.UserInterfaceException;
import ru.otus.hw2.core.gui.UserInterface;
import ru.otus.hw2.core.question.Answer;
import ru.otus.hw2.core.question.Question;
import ru.otus.hw2.utils.debug.Debug;

import java.util.Scanner;

@Service
public class UserInterfaceImpl implements UserInterface {
    private final Scanner scanner = new Scanner(System.in);

    private void println(Object o) {
        System.out.println(o);
    }

    private void print(Object o) {
        System.out.print(o);
    }

    private void printf(String fmt, Object ... o) {
        System.out.printf(fmt, o);
    }

    @Debug
    @Override
    public void showQuestion(Question question, int number) throws UserInterfaceException {
        printf("QUESTION %d: ", number);
        println(question.getText());
    }

    @Debug
    @Override
    public void showAnswer(Answer answer, int number) throws UserInterfaceException {
        printf("%d) ", number);
        println(answer.getText());
    }

    @Override
    public String receiveFreeFormResponse() throws UserInterfaceException {
        String message = "Enter a single custom string as your answer: ";
        String answer;
        do {
            print(message);
            answer = scanner.nextLine().trim();
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
            answer = scanner.nextLine().trim();
            message = "Sorry, it's not an integer number. Please, try again: ";
        } while ( ! answer.matches("^[+-]?\\d+$") );
        return Integer.parseInt(answer);
    }

    public void showMessage(String message) {
        println(message);
    }

    @Override
    public void showSeparator() throws UserInterfaceException {
        println("==================================");
    }
}
