package org.tinywind.server.util.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class DateTypePropertyEditor<T extends Date> extends PropertyEditorSupport {
    private static final List<String> patterns = Arrays.asList(
            "yyyy-MM-dd HH:mm:ss.SSSSSS",
            "yyyy-MM-dd HH:mm:ss.SSSSS",
            "yyyy-MM-dd HH:mm:ss.SSSS",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss.SS",
            "yyyy-MM-dd HH:mm:ss.S",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "HH:mm:ss.SSSSSS",
            "HH:mm:ss.SSSSS",
            "HH:mm:ss.SSSS",
            "HH:mm:ss.SSS",
            "HH:mm:ss.SS",
            "HH:mm:ss.S",
            "HH:mm:ss",
            "HH:mm");

    private final Class<T> klass;
    private final Function<T, String> converter;

    public DateTypePropertyEditor(Class<T> klass) {
        this.klass = klass;
        this.converter = t -> {
            if (t == null)
                return null;
            return t.toString();
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getAsText() {
        return converter.apply((T) getValue());
    }

    @SneakyThrows
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isEmpty(text)) {
            setValue(null);
            return;
        }

        for (String pattern : patterns) {
            try {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                dateFormat.setLenient(false);
                final Date parsed = dateFormat.parse(text);

                final T t = klass.getConstructor(long.class).newInstance(parsed.getTime());
                setValue(t);
                return;
            } catch (ParseException ignored) {
            }
        }

        try {
            final T t = klass.getConstructor(long.class).newInstance((Long.parseLong(text)));
            setValue(t);
            return;
        } catch (NumberFormatException ignored) {
        }

        throw new IllegalArgumentException("cannot found converter");
    }
}