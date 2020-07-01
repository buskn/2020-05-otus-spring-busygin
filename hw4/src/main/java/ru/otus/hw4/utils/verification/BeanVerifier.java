package ru.otus.hw4.utils.verification;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
@Slf4j
public class BeanVerifier implements ApplicationContextAware {
    private ApplicationContext ctx;

    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        verify();
    }

    public void verify() {
        ctx.getBeansWithAnnotation(Verifiable.class).forEach( (name, bean) -> {
            val annot = getVerifiableAnnotation(bean);
            Method method = getVerifyMethod(bean.getClass(), annot.value());
            invoke(method, bean);
        });
    }

    private Verifiable getVerifiableAnnotation(Object bean) {
        Verifiable[] annotations = bean.getClass().getAnnotationsByType(Verifiable.class);
        if (annotations.length == 0)
            throw new VerifyException("No Verifiable annotation on bean " + bean);
        return annotations[0];
    }

    private Method getVerifyMethod(Class<?> clazz, String methodName) {
        try {
            return clazz.getMethod(methodName);
        }
        catch (NoSuchMethodException e) {
            throw new VerifyException(e);
        }
    }

    private void invoke(Method method, Object bean) {
        try {
            method.invoke(bean);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new VerifyException(e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
