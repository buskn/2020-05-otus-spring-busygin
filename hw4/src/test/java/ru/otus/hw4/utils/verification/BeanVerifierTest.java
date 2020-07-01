package ru.otus.hw4.utils.verification;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest
class BeanVerifierTest {

    @Configuration
    @Import({BeanVerifier.class,
            BeanUsual.class,
            BeanCustomName.class})
    static class Config {}

    @Verifiable
    @Slf4j
    static class BeanUsual {
        public void verify() {
            log.info(getClass() + " verification invoked");
        }
    }

    @Verifiable("customMethod")
    @Slf4j
    static class BeanCustomName {
        public void customMethod() {
            log.info(getClass() + " verification invoked");
        }
    }

    @Autowired BeanVerifier verifier;

    @Test
    void whenVerify_thenSuccess() {
        verifier.verify();
    }
}