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
public class RangeValidator implements ConstraintValidator<Range, Object> {
    private static final Logger logger = LoggerFactory.getLogger(RangeValidator.class);
    private int min;
    private int max;
    private String field;
    private String message;
    private boolean useMessageSource;

    @Override
    public void initialize(Range notNull) {
        min = notNull.min();
        max = notNull.max();
        field = notNull.value();
        message = notNull.message();
        useMessageSource = notNull.messageSource();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o == null)
            return true;

        if (o instanceof java.lang.Number) {
            final double value = ((java.lang.Number) o).doubleValue();
            if (min <= value && value <= max)
                return true;
        }

        final RequestMessage requestMessage = SpringApplicationContextAware.requestMessage();
        final String message = requestMessage.getText(this.message, useMessageSource ? requestMessage.getText(field) : field, min, max);
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}