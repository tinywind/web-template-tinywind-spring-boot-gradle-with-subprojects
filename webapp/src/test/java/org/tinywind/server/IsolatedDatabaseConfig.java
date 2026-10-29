package org.tinywind.server;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.UUID;

@TestConfiguration
public class IsolatedDatabaseConfig {
    public static String newGuid() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase(java.util.Locale.ROOT);
    }

    @Bean
    static org.springframework.beans.factory.config.BeanFactoryPostProcessor isolateDemoTimers() {
        return factory -> {
            var registry = (org.springframework.beans.factory.support.BeanDefinitionRegistry) factory;
            registry.removeBeanDefinition("job1");
            registry.removeBeanDefinition("job2");
        };
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    com.icegreen.greenmail.util.GreenMail smtpFixture() {
        return new com.icegreen.greenmail.util.GreenMail(new com.icegreen.greenmail.util.ServerSetup(
                0, "127.0.0.1", com.icegreen.greenmail.util.ServerSetup.PROTOCOL_SMTP));
    }

    @Bean
    org.springframework.mail.javamail.JavaMailSenderImpl fixtureMailSender(com.icegreen.greenmail.util.GreenMail smtp) {
        var sender = new org.springframework.mail.javamail.JavaMailSenderImpl();
        sender.setHost("127.0.0.1");
        sender.setPort(smtp.getSmtp().getPort());
        return sender;
    }

    @Bean
    DataSourceInitializer primaryFixture(DataSource dataSource) {
        return initialize(dataSource);
    }

    @Bean
    DataSourceInitializer secondaryFixture(@Qualifier("secondaryDataSource") DataSource dataSource) {
        return initialize(dataSource);
    }

    private DataSourceInitializer initialize(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("fixture-schema.sql")));
        return initializer;
    }
}
