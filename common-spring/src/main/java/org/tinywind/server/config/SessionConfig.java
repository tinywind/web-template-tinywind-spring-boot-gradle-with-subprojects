package org.tinywind.server.config;

import org.tinywind.server.service.storage.BasicSessionStorage;
import org.tinywind.server.service.storage.SessionStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSession;

@Configuration
public class SessionConfig {
    @Bean
    public SessionStorage sessionStorage(HttpSession session) {
        return new BasicSessionStorage(session);
    }
}
