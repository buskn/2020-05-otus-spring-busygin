package ru.otus.hw5.ui;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class IOTest {
    private IO io;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        io = new IO(out);
    }

    @Test
    void whenPrint_thenSuccess() {
        String msg = "qwerty";
        io.print(msg);
        assertThat(out.toString(UTF_8)).isEqualTo(msg);
    }

    @Test
    void givenNoArg_whenPrintln_thenSuccess() {
        io.println();
        assertThat(out.toString(UTF_8)).isEqualTo("\n");
    }

    @Test
    void givenArg_whenPrintln_thenSuccess() {
        String msg = "qwerty";
        io.println(msg);
        assertThat(out.toString(UTF_8)).isEqualTo(msg + "\n");
    }
}