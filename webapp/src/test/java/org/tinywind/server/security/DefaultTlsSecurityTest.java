package org.tinywind.server.security;

import org.junit.jupiter.api.Test;

import javax.net.ssl.HttpsURLConnection;

import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultTlsSecurityTest {
    @Test void loadingRestInterfaceKeepsJvmTlsValidation() throws Exception {
        var verifier = HttpsURLConnection.getDefaultHostnameVerifier();
        var factory = HttpsURLConnection.getDefaultSSLSocketFactory();
        Class.forName("org.tinywind.server.service.AbstractRestInterface");
        assertSame(verifier, HttpsURLConnection.getDefaultHostnameVerifier());
        assertSame(factory, HttpsURLConnection.getDefaultSSLSocketFactory());
    }
}
