package ru.otus.hw5.ui;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.shell.Shell;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw5.config.Settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IOTest {
    private final static Locale LOCALE = Locale.forLanguageTag("en");
    private final static Charset CHARSET = UTF_8;

    private IO io;

    private ByteArrayOutputStream out;
    private ByteArrayInputStream in;
    @Mock private MessageSource messageSource;
    @Mock private Settings settings;
    @Mock private Settings.Ui ui;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        out = new ByteArrayOutputStream();
        in = new ByteArrayInputStream("line1\n  line2  \n line3 ".getBytes(CHARSET));
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
        val param = new Object[] {"param"};
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
}