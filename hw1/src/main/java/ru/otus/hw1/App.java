package ru.otus.hw1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw1.meeting.SphinxSecretary;

@Slf4j
public class App {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("./app-context.xml");

        SphinxSecretary sec = ctx.getBean("sphinxSecretary", SphinxSecretary.class);
        sec.printTest();
    }
}
