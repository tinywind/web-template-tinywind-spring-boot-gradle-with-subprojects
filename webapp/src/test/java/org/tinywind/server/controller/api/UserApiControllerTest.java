package org.tinywind.server.controller.api;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tinywind.server.model.User;
import org.tinywind.server.model.entity.UserEntity;
import org.tinywind.server.model.enums.UserGrade;
import org.tinywind.server.model.form.UserForm;
import org.tinywind.server.repository.UserRepository;
import org.tinywind.server.util.JsonResult;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class UserApiControllerTest extends AbstractApiControllerTest {
    private static final String BASE_URL = "/api/users/";

    @Autowired
    private UserRepository userRepository;

    @Test
    @SneakyThrows
    public void test() {
        final String loginId = "user3";
        UUID userId = post(loginId, "1234");
        checkPassword(userId, "1234");

        searchByLoginId(loginId);

        put(userId, loginId, "12345");
        checkPassword(userId, "12345");

        delete(userId);
    }

    @Test
    @SneakyThrows
    public void pagination() {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> MockMvcResultMatchers.model().attribute("result", "success"));
    }

    @SneakyThrows
    private void searchByLoginId(String loginId) {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "search")
                .param("loginId", loginId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> MockMvcResultMatchers.model().attribute("result", "success"))
                .andExpect(result -> {
                    final JsonResult<List<UserEntity>> jsonResult = objectMapper.readValue(result.getResponse().getContentAsByteArray(), listJsonResultType(UserEntity.class));
                    assert jsonResult.getData().stream().anyMatch(e -> Objects.equals(e.getLoginId(), loginId));
                });
    }

    @SneakyThrows
    private UUID post(String loginId, String password) {
        final UserForm form = new UserForm();
        form.setGrade(UserGrade.ADMIN);
        form.setLoginId(loginId);
        form.setPassword(password);
        form.setName("name");
        form.setPhone("000-0000-0000");
        form.setPhone2("000-0000-0000");
        form.setEmail("a@a.a");
        form.setBlackout(false);
        form.setComment("comment");

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .content(objectMapper.writeValueAsString(form))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> MockMvcResultMatchers.model().attribute("result", "success"))
                .andExpect(result -> MockMvcResultMatchers.model().attributeExists("data"))
                .andReturn();

        final JsonResult<?> jsonResult = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), JsonResult.class);
        return UUID.fromString(jsonResult.getData().toString());
    }

    @SneakyThrows
    private void put(UUID userId, String loginId, String password) {
        final UserForm form = new UserForm();
        form.setGrade(UserGrade.ADMIN);
        form.setLoginId(loginId);
        form.setPassword(password);
        form.setName("name");
        form.setPhone("000-0000-0000");
        form.setPhone2("000-0000-0000");
        form.setEmail("a@a.a");
        form.setBlackout(false);
        form.setComment("comment");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + userId)
                .content(objectMapper.writeValueAsString(form))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> MockMvcResultMatchers.model().attribute("result", "success"));
    }

    @SneakyThrows
    private void checkPassword(UUID userId, String password) {
        final User user = userRepository.findOne(userId);
        assert BCrypt.checkpw(password, user.getPassword());
    }

    @SneakyThrows
    private void delete(UUID userId) {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> MockMvcResultMatchers.model().attribute("result", "success"));
    }
}
