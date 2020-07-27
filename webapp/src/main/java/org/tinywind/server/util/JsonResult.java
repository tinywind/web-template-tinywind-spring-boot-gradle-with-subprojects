package org.tinywind.server.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author tinywind
 * @since 2018-01-13
 */
@Getter
@Setter
public class JsonResult<T> implements Serializable {
    private Result result;
    private String reason;
    private T data;
    private List<FieldError> fieldErrors;

    public JsonResult() {
        this(Result.success);
    }

    public JsonResult(Result result) {
        this(result, null);
    }

    public JsonResult(Result result, String message) {
        this(result, message, null);
    }

    public JsonResult(Exception e) {
        this(Result.failure, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
    }

    public JsonResult(BindingResult bindingResult) {
        this(bindingResult.hasErrors() ? Result.failure : Result.success, null, bindingResult);
    }

    public JsonResult(Result result, String message, BindingResult bindingResult) {
        setFieldErrors(new ArrayList<>());
        setResult(result);
        setReason(message);
        if (bindingResult != null)
            setBindingResult(bindingResult);
    }

    public static <T> JsonResult<T> data(T data) {
        final JsonResult<T> result = new JsonResult<>();
        result.setData(data);
        return result;
    }

    public static <T> JsonResult<T> create() {
        return new JsonResult<>();
    }

    public static <T> JsonResult<T> create(Result result) {
        return new JsonResult<>(result);
    }

    public static <T> JsonResult<T> create(Result result, String reason) {
        return new JsonResult<>(result, reason);
    }

    public static <T> JsonResult<T> create(Result result, String reason, BindingResult bindingResult) {
        return new JsonResult<>(result, reason, bindingResult);
    }

    public static <T> JsonResult<T> create(Exception e) {
        return new JsonResult<>(e);
    }

    public static <T> JsonResult<T> create(BindingResult bindingResult) {
        return new JsonResult<>(bindingResult);
    }

    public static <T> JsonResult<T> create(String reason) {
        return new JsonResult<>(Result.failure, reason);
    }

    public void setBindingResult(BindingResult bindingResult) {
        setResult(bindingResult.hasErrors() ? Result.failure : Result.success);

        List<FieldError> fieldErrors = getFieldErrors();
        if (fieldErrors == null)
            fieldErrors = new ArrayList<>();

        for (org.springframework.validation.FieldError error : bindingResult.getFieldErrors())
            fieldErrors.add(new FieldError(error.getField(), error.isBindingFailure(), error.getDefaultMessage()));

        setFieldErrors(fieldErrors);

        if (bindingResult.getGlobalError() != null)
            setReason(bindingResult.getGlobalError().getDefaultMessage());
    }

    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(result, Result.success);
    }

    @JsonIgnore
    public boolean isFailure() {
        return !isSuccess();
    }

    @JsonIgnore
    public String getRepresentMessage() {
        if (reason != null)
            return reason;

        if (fieldErrors != null && fieldErrors.size() > 0)
            return fieldErrors.get(0).getDefaultMessage();

        return null;
    }

    public enum Result {
        success, failure
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class FieldError {
        private String field;
        private Boolean typeMatchError;
        private String defaultMessage;
    }
}
