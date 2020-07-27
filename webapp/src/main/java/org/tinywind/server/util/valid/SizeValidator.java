package org.tinywind.server.util.valid;

import org.tinywind.server.util.spring.SpringApplicationContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author tinywind
 * @since 2016-09-03
 */
public class SizeValidator implements ConstraintValidator<Size, MultipartFile> {
    private static final Logger logger = LoggerFactory.getLogger(SizeValidator.class);
    private long size;
    private String message;

    private static String humanizeSize(long size) {
        final String[] units = new String[]{"KB", "MB", "GB", "TB"};
        int idx = -1;
        do {
            size /= 1024;
            ++idx;
        } while (size > 1024);

        return size + units[idx];
    }

    @Override
    public void initialize(Size size) {
        this.size = size.value();
        this.message = size.message();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        final boolean valid = value == null || value.getSize() <= this.size;
        if (!valid) {
            final String message = SpringApplicationContextAware.requestMessage().getText(this.message, humanizeSize(this.size));
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return valid;
    }
}
