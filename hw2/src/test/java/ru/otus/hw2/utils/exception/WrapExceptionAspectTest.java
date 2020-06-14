package ru.otus.hw2.utils.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import ru.otus.hw2.App;
import ru.otus.hw2.core.exception.Hw2Exception;
import ru.otus.hw2.core.exception.QuestionBlockCreationException;
import ru.otus.hw2.utils.csv.exception.MalformedCSVException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@Service
class WrapExceptionAspectTest {

    private ApplicationContext ctx;

    @BeforeEach
    void createContext() {
        ctx = new AnnotationConfigApplicationContext(App.class);
    }

    static class StaticInnerException extends RuntimeException {
        public StaticInnerException(Throwable cause) {
            super(cause);
        }

        public class ThrowableInnerException extends Throwable {
            // has default constructor
        }
    }

    @WrapExceptions(StaticInnerException.class)
    public void testStaticInnerException(String msg) {
        throw new RuntimeException(msg);
    }

    @Test
    void givenMethodThrowsRuntimeException_whenIntercept_thenWrapIntoStaticInnerException() {
        var x = ctx.getBean(getClass());
        String msg = "Inner test";
        assertThatExceptionOfType(StaticInnerException.class)
                .isThrownBy(() -> x.testStaticInnerException(msg))
                .withCauseInstanceOf(RuntimeException.class);
    }

    @WrapExceptions(StaticInnerException.ThrowableInnerException.class)
    public void testThrowableInnerException(String msg) {
        throw new StaticInnerException(new RuntimeException());
    }

    /**
     * TODO WrapExceptionAspect problem test with an inner Throwable classes
     * Problem test - when the target exception class is an inner class of other exception class
     * aspect choose the default no-arg constructor with implicit arg of outer class instance
     */
    // @Test
    void givenMethodThrowsRuntimeException_whenIntercept_thenWrapIntoThrowableInnerException() {
        var x = ctx.getBean(getClass());
        String msg = "Inner test";
        assertThatExceptionOfType(StaticInnerException.ThrowableInnerException.class)
                .isThrownBy(() -> x.testThrowableInnerException(msg))
                .withCauseInstanceOf(StaticInnerException.class);
    }

    @WrapExceptions
    public void testIOException() throws IOException {
        throw new IOException();
    }

    @Test
    void givenMethodThrowsIOException_whenIntercept_wrapIntoHw2Exception() {
        var x = ctx.getBean(WrapExceptionAspectTest.class);
        assertThatExceptionOfType(Hw2Exception.class)
                .isThrownBy(x::testIOException)
                .withCauseExactlyInstanceOf(IOException.class);
    }

    @WrapExceptions(QuestionBlockCreationException.class)
    public void testMalformedCSVException(String value1, Integer value2) {
        throw new MalformedCSVException("test");
    }

    @Test
    void givenMethodThrowsMalformedCSVException_whenIntercept_wrapIntoQuestionBlockCreationException() {
        var x = ctx.getBean(WrapExceptionAspectTest.class);
        assertThatExceptionOfType(QuestionBlockCreationException.class)
                .isThrownBy(() -> x.testMalformedCSVException("value", 1))
                .withCauseInstanceOf(MalformedCSVException.class);
    }

    @WrapExceptions
    public String testNoThrows(String x) {
        return x;
    }

    @Test
    void givenNoThrowMethod_whenIntercept_NoThrowReturnAndMethodValue() {
        var x = ctx.getBean(WrapExceptionAspectTest.class);
        String msg = "Nothing to throw";
        assertThat(x.testNoThrows(msg)).isEqualTo(msg);
    }
}