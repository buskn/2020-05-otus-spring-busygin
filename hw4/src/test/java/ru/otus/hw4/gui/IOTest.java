package ru.otus.hw4.gui;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw4.config.AppSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
class IOTest {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String MSG_CODE = "message.code";
    private static final String MSG_LOCAL = "localized message";
    private static final String MSG_PARAM = "message parameter";

    private IO io;

    private ByteArrayInputStream input;
    private ByteArrayOutputStream output;
    @Mock private MessageService messageService;
    @Mock private AppSettings settings;
    @Mock private AppSettings.Ui ui;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.initMocks(this);
        when(messageService.get(MSG_CODE, MSG_PARAM)).thenReturn(MSG_LOCAL);
        when(settings.getUi()).thenReturn(ui);
        when(ui.getLocale()).thenReturn(LOCALE);
        when(ui.getCharset()).thenReturn(CHARSET);

        input = new ByteArrayInputStream(new byte[0]);
        output = new ByteArrayOutputStream();
        io = new IO(input, output, messageService, settings);
    }

    @Test
    void givenValue_whenPrint_thenSuccessOutput() {
        io.print(MSG_LOCAL);

        assertThat(output.toByteArray()).isEqualTo(MSG_LOCAL.getBytes(CHARSET));
    }

    @Test
    void givenValue_whenPrintln_thenSuccessOutput() {
        io.println(MSG_LOCAL);

        assertThat(output.toByteArray()).isEqualTo((MSG_LOCAL + "\n").getBytes(CHARSET));
    }

    @Test
    void givenNoArg_whenPrintln_thenSuccessOutput() {
        io.println();

        assertThat(output.toByteArray()).isEqualTo("\n".getBytes(CHARSET));
    }

    @Test
    void givenMsgCode_whenInterPrint_thenOutputMsgLocal() {
        io.interPrint(MSG_CODE, MSG_PARAM);

        ArgumentCaptor<String> codeArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> objArg = ArgumentCaptor.forClass(String.class);
        verify(messageService, times(1))
                .get(codeArg.capture(), objArg.capture());
        assertThat(codeArg.getValue()).isEqualTo(MSG_CODE);
        assertThat(objArg.getValue()).isEqualTo(MSG_PARAM);

        assertThat(output.toByteArray()).isEqualTo(MSG_LOCAL.getBytes(CHARSET));
    }

    @Test
    void givenMsgCode_whenInterPrintln_thenOutputMsgLocal() {
        io.interPrintln(MSG_CODE, MSG_PARAM);

        ArgumentCaptor<String> codeArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> objArg = ArgumentCaptor.forClass(String.class);
        verify(messageService, times(1))
                .get(codeArg.capture(), objArg.capture());
        assertThat(codeArg.getValue()).isEqualTo(MSG_CODE);
        assertThat(objArg.getValue()).isEqualTo(MSG_PARAM);

        assertThat(output.toByteArray()).isEqualTo((MSG_LOCAL + "\n").getBytes(CHARSET));
    }

    @Test
    void givenSingleLine_whenReadLine_thenReturnSingleLine() {
        input = new ByteArrayInputStream(MSG_LOCAL.getBytes(CHARSET));
        io = new IO(input, output, messageService, settings);

        assertThat(io.readLine()).isEqualTo(MSG_LOCAL);
    }

    @Test
    void givenMultipleLine_whenReadLine_thenReturnSingleLine() {
        input = new ByteArrayInputStream((MSG_LOCAL + "\n" + MSG_CODE).getBytes(CHARSET));
        io = new IO(input, output, messageService, settings);

        assertThat(io.readLine()).isEqualTo(MSG_LOCAL);
    }

    @Test
    void givenArgs_whenInter_thenSuccess() {
        io.interPrint(MSG_CODE, MSG_PARAM);

        when(messageService.get(MSG_CODE, MSG_PARAM)).thenReturn(MSG_LOCAL);

        assertThat(io.inter(MSG_CODE, MSG_PARAM)).isEqualTo(MSG_LOCAL);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val paramArg = ArgumentCaptor.forClass(Object.class);
        verify(messageService, times(2))
                .get(codeArg.capture(), paramArg.capture());
        assertThat(codeArg.getValue()).isEqualTo(MSG_CODE);
        assertThat(paramArg.getValue()).isEqualTo(MSG_PARAM);
    }

}