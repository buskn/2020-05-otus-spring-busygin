package ru.otus.hw6;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.hw6.ui.IO;

@Component
@Order(value = Integer.MIN_VALUE)
@RequiredArgsConstructor
public class StartupInfo implements ApplicationRunner {
    private final IO io;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        io.interPrintln("shell.startup-info");
    }
}
