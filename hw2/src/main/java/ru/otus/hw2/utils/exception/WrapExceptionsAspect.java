package ru.otus.hw2.utils.exception;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Аспект для оборачивания исключений, возникающих в методах,
 * помеченных аннотацией {@link WrapExceptions},
 * в указанный в аннотации целевой класс исключения
 * с причиной в изначально возникшем исключении
 *
 * @implNote Аспект не работает на методах, принимающих аргументы примитивных типов
 * @implNote Следует использовать для класса целевого исключения не внутренние классы
 * Например, попытка обернуть в исключение, класс которого вложенный, будет успешной,
 * но обернуть во внутренний класс не получится, если только это класс исключения
 * внутренний относительно класса другого исключения и имеет конструктор по-умолчанию,
 * тогда оборачивание пройдет успешно, но поведение нового исключения будет не определено
 */
@Aspect
@Component
@Slf4j
public class WrapExceptionsAspect {

    @Pointcut("@annotation(annotation)")
    public void annotationOperation(WrapExceptions annotation) {}

    /**
     * Совет, содержащий логику оборачивания
     * @param jp {@link JoinPoint} среза
     * @param annotation Аннотация над методом, около которого установлен срез
     * @throws Throwable обернутое исключение при возникновении в целевом методе
     * @throws WrapExceptionsAspectException при ошибках конструирования целевого объекта исключения
     */
    @Around("annotationOperation(annotation)")
    public Object wrap(ProceedingJoinPoint jp, WrapExceptions annotation) throws Throwable {
        log.info("exception wrap start");
        log.info(annotation + "");
        log.info(annotation.value() + "");
        try {
            return jp.proceed();
        }
        catch (Throwable t) {
            throw createTargetExceptionInstance(annotation.value(), t);
        }
    }

    /**
     * Конструирует объект целевого исключения с указанным исключением-причиной
     * @param clazz Класс целевого исключения, объект которого нужно породить
     * @param cause Исключение-причина
     * @return Новый объект целевого исключения с причиной в указанном исключении
     */
    private Throwable createTargetExceptionInstance(Class<? extends Throwable> clazz, Throwable cause) {
        Constructor<?> constructor = Arrays.stream( clazz.getConstructors() )
                .filter(c ->
                        c.getParameterCount() == 1 && c.getParameterTypes()[0].isInstance(cause))
                .findAny()
                .orElseThrow(() -> new WrapExceptionsAspectException(
                        "No appropriate constructor on target exception class: " +
                                "expect one parameter of Throwable type"));
        try {
            return (Throwable) constructor.newInstance(cause);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new WrapExceptionsAspectException("Construction target exception error", e);
        }
    }

    /**
     * {@link JoinPoint} не содержит ссылки на {@link Method},
     * около которого устанавливаем срез, поэтому приходится через рефлексию
     * @param jp {@link JoinPoint} среза
     * @return {@link Method}, около которого установлен срез
     * @throws WrapExceptionsAspectException если не установлен целевой объект или не удалось
     * получить ссылку на метод, около которого установлен срез
     */
    private Method extractWrappedMethod(JoinPoint jp) throws WrapExceptionsAspectException {
        Object target = jp.getTarget();
        if (target == null)
            throw new WrapExceptionsAspectException("No target object specified");
        Class<?> targetClass = target.getClass();

        String methodName = jp.getSignature().getName();
        Class<?> [] argTypes = new Class<?>[jp.getArgs().length];
        for (int i = 0; i < argTypes.length; i++) {
            argTypes[i] = jp.getArgs()[i].getClass();
        }
        try {
            return targetClass.getMethod(methodName, argTypes);
        }
        catch (NoSuchMethodException e) {
            throw new WrapExceptionsAspectException("Wrapped method resolve error");
        }
    }

    /**
     * Получает класс целевого исключения, указанный в аннотации целевого метода {@link WrapExceptions}
     * @param method Целевой метод, рядом с которым установлен срез
     * @return Класс целевого исключения
     * @throws WrapExceptionsAspectException При отсутствии аннотации {@link WrapExceptions} на целевом методе
     */
    private Class<? extends Throwable> extractTargetExceptionClass(Method method)
            throws WrapExceptionsAspectException
    {
        WrapExceptions[] annotations = method.getAnnotationsByType(WrapExceptions.class);
        if (annotations.length == 0)
            throw new WrapExceptionsAspectException("No WrapException annotation on target method");
        return annotations[0].value();
    }

}
