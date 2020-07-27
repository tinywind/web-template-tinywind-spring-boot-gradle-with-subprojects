package org.tinywind.server.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractFilterConfig {
    protected final WebEndpointProperties webEndpointProperties;

    public AbstractFilterConfig(WebEndpointProperties webEndpointProperties) {
        this.webEndpointProperties = webEndpointProperties;
    }

    protected boolean excluded(HttpServletRequest request) {
        String originatingRequestUri = new UrlPathHelper().getOriginatingRequestUri(request);
        String basePath = webEndpointProperties.getBasePath();
        return originatingRequestUri.startsWith(basePath);
    }

}
