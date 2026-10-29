package org.tinywind.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.client.ResourceAccessException;
import org.tinywind.server.service.AbstractRestInterface;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HttpsTransportSecurityTest {
    @TempDir Path temporary;

    static class Client extends AbstractRestInterface {
        Client() { objectMapper = new ObjectMapper(); setup(); }
        Object request(String url) throws Exception { return get(url, Map.of(), Map.class); }
    }

    @Test void acceptsTrustedCertificateAndRejectsUnknownIssuerAndWrongHost() throws Exception {
        char[] password = UUID.randomUUID().toString().toCharArray();
        Path storePath = temporary.resolve("fixture.p12");
        Process keytool = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "keytool").toString(),
                "-genkeypair", "-alias", "fixture", "-keyalg", "RSA", "-keysize", "2048", "-validity", "1",
                "-dname", "CN=localhost", "-ext", "SAN=dns:localhost", "-storetype", "PKCS12", "-keystore", storePath.toString(),
                "-storepass", new String(password), "-noprompt").redirectErrorStream(true).start();
        String output = new String(keytool.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(0, keytool.waitFor(), output);
        KeyStore keys = KeyStore.getInstance("PKCS12");
        try (var stream = Files.newInputStream(storePath)) { keys.load(stream, password); }
        KeyManagerFactory managers = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        managers.init(keys, password);
        SSLContext serverTls = SSLContext.getInstance("TLS");
        serverTls.init(managers.getKeyManagers(), null, null);
        HttpsServer server = HttpsServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverTls));
        server.createContext("/", exchange -> {
            byte[] body = "{\"accepted\":true}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var stream = exchange.getResponseBody()) { stream.write(body); }
        });
        server.start();
        var original = HttpsURLConnection.getDefaultSSLSocketFactory();
        try {
            Client client = new Client();
            String trustedHost = "https://localhost:" + server.getAddress().getPort() + "/";
            assertThrows(ResourceAccessException.class, () -> client.request(trustedHost));
            KeyStore trust = KeyStore.getInstance(KeyStore.getDefaultType());
            trust.load(null);
            trust.setCertificateEntry("fixture", keys.getCertificate("fixture"));
            TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagers.init(trust);
            SSLContext trusted = SSLContext.getInstance("TLS");
            trusted.init(null, trustManagers.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(trusted.getSocketFactory());
            assertEquals(Map.of("accepted", true), client.request(trustedHost));
            assertThrows(ResourceAccessException.class,
                    () -> client.request("https://127.0.0.1:" + server.getAddress().getPort() + "/"));
        } finally {
            HttpsURLConnection.setDefaultSSLSocketFactory(original);
            server.stop(0);
        }
    }
}
