package ru.otus.hw3.config;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Locale;

@Deprecated
@Component
@Getter
@Slf4j
@ToString
public class ValueAnnotatedAppSettings {
    private final String questionFileName;
    private final Charset questionFileCharset;
    private final char questionFileSeparator;
    private final char questionFileEncloser;
    private final double testingAcceptableRatio;
    private final Charset uiCharset;
    private final String i18nPath;
    private final Locale uiLocale;
    private final boolean debug;

    public ValueAnnotatedAppSettings(
        @Value("${hw3.questions.filename:questions}") String questionFileName,
        @Value("${hw3.questions.charset:UTF-8}") Charset questionFileCharset,
        @Value("${hw3.questions.separator:;}") char questionFileSeparator,
        @Value("${hw3.questions.encloser:\"}") char questionFileEncloser,
        @Value("${hw3.testing.acceptable-ratio:0.59}") double testingAcceptableRatio,
        @Value("${hw3.ui.charset:UTF-8}") Charset uiCharset,
        @Value("${hw3.ui.i18n:i18n}") String i18nPath,
        @Value("${hw3.ui.locale:en}") Locale uiLocale,
        @Value("${hw3.debug:false}") boolean debug)
    {
        this.questionFileName = questionFileName;
        this.questionFileCharset = questionFileCharset;
        this.questionFileSeparator = questionFileSeparator;
        this.questionFileEncloser = questionFileEncloser;
        this.testingAcceptableRatio = testingAcceptableRatio;
        this.uiCharset = uiCharset;
        this.i18nPath = i18nPath;
        this.uiLocale = uiLocale;
        this.debug = debug;
    }
}
