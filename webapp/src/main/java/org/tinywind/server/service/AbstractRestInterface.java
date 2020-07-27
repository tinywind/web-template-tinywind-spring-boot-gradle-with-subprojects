package org.tinywind.server.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.tinywind.server.util.JsonResult;
import org.tinywind.server.util.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

@Slf4j
public abstract class AbstractRestInterface {
    static {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    protected ObjectMapper objectMapper;
    protected TypeFactory typeFactory;

    protected static HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(30 * 1000);
        factory.setReadTimeout(30 * 1000);
        return factory;
    }

    @PostConstruct
    public void setup() {
        typeFactory = objectMapper.getTypeFactory();
    }

    protected <BODY> Object get(String url, BODY o, JavaType javaType) throws HttpStatusCodeException, IOException {
        return call(url, o, javaType, HttpMethod.GET, false);
    }

    protected <BODY> void post(String url, BODY o) throws HttpStatusCodeException, IOException, ResultFailException {
        final JsonResult<?> result = post(url, o, JsonResult.class);
        if (result.isFailure())
            throw new ResultFailException(result.getRepresentMessage());
    }

    protected <BODY> void put(String url, BODY o) throws HttpStatusCodeException, IOException, ResultFailException {
        final JsonResult<?> result = put(url, o, JsonResult.class);
        if (result.isFailure())
            throw new ResultFailException(result.getRepresentMessage());
    }

    protected <BODY> void patch(String url, BODY o) throws HttpStatusCodeException, IOException, ResultFailException {
        final JsonResult<?> result = patch(url, o, JsonResult.class);
        if (result.isFailure())
            throw new ResultFailException(result.getRepresentMessage());
    }

    protected <BODY> Object post(String url, BODY o, JavaType javaType) throws HttpStatusCodeException, IOException {
        return call(url, o, javaType, HttpMethod.POST, false);
    }

    protected <BODY> Object put(String url, BODY o, JavaType javaType) throws HttpStatusCodeException, IOException {
        return call(url, o, javaType, HttpMethod.PUT, false);
    }

    protected <BODY> Object patch(String url, BODY o, JavaType javaType) throws HttpStatusCodeException, IOException {
        return call(url, o, javaType, HttpMethod.PATCH, false);
    }

    protected <BODY> Object delete(String url, BODY o, JavaType javaType) throws HttpStatusCodeException, IOException {
        return call(url, o, javaType, HttpMethod.DELETE, false);
    }

    protected <BODY> Object call(String url, BODY o, JavaType javaType, HttpMethod method, boolean urlParam) throws HttpStatusCodeException, IOException {
        final RestTemplate template = new RestTemplate(/*getFactory()*/);
        @SuppressWarnings("unchecked") final Map<String, Object> params = (Map<String, Object>) objectMapper.convertValue(o, Map.class);
        template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.defaultCharset()));

        if (log.isInfoEnabled())
            log.info("[" + method + "] " + (method.equals(HttpMethod.GET) ? UrlUtils.joinQueryString(url, params) : url));

        final HttpHeaders headers = new HttpHeaders();
        if (!method.equals(HttpMethod.GET))
            headers.setContentType(MediaType.APPLICATION_JSON);

        final ResponseEntity<String> response = method.equals(HttpMethod.GET) || urlParam
                ? template.exchange(UrlUtils.joinQueryString(url, params), method, new HttpEntity<>(headers), String.class)
                : template.exchange(url, method, new HttpEntity<>(objectMapper.writeValueAsString(params), headers), String.class);

        return objectMapper.readValue(response.getBody(), javaType);
    }

    protected <BODY, RESPONSE> RESPONSE get(String url, BODY o, Class<RESPONSE> klass) throws HttpStatusCodeException, IOException {
        return call(url, o, klass, HttpMethod.GET, false);
    }

    protected <BODY, RESPONSE> RESPONSE post(String url, BODY o, Class<RESPONSE> klass) throws HttpStatusCodeException, IOException {
        return call(url, o, klass, HttpMethod.POST, false);
    }

    protected <BODY, RESPONSE> RESPONSE put(String url, BODY o, Class<RESPONSE> klass) throws HttpStatusCodeException, IOException {
        return call(url, o, klass, HttpMethod.PUT, false);
    }

    protected <BODY, RESPONSE> RESPONSE patch(String url, BODY o, Class<RESPONSE> klass) throws HttpStatusCodeException, IOException {
        return call(url, o, klass, HttpMethod.PATCH, false);
    }

    protected <BODY, RESPONSE> RESPONSE delete(String url, BODY o, Class<RESPONSE> klass) throws HttpStatusCodeException, IOException {
        return call(url, o, klass, HttpMethod.DELETE, false);
    }

    protected <BODY, RESPONSE> RESPONSE call(String url, BODY o, Class<RESPONSE> klass, HttpMethod method) throws HttpStatusCodeException, IOException {
        return call(url, o, klass, method, false);
    }

    protected <BODY, RESPONSE> RESPONSE call(String url, BODY o, Class<RESPONSE> klass, HttpMethod method, boolean urlParam) throws HttpStatusCodeException, IOException {
        final RestTemplate template = new RestTemplate(/*getFactory()*/);
        @SuppressWarnings("unchecked") final Map<String, Object> params = (Map<String, Object>) objectMapper.convertValue(o, Map.class);
        template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.defaultCharset()));

        if (log.isInfoEnabled())
            log.info("[" + method + "] " + (method.equals(HttpMethod.GET) ? UrlUtils.joinQueryString(url, params) : url));

        final HttpHeaders headers = new HttpHeaders();
        if (!method.equals(HttpMethod.GET))
            headers.setContentType(MediaType.APPLICATION_JSON);

        final ResponseEntity<String> response = method.equals(HttpMethod.GET) || urlParam
                ? template.exchange(UrlUtils.joinQueryString(url, params), method, new HttpEntity<>(headers), String.class)
                : template.exchange(url, method, new HttpEntity<>(objectMapper.writeValueAsString(params), headers), String.class);

        return objectMapper.readValue(response.getBody(), klass);
    }
}
