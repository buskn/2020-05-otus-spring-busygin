package ru.otus.hw2.utils.debug;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.otus.hw2.App;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Аспект отладочной информации
 */
@Aspect
@Component
@Slf4j
public class DebugAspect {
    private final String pkg = App.class.getPackageName();
    private final boolean isDebugMode;

    public DebugAspect(@Value("${hw2.debug:false}") boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
    }

    @Pointcut("@annotation(annotation)")
    public void pointcut(Debug annotation) {}

    @Around("pointcut(annotation)")
    public void debug(ProceedingJoinPoint jp, Debug annotation) throws Throwable {
        if (isDebugMode) {
            String target = jp.getTarget().getClass().getCanonicalName() +
                    "." + jp.getSignature().getName() + "()";
            log.info("DEBUG INFO for invocation of " + target);
            log.info("with args: " + Arrays.toString(jp.getArgs()));
            log.info(getAppStack());

            long startTime = System.currentTimeMillis();
            jp.proceed();
            double time = ((double)System.currentTimeMillis() - startTime) / 1000;

            log.info("{} consumed time: {} sec", target, time);
        }
        else {
            jp.proceed();
        }
    }

    private String getAppStack() {
        return Arrays.stream(new Throwable().getStackTrace())
                .map(s -> s.getClassName() + "." + s.getMethodName() + "()")
                .filter(s -> s.startsWith(pkg))
                .skip(1)
                .collect(Collectors.joining("\n", "app stack: \n", ""));
    }
}
