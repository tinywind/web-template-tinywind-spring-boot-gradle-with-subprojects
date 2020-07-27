package org.tinywind.server.controller.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.tinywind.server.util.JsonResult;
import org.tinywind.server.util.page.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractApiControllerTest {
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final TypeFactory typeFactory = objectMapper.getTypeFactory();

    @Autowired
    protected MockMvc mockMvc;

    AbstractApiControllerTest() {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    @BeforeAll
    public static void beforeAll() {
        log.info("AbstractApiControllerTest.beforeAll()");
    }

    @AfterAll
    public static void afterAll() {
        log.info("AbstractApiControllerTest.afterAll()");
    }

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
}
