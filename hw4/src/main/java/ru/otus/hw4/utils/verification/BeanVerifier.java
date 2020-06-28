package ru.otus.hw4.utils.verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BeanVerifier {
    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        log.info("Bean verifier starts");
        event.getApplicationContext().getBeansOfType(Verifiable.class).forEach( (k, v) -> {
            log.info("verifying " + k);
            v.verify();
        });
        log.info("Bean verifier done");
    }
}
