package org.tinywind.server.config;

import org.tinywind.server.web.filter.CachedHttpServletRequestFilter;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig extends AbstractFilterConfig {

    public FilterConfig(WebEndpointProperties webEndpointProperties) {
        super(webEndpointProperties);
    }

    @Bean
    public FilterRegistrationBean<CachedHttpServletRequestFilter> cachedHttpServletRequestFilter() {
        FilterRegistrationBean<CachedHttpServletRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachedHttpServletRequestFilter(this::excluded));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
