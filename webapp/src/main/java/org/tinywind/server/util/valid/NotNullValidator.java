package org.tinywind.server.util.valid;

import org.tinywind.server.config.RequestMessage;
import org.tinywind.server.util.spring.SpringApplicationContextAware;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author tinywind
 * @since 2016-09-03
 */
public class NotNullValidator implements ConstraintValidator<NotNull, Object> {
    private static final Logger logger = LoggerFactory.getLogger(NotNullValidator.class);
    private String field;
    private String message;
    private boolean useMessageSource;

    @Override
    public void initialize(NotNull notNull) {
        field = notNull.value();
        message = notNull.message();
        useMessageSource = notNull.messageSource();
    }

    @SuppressWarnings("all")
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o == null
                || (o instanceof String && StringUtils.isEmpty((CharSequence) o))
                || (o.getClass().isArray() && ((Object[]) o).length == 0)
                || (o instanceof Iterable && !((Iterable) o).iterator().hasNext())
                ) {
            error(constraintValidatorContext);
            return false;
        }

        return true;
    }

    private void error(ConstraintValidatorContext constraintValidatorContext) {
        final RequestMessage requestMessage = SpringApplicationContextAware.requestMessage();
        final String message = requestMessage.getText(this.message, useMessageSource ? requestMessage.getText(field) : field);
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}