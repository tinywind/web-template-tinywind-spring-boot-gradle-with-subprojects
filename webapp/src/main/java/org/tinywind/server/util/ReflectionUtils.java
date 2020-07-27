package org.tinywind.server.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

@Slf4j
public class ReflectionUtils {
    public static void copy(Object target, Object source, Class<?> targetClass, String... ignoredFields) {
        final List<String> ignoredFieldList = Arrays.asList(ignoredFields);

        for (final Field field : targetClass.getDeclaredFields()) {
            try {
                final int modifiers = field.getModifiers();
                if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) continue;

                final String fieldName = field.getName();
                if (ignoredFieldList.contains(fieldName))
                    continue;

                final String capName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                final Method setter = targetClass.getDeclaredMethod("set" + capName, field.getType());
                final Object invoked = source.getClass().getMethod("get" + capName).invoke(source);
                setter.invoke(target, invoked);
            } catch (Exception e) {
                log.trace(e.getMessage());
            }
        }
    }

    public static void copy(Object target, Object source, String... ignoredFields) {
        for (Class<?> aClass = target.getClass(); !aClass.equals(Object.class); aClass = aClass.getSuperclass()) {
            copy(target, source, aClass, ignoredFields);
        }
    }

    public static void checkMemberEmptyValue(Object o) throws InvocationTargetException, IllegalAccessException {
        final Stack<String> stack = new Stack<>();
        stack.push("object");
        checkMemberEmptyValue(o, stack);
    }

    private static void checkMemberEmptyValue(Object o, Stack<String> stack) throws InvocationTargetException, IllegalAccessException {
        final Class<?> aClass = o.getClass();

        for (Method method : aClass.getDeclaredMethods()) {
            final String methodName = method.getName();

            if (methodName.startsWith("get")) {

                String objectName = methodName.substring(3);
                objectName = objectName.substring(0, 1).toLowerCase() + objectName.substring(1);
                try {
                    aClass.getDeclaredField(objectName);
                } catch (Exception ignored) {
                    continue;
                }

                final Object invoked = method.invoke(o);

                if (invoked == null) {
                    final StringBuilder builder = new StringBuilder();
                    builder.append("empty : ");
                    for (String aStack : stack) builder.append(aStack).append(".");
                    builder.append(objectName);

                    throw new IllegalStateException(builder.toString());
                }

                stack.push(objectName);
                checkMemberEmptyValue(invoked, stack);
                stack.pop();
            }
        }
    }
}
