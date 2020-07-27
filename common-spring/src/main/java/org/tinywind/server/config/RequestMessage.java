package org.tinywind.server.config;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@AllArgsConstructor
@Component
public class RequestMessage {
    private final MessageSource source;

    public String getText(String code) {
        return getText(code, new Object[0]);
    }

    public String getText(String code, Object... objects) {
        return source.getMessage(code, objects, "Message not found. code: " + code, Locale.getDefault());
    }

    public String getEnumText(Enum<?> value) {
        return getText(value.getClass().getName() + "." + value.name());
    }

    public String getEnumText(Object e, String member, Object value) {
        if (e == null || value == null) return "";
        return getText(e.getClass().getName() + "." + member + "." + value.toString());
    }
}
