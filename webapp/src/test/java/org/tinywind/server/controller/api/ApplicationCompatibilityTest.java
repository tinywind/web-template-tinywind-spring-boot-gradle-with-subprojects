package org.tinywind.server.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.tinywind.server.model.form.UserForm;
import org.tinywind.server.model.enums.UserGrade;
import org.tinywind.server.repository.UserRepository;
import org.tinywind.server.service.FileService;
import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationCompatibilityTest extends AbstractApiControllerTest {
    @Value("${local.server.port}") int port;
    @Autowired UserRepository users;
    @Autowired FileService files;
    @Autowired DataSource primary;
    @Autowired @Qualifier("secondaryDataSource") DataSource secondary;
    @Autowired PlatformTransactionManager primaryTransactions;
    @Autowired @Qualifier("secondaryTransactionManager") PlatformTransactionManager secondaryTransactions;

    private HttpResponse<String> get(HttpClient client, String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(15)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test void servesJspAssetsDocumentationAndHealthOverHttp() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        var login = get(client, "/");
        assertEquals(200, login.statusCode(), login.body());
        assertTrue(login.body().contains("/webjars/jquery/3.7.1/jquery.min.js"));
        for (String path : new String[]{"/webjars/jquery/3.7.1/jquery.min.js", "/webjars/momentjs/2.31.0/moment.min.js", "/webjars/d3js/5.16.0/d3.min.js", "/webjars/jquery-ui/1.14.2/jquery-ui.min.js", "/actuator/health"}) {
            assertEquals(200, get(client, path).statusCode(), path);
        }
        var docs = get(client, "/v3/api-docs/application");
        assertEquals(200, docs.statusCode(), docs.body());
        assertTrue(objectMapper.readTree(docs.body()).has("openapi"), docs.body());
        var config = get(client, "/v3/api-docs/swagger-config");
        assertEquals(200, config.statusCode(), config.body());
        assertTrue(objectMapper.readTree(config.body()).has("urls"), config.body());
        assertEquals(200, get(client, "/swagger-ui/index.html").statusCode());
    }

    @Test void keepsLoginSessionAndLogsOut() throws Exception {
        UserForm user = new UserForm();
        user.setGrade(UserGrade.ADMIN);
        user.setLoginId("http-user");
        user.setPassword(BCrypt.hashpw("fixture-password", BCrypt.gensalt()));
        user.setName("Fixture");
        user.setPhone("000"); user.setPhone2("000");
        user.setEmail("fixture@example.invalid"); user.setBlackout(false); user.setComment("fixture");
        users.insert(user);
        try {
            CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
            HttpClient client = HttpClient.newBuilder().cookieHandler(cookies).build();
            assertEquals(200, get(client, "/").statusCode());
            String json = "{\"loginId\":\"http-user\",\"password\":\"fixture-password\"}";
            var login = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/auth/login"))
                    .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(200, login.statusCode(), login.body());
            assertEquals("success", objectMapper.readTree(login.body()).path("result").asText(), login.body());
            var main = get(client, "/main");
            assertEquals(200, main.statusCode(), main.body());
            assertTrue(main.body().contains(user.getId().toString()), main.body());
            assertEquals(200, get(client, "/api/auth/logout").statusCode());
            assertFalse(get(client, "/main").body().contains(user.getId().toString()));
        } finally { users.delete(user.getId()); }
    }

    @Test void savesAndDownloadsStoredBytesWithLegacyMetadata() throws Exception {
        byte[] data = "stored-content".getBytes(StandardCharsets.UTF_8);
        var id = files.save("report 한글.txt", data);
        Path stored = files.file(id).toPath();
        try {
            assertArrayEquals(data, Files.readAllBytes(stored));
            String url = files.url(id);
            var download = get(HttpClient.newHttpClient(), url);
            assertEquals(200, download.statusCode(), download.body());
            assertEquals("stored-content", download.body());
            assertTrue(download.headers().firstValue("Content-Disposition").orElseThrow().contains("attachment"));
            assertEquals(400, get(HttpClient.newHttpClient(), "/files/download?file=..%2Fprivate.txt").statusCode());
        } finally {
            new JdbcTemplate(primary).update("DELETE FROM \"file\" WHERE \"id\" = ?", id.toString().replace("-", "").toUpperCase(java.util.Locale.ROOT));
            Files.deleteIfExists(stored);
        }
    }

    @Test void uploadsMultipartFileAndKeepsSecondaryFileListingIndependent() throws Exception {
        String boundary = "template-fixture-boundary";
        String body = "--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"upload.txt\"\r\n"
                + "Content-Type: application/octet-stream\r\n\r\nupload-content\r\n--" + boundary + "--\r\n";
        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/files"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), response.body());
        var file = objectMapper.readTree(response.body()).path("data");
        String path = file.path("path").asText();
        String id = file.path("id").asText();
        try {
            assertEquals("upload.txt", file.path("originalName").asText());
            assertEquals(14, file.path("size").asLong());
            assertEquals("upload-content", get(client, files.url(path)).body());
            var other = get(client, "/api/__secondary/files");
            assertEquals(200, other.statusCode(), other.body());
            assertEquals(0, objectMapper.readTree(other.body()).path("data").size(), other.body());
        } finally {
            new JdbcTemplate(primary).update("DELETE FROM \"file\" WHERE \"id\" = ?", id.replace("-", "").toUpperCase(java.util.Locale.ROOT));
            Files.deleteIfExists(files.file(path).toPath());
        }
    }

    @Test void keepsIndependentDatasourcesAndTransactions() {
        JdbcTemplate first = new JdbcTemplate(primary);
        JdbcTemplate second = new JdbcTemplate(secondary);
        assertNotSame(primary, secondary);
        assertNotSame(primaryTransactions, secondaryTransactions);
        first.execute("CREATE TABLE IF NOT EXISTS fixture_transaction (id INTEGER)");
        second.execute("CREATE TABLE IF NOT EXISTS fixture_transaction (id INTEGER)");
        new TransactionTemplate(primaryTransactions).executeWithoutResult(status -> {
            first.update("INSERT INTO fixture_transaction VALUES (1)");
            status.setRollbackOnly();
        });
        new TransactionTemplate(secondaryTransactions).executeWithoutResult(status -> second.update("INSERT INTO fixture_transaction VALUES (2)"));
        assertEquals(0, first.queryForObject("SELECT COUNT(*) FROM fixture_transaction", Integer.class));
        assertEquals(1, second.queryForObject("SELECT COUNT(*) FROM fixture_transaction", Integer.class));
        second.update("DELETE FROM fixture_transaction");
    }
}
