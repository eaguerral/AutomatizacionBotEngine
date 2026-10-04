package com.botengine.framework.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(
            ApplicationContext applicationContext) {

        context = applicationContext;
    }

    public static <T> T getBean(
            Class<T> tipo) {

        if (context == null) {
            throw new IllegalStateException(
                    "El contexto de Spring no se encuentra disponible."
            );
        }

        return context.getBean(tipo);
    }
}