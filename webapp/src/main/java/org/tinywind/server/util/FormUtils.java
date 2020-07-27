package org.tinywind.server.util;

import org.tinywind.server.config.RequestMessage;
import org.tinywind.server.util.mybatis.CodeHasable;
import org.tinywind.server.util.spring.SpringApplicationContextAware;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormUtils {
    public static <T extends Enum<?>> LinkedHashMap<String, String> options(Class<T> enumClass) {
        return options(false, enumClass);
    }

    public static <T extends Enum<?>> LinkedHashMap<String, String> options(boolean withWholeExpression, Class<T> enumClass) {
        return options(withWholeExpression, enumClass.getEnumConstants());
    }

    public static <T extends Enum<?>> LinkedHashMap<String, String> options(T... values) {
        return options(false, values);
    }

    public static <T extends Enum<?>> LinkedHashMap<String, String> options(boolean withWholeExpression, T... values) {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (withWholeExpression)
            map.put("", requestMessage().getText("text.whole"));

        for (T e : values)
            map.put(e.name(), requestMessage().getEnumText(e));

        return map;
    }

    public static <E extends Enum<E> & CodeHasable<C>, C> LinkedHashMap<C, String> optionsOfCode(Class<E> enumClass) {
        return optionsOfCode(enumClass.getEnumConstants());
    }

    public static <E extends Enum<E> & CodeHasable<C>, C> LinkedHashMap<C, String> optionsOfCode(E... values) {
        final LinkedHashMap<C, String> map = new LinkedHashMap<>();
        for (E e : values)
            map.put(e.getCode(), requestMessage().getEnumText(e));
        return map;
    }

    public static Map<String, String> booleanOptions() {
        return booleanOptions(false);
    }

    public static Map<String, String> booleanOptions(boolean withWholeExpression) {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (withWholeExpression)
            map.put("", requestMessage().getText("text.whole"));

        map.put("true", requestMessage().getText("Boolean.TRUE"));
        map.put("false", requestMessage().getText("Boolean.FALSE"));

        return map;
    }

    protected static RequestMessage requestMessage() {
        return SpringApplicationContextAware.requestMessage();
    }
}
