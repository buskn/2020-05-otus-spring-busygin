package ru.otus.hw4.gui;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.otus.hw4.config.AppSettings;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MessageServiceTest {
    private final static Locale
            LOCALE_EN = Locale.ENGLISH,
            LOCALE_RU = Locale.forLanguageTag("ru"),
            LOCALE_UNSUPPORTED = Locale.CHINESE;

    @Configuration
    @Import(MessageService.class)
    static class Config {
        @Bean
        MessageSource messageSource() {
            ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
            ms.setBasename("classpath:/i18n/bundle");
            ms.setDefaultEncoding("UTF-8");
            return ms;
        }
    }

    @Autowired MessageService messageService;

    @MockBean AppSettings settings;
    @MockBean AppSettings.Ui ui;

    @BeforeEach
    private void setUp() {
        when(settings.getUi()).thenReturn(ui);
    }

    @Test
    void givenEnLocale_whenGet_thenSuccess() {
        when(ui.getLocale()).thenReturn(LOCALE_EN);

        assertThat(messageService.get("test.message.no-params"))
                .isEqualTo("test message");
    }

    @Test
    void givenRuLocale_whenGet_thenSuccess() {
        when(ui.getLocale()).thenReturn(LOCALE_RU);

        assertThat(messageService.get("test.message.no-params"))
                .isEqualTo("тестовое сообщение");
    }

    @Test
    void givenUnsupportedLocale_whenGet_thenSuccessWithDefaultLocale() {
        when(ui.getLocale()).thenReturn(LOCALE_UNSUPPORTED);

        assertThat(messageService.get("test.message.no-params"))
                .isEqualTo("test message");
    }

    @Test
    void givenEnLocaleCodeWithOneParameter_whenGet_thenSuccess() {
        when(ui.getLocale()).thenReturn(LOCALE_EN);
        Object param = new Object();

        assertThat(messageService.get("test.message.one-param", param))
                .isEqualTo("test message with parameter " + param);
    }

    @Test
    void givenRuLocaleCodeWithOneParameter_whenGet_thenSuccess() {
        when(ui.getLocale()).thenReturn(LOCALE_RU);
        Object param = new Object();

        assertThat(messageService.get("test.message.one-param", param))
                .isEqualTo("тестовое сообщение с параметром " + param);
    }

    @Test
    void givenUnknownCode_whenGet_thenThrows() {
        when(ui.getLocale()).thenReturn(LOCALE_EN);
        String code = "unknown.code";

        assertThatExceptionOfType(NoSuchMessageException.class)
                .isThrownBy(() -> messageService.get(code))
                .withMessageContaining(code);
    }

}