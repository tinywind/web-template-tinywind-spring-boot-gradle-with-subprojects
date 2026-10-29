package org.tinywind.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.tinywind.server.service.AbstractRestInterface;
import org.tinywind.server.service.api.ApiServerInterface;
import org.springframework.http.HttpHeaders;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PatchTransportCompatibilityTest {
    static class Client extends AbstractRestInterface {
        Client() { objectMapper = new ObjectMapper(); setup(); }
        Object patchRequest(String url) throws Exception { return patch(url, Map.of("value", "updated"), Map.class); }
        Object patchWithFactory(String url) { return new RestTemplate(getClientHttpRequestFactory()).patchForObject(url, Map.of("value", "updated"), Map.class); }
    }

    static class ApiClient extends ApiServerInterface {
        ApiClient() { objectMapper = new ObjectMapper(); setup(); }
        Object patchRequest(String url) throws Exception {
            apiServerUrl = url;
            return patch("", Map.of("value", "updated"), Map.class);
        }
        @Override protected <INFO> INFO getAuthorization() { return null; }
        @Override protected <INFO> void setAuthorization(HttpHeaders headers, INFO info) { }
    }

    @Test void sendsPatchBodyThroughBothTransportsAndPreservesErrorStatus() throws Exception {
        var received = new ArrayBlockingQueue<String>(2);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            received.add(exchange.getRequestMethod() + " " + new String(exchange.getRequestBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
            byte[] body = "{\"accepted\":true}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(exchange.getRequestURI().getPath().equals("/error") ? 422 : 202, body.length);
            try (var stream = exchange.getResponseBody()) { stream.write(body); }
        });
        server.start();
        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/";
            Client client = new Client();
            assertEquals(Map.of("accepted", true), client.patchRequest(url));
            assertEquals("PATCH {\"value\":\"updated\"}", received.poll(5, TimeUnit.SECONDS));
            assertEquals(Map.of("accepted", true), client.patchWithFactory(url));
            assertEquals("PATCH {\"value\":\"updated\"}", received.poll(5, TimeUnit.SECONDS));
            assertEquals(Map.of("accepted", true), new ApiClient().patchRequest(url));
            assertEquals("PATCH {\"value\":\"updated\"}", received.poll(5, TimeUnit.SECONDS));
            var error = assertThrows(HttpStatusCodeException.class, () -> new ApiClient().patchRequest(url + "error"));
            assertEquals(422, error.getStatusCode().value());
        } finally { server.stop(0); }
    }
}
