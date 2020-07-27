package org.tinywind.server.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.charset.Charset;

public class UnauthorizedException extends HttpStatusCodeException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String statusText) {
        super(HttpStatus.UNAUTHORIZED, statusText);
    }

    public UnauthorizedException(String statusText, byte[] responseBody, Charset responseCharset) {
        super(HttpStatus.UNAUTHORIZED, statusText, responseBody, responseCharset);
    }

    public UnauthorizedException(String statusText, HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {
        super(HttpStatus.UNAUTHORIZED, statusText, responseHeaders, responseBody, responseCharset);
    }
}
