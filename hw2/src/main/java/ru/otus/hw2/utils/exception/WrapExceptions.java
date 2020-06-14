package ru.otus.hw2.utils.exception;

import ru.otus.hw2.core.exception.Hw2Exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для обозначения среза аспекта,
 * оборачивающего возникающие исключения в тип,
 * указанный в значении аннотации
 *
 * @implNote Установка над методом, принимающим
 * аргументы примитивных типов, приведет к
 * исключению времени выполнения
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WrapExceptions {
    Class<? extends Throwable> value() default Hw2Exception.class;
}
