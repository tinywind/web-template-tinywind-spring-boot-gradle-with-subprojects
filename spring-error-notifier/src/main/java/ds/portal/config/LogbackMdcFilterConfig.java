package ds.portal.config;

import ds.portal.config.web.filter.LogbackMdcFilter;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class LogbackMdcFilterConfig extends AbstractFilterConfig {

    public LogbackMdcFilterConfig(WebEndpointProperties webEndpointProperties) {
        super(webEndpointProperties);
    }

    @Bean
    public FilterRegistrationBean<LogbackMdcFilter> logbackMdcFilter() {
        FilterRegistrationBean<LogbackMdcFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogbackMdcFilter(this::excluded));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registrationBean;
    }
}
