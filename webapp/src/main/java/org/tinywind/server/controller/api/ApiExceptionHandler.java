package org.tinywind.server.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.tinywind.server.Constants;
import org.tinywind.server.config.RequestGlobal;
import org.tinywind.server.service.ResultFailException;
import org.tinywind.server.service.UnauthorizedException;
import org.tinywind.server.util.JsonResult;
import org.tinywind.server.util.spring.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientResponseException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;
import java.util.Arrays;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler {

    private final RequestGlobal g;
    private final ObjectMapper objectMapper;

    @ExceptionHandler(SQLException.class)
    public void sqlException(HttpServletResponse response, Exception e) throws IOException {
        writeResponse(response, e, HttpServletResponse.SC_PRECONDITION_FAILED, "옯바르지 않은 입력값");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentException(HttpServletResponse response, Exception e) throws IOException {
        writeResponse(response, e, HttpServletResponse.SC_PRECONDITION_FAILED, "옯바르지 않은 입력값");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public void duplicateKeyException(HttpServletResponse response, Exception e) throws IOException {
        writeResponse(response, e, HttpServletResponse.SC_PRECONDITION_FAILED, "이미 존재하는 정보입니다.");
    }

    @ExceptionHandler(NullPointerException.class)
    public void nullPointerException(HttpServletResponse response, Exception e) throws IOException {
        writeResponse(response, e, HttpServletResponse.SC_PRECONDITION_FAILED, "정의되지 않은 조건");
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public void unsupportedOperationException(HttpServletResponse response, Exception e) throws IOException {
        writeResponse(response, e, HttpServletResponse.SC_NOT_FOUND, "NOT FOUND API");
    }

    @ExceptionHandler(SQLNonTransientException.class)
    public void sqlNonTransientException(HttpServletResponse response, Exception e) throws IOException {
        writeResponse(response, new Exception("장비 접근에 실패했습니다.(DB)", e), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "장비 접근에 실패했습니다.(DB)");
    }

    @ExceptionHandler(ResultFailException.class)
    public void resultFailException(HttpServletResponse response, ResultFailException e) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
        response.getWriter().print(objectMapper.writeValueAsString(e.getResult()));
    }

    @ExceptionHandler(value = {HttpStatusCodeException.class})
    public void httpClientErrorException(HttpServletResponse response, RestClientResponseException e) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.setStatus(e.getRawStatusCode());
        if (e.getRawStatusCode() == HttpServletResponse.SC_UNAUTHORIZED)
            JsonResult.create("API 서버 로그인이 필요합니다.");

        try {
            objectMapper.readValue(e.getResponseBodyAsByteArray(), JsonResult.class);
        } catch (IOException e1) {
            log.error(e1.getMessage(), e1);
        }
        response.getWriter().print(e.getResponseBodyAsString());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public void unauthorizedException(HttpServletResponse response, UnauthorizedException e) throws IOException {
        g.invalidateSession();
        writeResponse(response, e, HttpServletResponse.SC_UNAUTHORIZED, "허가되지 않은 접근");
    }

    @ExceptionHandler(ValidationException.class)
    public void validationException(HttpServletResponse response, ValidationException e) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);

        final JsonResult<?> jsonResult = JsonResult.create(e.getBindingResult());
        if (StringUtils.isNotEmpty(e.getMessage()))
            jsonResult.setReason(e.getMessage());

        response.getWriter().print(objectMapper.writeValueAsString(jsonResult));
    }

    @ExceptionHandler(javax.validation.ValidationException.class)
    public void validationException(HttpServletResponse response, javax.validation.ValidationException e) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
        response.getWriter().print(objectMapper.writeValueAsString(JsonResult.create(e.getMessage())));
    }

    @ExceptionHandler(UncheckedIOException.class)
    public void uncheckedIOException(HttpServletResponse response, Exception e) throws IOException {
        if (StringUtils.isEmpty(e.getMessage()))
            writeResponse(response, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "시스템 예외 발생");

        final String exceptionString = "Exception:";
        final int exceptionIndex = e.getMessage().indexOf(exceptionString);

        if (exceptionIndex >= 0) {
            printException(e);
            writeResponse(response, new Exception(e.getMessage().substring(exceptionIndex + exceptionString.length() + 1)), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "시스템 예외 발생");
        } else {
            writeResponse(response, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "시스템 예외 발생");
        }
    }

    @ExceptionHandler(Exception.class)
    public void exception(HttpServletResponse response, Exception e) throws IOException {
        writeResponse(response, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "시스템 예외 발생");
    }

    private void writeResponse(HttpServletResponse response, Exception e, int status, String defaultMessage) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.setStatus(status);
        printException(e);

        final String message = e.getMessage();
        response.getWriter().print(objectMapper.writeValueAsString(JsonResult.create(StringUtils.isEmpty(message) ? defaultMessage : message)));
    }

    private void printException(Exception e) {
        log.info(e.getMessage());
        Arrays.stream(e.getStackTrace())
                .filter(exception -> exception.getClassName().startsWith(Constants.BASE_PACKAGE) && StringUtils.isNotEmpty(exception.getFileName()) && !exception.getFileName().startsWith("<"))
                .forEach(exception -> log.info("\t" + exception.toString()));
    }
}
