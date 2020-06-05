package ru.otus.hw1.utils;

/**
 * Класс, который просто считает от 1
 */
public class Counter {
    private int count;
    public int incAndGet() {
        return ++count;
    }
}
