package org.tinywind.server.util.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tinywind
 * @since 2016-09-03
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotNullValidator.class)
@Documented
public @interface NotNull {
    String value() default "필드";

    String message() default "validation.blank";

    boolean messageSource() default false;

    @SuppressWarnings("UnusedDeclaration") Class<?>[] groups() default {};

    @SuppressWarnings("UnusedDeclaration") Class<? extends Payload>[] payload() default {};
}
