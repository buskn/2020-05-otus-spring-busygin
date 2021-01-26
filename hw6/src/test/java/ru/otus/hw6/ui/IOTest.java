package ru.otus.hw6.ui;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;
import ru.otus.hw6.common.HwException;
import ru.otus.hw6.config.Settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class IOTest {
    private final static Locale LOCALE = Locale.forLanguageTag("en");
    private final static Charset CHARSET = UTF_8;

    private IO io;

    private ByteArrayOutputStream out;
    private ByteArrayInputStream in;
    @Mock
    private MessageSource messageSource;
    @Mock
    private Settings settings;
    @Mock
    private Settings.Ui ui;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        out = new ByteArrayOutputStream();

        val inputLines = List.of("line1", "  line2  ", " line3 ", "", "");
        in = new ByteArrayInputStream(String.join("\n", inputLines).getBytes(CHARSET));

        when(settings.getUi()).thenReturn(ui);
        when(ui.getLocale()).thenReturn(LOCALE);

        io = new IO(out, in, messageSource, settings);
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

    @Test
    void whenPrintf_thenSuccess() {
        val fmt = "1%s2%s3";
        val param1 = "_test1_";
        val param2 = "_test2_";
        val result = "1_test1_2_test2_3";

        io.printf(fmt, param1, param2);

        assertThat(out.toString(UTF_8)).isEqualTo(result);
    }

    @Test
    void givenExistCode_whenInter_thenSuccess() {
        val param = new Object[]{"param"};
        String code = "code";
        String result = "inter";
        when(messageSource.getMessage(eq(code), any(), eq(LOCALE)))
                .thenReturn(result);

        assertThat(io.inter(code, param)).isEqualTo(result);
        val paramArg = ArgumentCaptor.forClass(Object[].class);
        verify(messageSource, times(1))
                .getMessage(any(), paramArg.capture(), any());
        assertThat(paramArg.getValue()).isEqualTo(param);
    }

    @Test
    void whenReadLine_thenSuccess() {
        assertThat(io.readLine()).isEqualTo("line1");
        assertThat(io.readLine()).isEqualTo("line2");
        assertThat(io.readLine()).isEqualTo("line3");
    }

    @Test
    void givenMultiline_whenReadMultilineString_thenSuccess() {
        assertThat(io.readMultilineString()).isEqualTo("line1\nline2\nline3");
    }

    @Test
    void givenEmptyLine_whenReadMultilineString_thenReturnEmpty() {
        val emptyInput = new ByteArrayInputStream("\n".getBytes(CHARSET));
        io = new IO(out, emptyInput, io.getMessageSource(), io.getSettings());
        assertThat(io.readMultilineString()).isEmpty();
    }

    @Test
    void givenBlankMultiLine_whenReadMultilineString_thenReturnEmpty() {
        val emptyInput = new ByteArrayInputStream("    \t   \n     \n".getBytes(CHARSET));
        io = new IO(out, emptyInput, io.getMessageSource(), io.getSettings());
        assertThat(io.readMultilineString()).isEmpty();
    }

    @Test
    void givenNumberInBounds_whenReadIntInBounds_thenSuccess() {
        int num = 123;
        val input = new ByteArrayInputStream((num + "\n").getBytes(CHARSET));
        io = new IO(out, input, messageSource, settings);
        assertThat(io.readIntInBounds(100, 1000)).isEqualTo(num);
    }

    @Test
    void givenNumberOnLowerEdge_whenReadIntInBounds_thenSuccess() {
        int num = 100;
        val input = new ByteArrayInputStream((num + "\n").getBytes(CHARSET));
        io = new IO(out, input, messageSource, settings);
        assertThat(io.readIntInBounds(100, 1000)).isEqualTo(num);
    }

    @Test
    void givenNumberOnUpperEdge_whenReadIntInBounds_thenSuccess() {
        int num = 1000;
        val input = new ByteArrayInputStream((num + "\n").getBytes(CHARSET));
        io = new IO(out, input, messageSource, settings);
        assertThat(io.readIntInBounds(100, 1000)).isEqualTo(num);
    }

    @Test
    void givenBlank_whenReadIntInBounds_thenThrow() {
        val num = "  ";
        val input = new ByteArrayInputStream((num + "\n").getBytes(CHARSET));
        io = new IO(out, input, messageSource, settings);
        assertThatExceptionOfType(HwException.class)
                .isThrownBy(() -> io.readIntInBounds(100, 1000));
    }

    @Test
    void givenNotNumber_whenReadIntInBounds_thenThrow() {
        val num = "asd";
        val input = new ByteArrayInputStream((num + "\n").getBytes(CHARSET));
        io = new IO(out, input, messageSource, settings);
        assertThatExceptionOfType(HwException.class)
                .isThrownBy(() -> io.readIntInBounds(100, 1000));
    }

    @Test
    void givenNumberLowerThanBounds_whenReadIntInBounds_thenThrow() {
        int num = 99;
        val input = new ByteArrayInputStream((num + "\n").getBytes(CHARSET));
        io = new IO(out, input, messageSource, settings);
        assertThatExceptionOfType(HwException.class)
                .isThrownBy(() -> io.readIntInBounds(100, 1000));
    }

    @Test
    void givenNumberAboveThanBounds_whenReadIntInBounds_thenThrow() {
        int num = 1001;
        val input = new ByteArrayInputStream((num + "\n").getBytes(CHARSET));
        io = new IO(out, input, messageSource, settings);
        assertThatExceptionOfType(HwException.class)
                .isThrownBy(() -> io.readIntInBounds(100, 1000));
    }
}