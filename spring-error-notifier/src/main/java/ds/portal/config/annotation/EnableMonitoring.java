package ds.portal.config.annotation;

import ds.portal.config.LogbackConfig;
import ds.portal.config.LogbackMdcFilterConfig;
import ds.portal.config.properties.LogConfigProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({LogbackConfig.class, LogbackMdcFilterConfig.class, LogConfigProperties.class})
public @interface EnableMonitoring {
}
