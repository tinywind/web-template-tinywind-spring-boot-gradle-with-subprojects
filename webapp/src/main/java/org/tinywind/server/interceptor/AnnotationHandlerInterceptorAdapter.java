package org.tinywind.server.interceptor;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

public class AnnotationHandlerInterceptorAdapter<T extends Annotation> extends BaseHandlerInterceptorAdapter {
    protected final Class<T> klass;

    public AnnotationHandlerInterceptorAdapter(Class<T> klass) {
        this.klass = klass;
    }

    @Override
    final public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        T annotation = getAnnotation(handler);
        return annotation == null || preHandle(request, response, (HandlerMethod) handler, annotation);
    }

    private T getAnnotation(Object handler) {
        return getAnnotation(handler, klass);
    }

    protected <A extends Annotation> A getAnnotation(Object handler, Class<A> annotationClass) {
        if (!(handler instanceof HandlerMethod)) {
            return null;
        }
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final A methodAnnotation = handlerMethod.getMethodAnnotation(annotationClass);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return AnnotationUtils.findAnnotation(handlerMethod.getMethod().getDeclaringClass(), annotationClass);
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, T annotation) throws Exception {
        return true;
    }
}
