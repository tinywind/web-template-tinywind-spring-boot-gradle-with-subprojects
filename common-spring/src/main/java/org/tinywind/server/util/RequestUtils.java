package org.tinywind.server.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Supplier;

public class RequestUtils {

    public static <T> T getAttribute(String name, Supplier<T> defaultValueConstructor) {
        final HttpServletRequest request = getCurrentRequest();
        @SuppressWarnings("unchecked") T object = (T) request.getAttribute(name);
        if (object != null)
            return object;

        final T value = defaultValueConstructor.get();
        request.setAttribute(name, value);
        return value;
    }

    public static HttpServletRequest getCurrentRequest() {
        return getRequestAttributes().getRequest();
    }

    private static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static boolean isPost() {
        return isPost(getCurrentRequest());
    }

    public static boolean isPost(HttpServletRequest request) {
        return request.getMethod().equals("POST");
    }

    public static String getCurrentUrlPrefix() {
        final HttpServletRequest request = getCurrentRequest();
        String prefix = request.getScheme() + "://" + request.getServerName();
        final int port = request.getServerPort();
        if (port != 80) {
            prefix = prefix + ":" + port;
        }
        return prefix;
    }

    public static <T> Optional<T> context(Supplier<T> body) {
        if (getRequestAttributes() == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(body.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
