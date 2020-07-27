package org.tinywind.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConditionalOnProperty(value = "user-data.application.debugging", havingValue = "true")
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket(@Value("${user-data.application.swagger.api-package}") String apiPackage) {
        return new Docket(DocumentationType.SPRING_WEB)
                .select()
                .apis(RequestHandlerSelectors.basePackage(apiPackage))
                .paths(PathSelectors.any())
                .build();
    }

}