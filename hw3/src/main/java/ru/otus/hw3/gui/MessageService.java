package ru.otus.hw3.gui;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw3.config.AppSettings;

@Service
public class MessageService {
    private final MessageSource messageSource;
    private final AppSettings settings;

    public MessageService(MessageSource messageSource, AppSettings settings) {
        this.messageSource = messageSource;
        this.settings = settings;
    }

    public String get(String code, Object ... args) {
        return messageSource.getMessage(code, args, settings.ui.locale);
    }

}
