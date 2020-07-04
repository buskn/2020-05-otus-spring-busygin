package ru.otus.hw5.ui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.PrintWriter;

@Service
public class IO {
    private final PrintWriter writer;

    public IO(@Value("#{ T(java.lang.System).out }") OutputStream outputStream) {
        writer = new PrintWriter(outputStream);
    }

    public void print(Object o) {
        writer.print(o);
        writer.flush();
    }

    public void println() {
        writer.println();
        writer.flush();
    }

    public void println(Object o) {
        writer.println(o);
        writer.flush();
    }
}
