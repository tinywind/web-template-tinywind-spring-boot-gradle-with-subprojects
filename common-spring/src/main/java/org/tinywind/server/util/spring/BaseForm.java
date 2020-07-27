package org.tinywind.server.util.spring;

import org.tinywind.server.util.StringUtils;
import org.springframework.validation.BindingResult;

public abstract class BaseForm {
    public static final String NOT_BLANK = "org.hibernate.validator.constraints.NotBlank.message";
    public static final String NOT_NULL = "javax.validation.constraints.NotNull.message";

    public static void reject(BindingResult result, String field, String message, Object... objects) {

        if (!(message.startsWith("{") && message.endsWith("}"))) {
            message = SpringApplicationContextAware.requestMessage().getText(message, objects);
        } else {
            message = StringUtils.slice(message, 1, -1);
        }
        result.rejectValue(field, null, message);
    }

    public boolean validate(BindingResult bindingResult) {
        return !bindingResult.hasErrors();
    }

    public void beforeValidate(BindingResult bindingResult) {
    }
}