package ru.otus.hw5.config;

import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class Localization {
    @Bean
    public MessageSource messageSource() {
        val src = new ReloadableResourceBundleMessageSource();
        src.setBasename("classpath:/i18n/messages");
        src.setDefaultEncoding("UTF-8");
        src.setUseCodeAsDefaultMessage(true);
        return src;
    }
}
