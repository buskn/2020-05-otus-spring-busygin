package ru.otus.hw4.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw4.config.AppSettings;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class IOTest {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @MockBean IO io;

    @MockBean InputStream input;
    @MockBean OutputStream output;
    @MockBean MessageService messageService;
    @MockBean AppSettings settings;
    @Mock AppSettings.Ui ui;

    @BeforeEach
    private void setUp() {
        when(settings.getUi()).thenReturn(ui);
        // TODO continue
    }

    @Test
    void print() {
    }

    @Test
    void println() {
    }

    @Test
    void testPrintln() {
    }

    @Test
    void interPrint() {
    }

    @Test
    void interPrintln() {
    }

    @Test
    void readLine() {
    }
}