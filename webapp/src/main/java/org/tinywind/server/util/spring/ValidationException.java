package org.tinywind.server.util.spring;

import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

public class ValidationException extends javax.validation.ValidationException {
    private final BindingResult bindingResult;

    public ValidationException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public ValidationException(BindingResult bindingResult) {
        this(bindingResult != null ? toString(bindingResult) : null, bindingResult);
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    private static String toString(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map((br) -> br == null ? "null" : br.getField() + ":" + br.getDefaultMessage())
                .collect(Collectors.joining(","));
    }
}
