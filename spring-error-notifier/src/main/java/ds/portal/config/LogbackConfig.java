package ds.portal.config;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import ds.portal.config.properties.LogConfigProperties;
import ds.portal.config.web.log.WebhookAppender;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogbackConfig implements InitializingBean {

    private final LogConfigProperties logConfigProperties;
    private final ObjectMapper objectMapper;

    public LogbackConfig(LogConfigProperties logConfigProperties, ObjectMapper objectMapper) {
        this.logConfigProperties = logConfigProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterPropertiesSet() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        WebhookAppender webhookAppender = new WebhookAppender(logConfigProperties, objectMapper);
        webhookAppender.setContext(loggerContext);
        webhookAppender.setName("webhookAppender");
        webhookAppender.start();
        loggerContext.getLogger("ROOT").addAppender(webhookAppender);
    }
}