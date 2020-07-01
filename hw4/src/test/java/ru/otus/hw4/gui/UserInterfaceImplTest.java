package ru.otus.hw4.gui;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw4.core.question.Answer;
import ru.otus.hw4.core.question.Question;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserInterfaceImplTest {

    @Configuration
    @Import(UserInterfaceImpl.class)
    static class Config {}

    @SpyBean UserInterfaceImpl ui;

    @MockBean IO io;

    @Test
    void whenIntro_thenCorrectInvokes() {
        ui.intro();

        verify(ui, atLeastOnce()).showSeparator();
        val arg = ArgumentCaptor.forClass(String.class);
        verify(io, times(1)).interPrintln(arg.capture());
        assertThat(arg.getValue()).isEqualTo("welcome");
    }

    @Test
    void whenGreet_thenCorrectInvoking() {
        String username = "username";

        ui.greet(username);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val paramArg = ArgumentCaptor.forClass(String.class);
        verify(io, times(1))
                .interPrintln(codeArg.capture(), paramArg.capture());
        assertThat(codeArg.getValue()).isEqualTo("greet");
        assertThat(paramArg.getValue()).isEqualTo(username);
    }

    @Test
    void whenShowQuestion_thenCorrectInvoking() {
        String text = "question";
        Question question = mock(Question.class);
        when(question.getText()).thenReturn(text);
        int number = 123;

        ui.showQuestion(question, number);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val paramArg = ArgumentCaptor.forClass(Object.class);
        verify(io, times(1))
                .interPrint(codeArg.capture(), paramArg.capture());
        assertThat(codeArg.getValue()).isEqualTo("question.number");
        assertThat(paramArg.getValue()).isEqualTo(number);

        val codeArg2 = ArgumentCaptor.forClass(String.class);
        verify(io, atLeastOnce())
                .println(codeArg2.capture());
        assertThat(codeArg2.getValue()).isEqualTo(text);
    }

    @Test
    void whenShowAnswer_thenCorrectInvoking() {
        String text = "answer";
        Answer answer = mock(Answer.class);
        when(answer.getText()).thenReturn(text);
        int number = 123;

        ui.showAnswer(answer, number);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val paramArg = ArgumentCaptor.forClass(Object.class);
        verify(io, times(1))
                .interPrint(codeArg.capture(), paramArg.capture());
        assertThat(codeArg.getValue()).isEqualTo("answer.number");
        assertThat(paramArg.getValue()).isEqualTo(number);

        val codeArg2 = ArgumentCaptor.forClass(String.class);
        verify(io, times(1))
                .println(codeArg2.capture());
        assertThat(codeArg2.getValue()).isEqualTo(text);
    }

    @Test
    void givenMixedInput_whenReceiveFreeFormResponse_thenSuccess() {
        String response = "response";
        when(io.readLine())
                .thenReturn("")
                .thenReturn(response);

        assertThat(ui.receiveFreeFormResponse()).isEqualTo(response);

        val arg = ArgumentCaptor.forClass(String.class);
        verify(io, times(2)).interPrint(arg.capture());
        assertThat(arg.getAllValues()).containsExactly("enter.free", "reenter.free");
        verify(io, times(2)).readLine();
    }

    @Test
    void givenMixedInput_whenReceiveTestFormResponse_thenSuccess() {
        String response = "10";
        when(io.readLine())
                .thenReturn("")
                .thenReturn("qwerty")
                .thenReturn(response);

        assertThat(ui.receiveTestFormResponse()).isEqualTo(Integer.parseInt(response));

        val arg = ArgumentCaptor.forClass(String.class);
        verify(io, times(3)).interPrint(arg.capture());
        assertThat(arg.getAllValues()).containsExactly("enter.test", "reenter.test", "reenter.test");
        verify(io, times(3)).readLine();
    }

    @Test
    void whenShowTestAnswerBounds_thenSuccess() {
        int minNum = 0, maxNum = 10;

        ui.showTestAnswerBounds(minNum, maxNum);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val minNumArg = ArgumentCaptor.forClass(Integer.class);
        val maxNumArg = ArgumentCaptor.forClass(Integer.class);
        verify(io, times(1))
                .interPrintln(codeArg.capture(), minNumArg.capture(), maxNumArg.capture());
        assertThat(codeArg.getValue()).isEqualTo("reenter.test.bound");
        assertThat(minNumArg.getValue()).isEqualTo(minNum);
        assertThat(maxNumArg.getValue()).isEqualTo(maxNum);
    }

    @Test
    void whenShowSeparator_thenSuccess() {
        ui.showSeparator();
        verify(io, atLeastOnce()).println(any());
    }

    @Test
    void whenCongratulate_thenSuccess() {
        String username = "username";

        ui.congratulate(username);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val paramArg = ArgumentCaptor.forClass(String.class);
        verify(io, times(1))
                .interPrintln(codeArg.capture(), paramArg.capture());
        assertThat(codeArg.getValue()).isEqualTo("congratulations");
        assertThat(paramArg.getValue()).isEqualTo(username);
    }

    @Test
    void whenConsole_thenSuccess() {
        String username = "username";

        ui.console(username);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val paramArg = ArgumentCaptor.forClass(String.class);
        verify(io, times(1))
                .interPrintln(codeArg.capture(), paramArg.capture());
        assertThat(codeArg.getValue()).isEqualTo("consolation");
        assertThat(paramArg.getValue()).isEqualTo(username);
    }

    @Test
    void whenShowRatio_thenSuccess() {
        double ratio = Double.MAX_VALUE;

        ui.showRatio(ratio);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val paramArg = ArgumentCaptor.forClass(Double.class);
        verify(io, times(1))
                .interPrintln(codeArg.capture(), paramArg.capture());
        assertThat(codeArg.getValue()).isEqualTo("ratio");
        assertThat(paramArg.getValue()).isEqualTo(ratio);
    }
}