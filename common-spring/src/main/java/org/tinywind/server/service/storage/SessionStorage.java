package org.tinywind.server.service.storage;

import java.util.function.Supplier;

public interface SessionStorage {
    void set(String sessionId, String key, Object value);

    <T> T get(String sessionId, String key, Class<T> returnType);

    default <T> T get(String sessionId, String key, Class<T> returnType, Supplier<T> defaultValueSupplier) {
        final T value = get(sessionId, key, returnType);
        if (value != null) return value;

        final T t = defaultValueSupplier.get();
        set(sessionId, key, t);
        return t;
    }

    void remove(String sessionId, String key);

    void expire(String sessionId);
}
