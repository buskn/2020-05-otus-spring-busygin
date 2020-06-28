package ru.otus.hw3.utils;

/**
 * Класс, который просто считает от 1
 */
public class Counter {
    private int count;
    public void inc() { count++; }
    public void incIf(boolean value) { if (value) count++; }
    public int incAndGet() {
        return ++count;
    }
    public int get() { return count; }
}
