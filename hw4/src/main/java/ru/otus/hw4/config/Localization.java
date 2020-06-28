package ru.otus.hw4.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class Localization {
    @Bean
    public MessageSource messageSource() {
        var ms = new ReloadableResourceBundleMessageSource();
        ms.setBasenames("classpath:/i18n/messages", "classpath:/i18n/questions");
        ms.setDefaultEncoding("UTF-8");
        return ms;
    }
}
