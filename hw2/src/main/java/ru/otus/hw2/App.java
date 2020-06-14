package ru.otus.hw2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import ru.otus.hw2.core.Sphinx;

@Configuration
@Slf4j
@ComponentScan
@PropertySource("properties.prop")
@EnableAspectJAutoProxy
public class App {

    public static void main(String[] args) {
        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(App.class);

        Sphinx sphinx = ctx.getBean("sphinx", Sphinx.class);
        sphinx.doTesting();
    }
}
