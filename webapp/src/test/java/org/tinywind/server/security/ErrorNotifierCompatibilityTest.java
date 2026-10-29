package org.tinywind.server.security;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import ds.portal.config.properties.LogConfigProperties;
import ds.portal.config.web.log.WebhookAppender;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ErrorNotifierCompatibilityTest {
    @Test void sendsStructuredErrorToLoopbackWebhook() throws Exception {
        var received = new ArrayBlockingQueue<String>(1);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/endpoint", exchange -> {
            received.add(new String(exchange.getRequestBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        });
        server.start();
        LoggerContext context = new LoggerContext();
        try {
            LogConfigProperties properties = new LogConfigProperties();
            properties.setService("template-fixture");
            properties.setEndpointUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/endpoint");
            WebhookAppender appender = new WebhookAppender(properties, new ObjectMapper());
            appender.setContext(context); appender.start();
            var logger = context.getLogger("fixture");
            var event = new LoggingEvent(getClass().getName(), logger, Level.ERROR, "fixture-error", null, null);
            appender.doAppend(event);
            String body = received.poll(5, TimeUnit.SECONDS);
            assertNotNull(body);
            var error = new ObjectMapper().readTree(body);
            assertEquals("template-fixture", error.path("service").asText());
            assertEquals("ERROR", error.path("level").asText());
            assertEquals("fixture-error", error.path("formattedMessage").asText());
            appender.stop();
        } finally { context.stop(); server.stop(0); }
    }
}
