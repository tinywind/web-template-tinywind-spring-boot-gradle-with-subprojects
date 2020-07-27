package org.tinywind.server.util.spring;

import org.tinywind.server.config.RequestMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public class SpringApplicationContextAware implements ApplicationContextAware {
    protected static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
    }

    public static MessageSource messageSource() {
        return (MessageSource) applicationContext.getBean("messageSource");
    }

    public static RequestMessage requestMessage() {
        return (RequestMessage) applicationContext.getBean("requestMessage");
    }

    public static Validator validator() {
        return (Validator) applicationContext.getBean("validator");
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextAware.applicationContext = applicationContext;
    }
}
