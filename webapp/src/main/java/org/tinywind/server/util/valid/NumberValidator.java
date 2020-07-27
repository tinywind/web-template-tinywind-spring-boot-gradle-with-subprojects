package org.tinywind.server.util.valid;

import org.tinywind.server.config.RequestMessage;
import org.tinywind.server.util.spring.SpringApplicationContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author tinywind
 * @since 2016-09-03
 */
public class NumberValidator implements ConstraintValidator<Number, Object> {
    private static final Logger logger = LoggerFactory.getLogger(NotNullValidator.class);

    private Integer number;
    private String field;
    private String message;
    private boolean useMessageSource;

    @Override
    public void initialize(Number Number) {
        number = Number.number();
        field = Number.value();
        message = Number.message();
        useMessageSource = Number.messageSource();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o == null) {
            logger.warn(field + "is null.");
            return true;
        }

        if (!o.getClass().isArray() && !(o instanceof Iterable)) {
            logger.warn(field + "is must array of iterable.");
            return true;
        }

        if (o.getClass().isArray()) {
            if (((Object[]) o).length != number) {
                error(constraintValidatorContext);
                return false;
            }
            return true;
        }

        // o instanceof Iterable
        int size = 0;
        for (Object ignored : ((Iterable) o)) {
            size++;
        }

        if (size != number) {
            error(constraintValidatorContext);
            return false;
        }

        return true;
    }

    private void error(ConstraintValidatorContext constraintValidatorContext) {
        final RequestMessage requestMessage = SpringApplicationContextAware.requestMessage();
        final String message = requestMessage.getText(this.message, useMessageSource ? requestMessage.getText(field) : field, number);
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}