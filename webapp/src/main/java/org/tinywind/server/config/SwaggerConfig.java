package org.tinywind.server.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "user-data.application.debugging", havingValue = "true")
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi applicationApi(@Value("${user-data.application.swagger.api-package}") String apiPackage) {
        return GroupedOpenApi.builder().group("application").packagesToScan(apiPackage).build();
    }
}
