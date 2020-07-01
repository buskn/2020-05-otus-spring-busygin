package ru.otus.hw4.utils.verification;

import org.springframework.context.ApplicationEvent;

public class VerificationEvent extends ApplicationEvent {
    public VerificationEvent(Object source) {
        super(source);
    }
}
