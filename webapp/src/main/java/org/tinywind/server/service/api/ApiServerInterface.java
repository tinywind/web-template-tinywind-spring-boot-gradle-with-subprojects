package org.tinywind.server.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.tinywind.server.service.AbstractRestInterface;
import org.tinywind.server.service.ResultFailException;
import org.tinywind.server.service.UnauthorizedException;
import org.tinywind.server.util.JsonResult;
import org.tinywind.server.util.UrlUtils;
import org.tinywind.server.util.page.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public abstract class ApiServerInterface extends AbstractRestInterface {

    @Value("${apiserver.url}")
    protected String apiServerUrl;

    protected <T> JavaType jsonResultType() {
        return typeFactory.constructType(JsonResult.class);
    }

    protected <T> JavaType jsonResultType(Class<T> dataType) {
        return typeFactory.constructParametricType(JsonResult.class, dataType);
    }

    protected <T> JavaType paginationJsonResultType(Class<T> dataType) {
        final JavaType type = typeFactory.constructParametricType(Pagination.class, dataType);
        return typeFactory.constructParametricType(JsonResult.class, type);
    }

    protected <T> JavaType listJsonResultType(Class<T> dataType) {
        final JavaType type = typeFactory.constructCollectionLikeType(ArrayList.class, dataType);
        return typeFactory.constructParametricType(JsonResult.class, type);
    }

    @Override
    protected <BODY> void post(String url, BODY o) throws HttpStatusCodeException, IOException, ResultFailException {
        final JsonResult<?> result = call(url, o, JsonResult.class, HttpMethod.POST, false);
        if (result.isFailure())
            throw new ResultFailException(result);
    }

    @Override
    protected <BODY> void put(String url, BODY o) throws HttpStatusCodeException, IOException, ResultFailException {
        final JsonResult<?> result = call(url, o, JsonResult.class, HttpMethod.PUT, false);
        if (result.isFailure())
            throw new ResultFailException(result);
    }

    protected void delete(String url) throws HttpStatusCodeException, IOException, ResultFailException {
        final JsonResult<?> result = call(url, null, JsonResult.class, HttpMethod.DELETE, false);
        if (result.isFailure())
            throw new ResultFailException(result);
    }

    @SuppressWarnings("unchecked")
    protected <BODY, RESPONSE> JsonResult<Pagination<RESPONSE>> getPagination(String url, BODY o, Class<RESPONSE> klass) throws IOException, ResultFailException {
        final JsonResult<Pagination<RESPONSE>> result = (JsonResult<Pagination<RESPONSE>>) call(url, o, paginationJsonResultType(klass), HttpMethod.GET, true);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <BODY, RESPONSE> JsonResult<List<RESPONSE>> getList(String url, BODY o, Class<RESPONSE> klass) throws IOException, ResultFailException {
        final JsonResult<List<RESPONSE>> result = (JsonResult<List<RESPONSE>>) call(url, o, listJsonResultType(klass), HttpMethod.GET, true);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <BODY, RESPONSE> JsonResult<RESPONSE> getData(String url, BODY o, JavaType type) throws IOException, ResultFailException {
        final JsonResult<RESPONSE> result = (JsonResult<RESPONSE>) call(url, o, type, HttpMethod.GET, true);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <BODY, RESPONSE> JsonResult<RESPONSE> getData(String url, BODY o, Class<RESPONSE> klass) throws IOException, ResultFailException {
        final JsonResult<RESPONSE> result = (JsonResult<RESPONSE>) call(url, o, jsonResultType(klass), HttpMethod.GET, true);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    protected <BODY, RESPONSE> JsonResult<?> getResult(String url, BODY o) throws IOException, ResultFailException {
        final JsonResult<?> result = call(url, o, JsonResult.class, HttpMethod.GET, true);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <BODY, RESPONSE> JsonResult<Pagination<RESPONSE>> getPagination(HttpMethod method, String url, BODY o, Class<RESPONSE> klass) throws IOException, ResultFailException {
        final JsonResult<Pagination<RESPONSE>> result = (JsonResult<Pagination<RESPONSE>>) call(url, o, paginationJsonResultType(klass), method, true);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <BODY, RESPONSE> JsonResult<List<RESPONSE>> getList(HttpMethod method, String url, BODY o, Class<RESPONSE> klass) throws IOException, ResultFailException {
        final JsonResult<List<RESPONSE>> result = (JsonResult<List<RESPONSE>>) call(url, o, listJsonResultType(klass), method, true);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    protected <BODY, RESPONSE> JsonResult<RESPONSE> getData(HttpMethod method, String url, BODY o, Class<RESPONSE> klass) throws IOException, ResultFailException {
        return getData(method, url, o, klass, true);
    }

    @SuppressWarnings("unchecked")
    protected <BODY, RESPONSE> JsonResult<RESPONSE> getData(HttpMethod method, String url, BODY o, Class<RESPONSE> klass, boolean urlParam) throws IOException, ResultFailException {
        final JsonResult<RESPONSE> result = (JsonResult<RESPONSE>) call(url, o, jsonResultType(klass), method, urlParam);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    protected <BODY, RESPONSE> JsonResult<?> result(HttpMethod method, String url, BODY o) throws IOException, ResultFailException {
        final JsonResult<?> result = call(url, o, JsonResult.class, method, true);
        if (result.isFailure())
            throw new ResultFailException(result);
        return result;
    }

    @Override
    protected <BODY> Object call(String url, BODY o, JavaType javaType, HttpMethod method, boolean urlParam) throws IOException {
        return objectMapper.readValue(getResponse(url, o, method, urlParam), javaType);
    }

    @Override
    protected <BODY, RESPONSE> RESPONSE call(String url, BODY o, Class<RESPONSE> klass, HttpMethod method, boolean urlParam) throws IOException {
        final String response = getResponse(url, o, method, urlParam);
        return objectMapper.readValue(response, klass);
    }

    private <BODY> String getResponse(String url, BODY o, HttpMethod method, boolean urlParam) throws JsonProcessingException {
        url = apiServerUrl + url;

        final RestTemplate template = new RestTemplate(getClientHttpRequestFactory());
        template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.defaultCharset()));

        final HttpHeaders headers = new HttpHeaders();
        if (!method.equals(HttpMethod.GET))
            headers.setContentType(MediaType.APPLICATION_JSON);

        final Object info = getAuthorization();
        if (info != null)
            setAuthorization(headers, info);

        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);

        String paramString;
        try {
            final Map<String, Object> params = o == null ? new HashMap<>() : objectMapper.convertValue(o, typeFactory.constructParametricType(Map.class, String.class, Object.class));
            paramString = objectMapper.writeValueAsString(params);
            if (method.equals(HttpMethod.GET) || urlParam)
                params.forEach((key, value) -> {
                    if (value == null)
                        return;

                    if (value.getClass().isArray()) {
                        if (((Object[]) value).length > 0)
                            uriBuilder.queryParam(key, value);
                    } else if (value instanceof Iterable) {
                        if (((Iterable<?>) value).iterator().hasNext()) {
                            Iterable<?> iter = ((Iterable<?>) value);

                            final List<Object> objects = new ArrayList<>();
                            iter.forEach(objects::add);

                            uriBuilder.queryParam(key, objects.toArray());
                        }
                    } else {
                        uriBuilder.queryParam(key, value);
                    }
                });

            if (log.isInfoEnabled())
                log.info("[" + method + "] " + (method.equals(HttpMethod.GET) || urlParam ? uriBuilder.build().encode().toUri() : url));
        } catch (Exception ignored) {
            paramString = objectMapper.writeValueAsString(o);
        }

        try {
            final ResponseEntity<String> response = method.equals(HttpMethod.GET) || urlParam
                    ? template.exchange(uriBuilder.build().encode().toUri(), method, new HttpEntity<>(headers), String.class)
                    : template.exchange(url, method, new HttpEntity<>(paramString, headers), String.class);

            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.warn(e.getStatusCode() + " : " + e.getResponseBodyAsString());
            if (Objects.equals(e.getStatusCode(), HttpStatus.UNAUTHORIZED))
                throw new UnauthorizedException(e.getStatusText(), e.getResponseHeaders(), e.getResponseBodyAsByteArray(), Charset.defaultCharset());
            throw e;
        }
    }

    protected abstract <INFO> INFO getAuthorization();

    protected abstract <INFO> void setAuthorization(HttpHeaders headers, INFO info);

    protected <BODY> void sendByMultipartFile(HttpMethod method, String url, BODY o, Map<String, FileResource> files) throws IOException, ResultFailException {
        sendByMultipartFile(method, url, o, jsonResultType(), files);
    }

    @SuppressWarnings("unchecked")
    protected <BODY, RESPONSE> RESPONSE sendByMultipartFile(HttpMethod method, String url, BODY o, Class<RESPONSE> klass, Map<String, FileResource> files) throws IOException, ResultFailException {
        return (RESPONSE) sendByMultipartFile(method, url, o, jsonResultType(klass), files);
    }

    protected <BODY> Object sendByMultipartFile(HttpMethod method, String url, BODY o, JavaType responseType, Map<String, FileResource> files) throws IOException, ResultFailException {
        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        if (o != null) {
            final Map<String, Object> params = objectMapper.convertValue(o, typeFactory.constructParametricType(Map.class, String.class, Object.class));
            params.forEach(parts::add);
        }
        files.forEach(parts::add);

        final RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final Object info = getAuthorization();
        if (info != null)
            setAuthorization(headers, info);

        final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);

        try {
            final ResponseEntity<String> response = restTemplate.exchange(apiServerUrl + url, method, requestEntity, String.class);
            final JsonResult<?> result = objectMapper.readValue(response.getBody(), responseType);

            if (result.isFailure())
                throw new ResultFailException(result);
            return result.getData();
        } catch (HttpStatusCodeException e) {
            if (Objects.equals(e.getStatusCode(), HttpStatus.UNAUTHORIZED))
                throw new UnauthorizedException(e.getStatusText(), e.getResponseHeaders(), e.getResponseBodyAsByteArray(), Charset.defaultCharset());
            throw e;
        }
    }

    protected static class FileResource extends FileSystemResource {
        private final String filename;

        public FileResource(String filePath, String filename) {
            super(filePath);
            this.filename = UrlUtils.encode(filename);
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}
