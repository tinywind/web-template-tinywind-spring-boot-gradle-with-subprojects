package org.tinywind.server.controller.api;

import org.tinywind.server.controller.BaseController;
import org.tinywind.server.model.User;
import org.tinywind.server.model.form.LoginForm;
import org.tinywind.server.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthApiController extends BaseController {
    private final UserRepository repository;

    @SneakyThrows
    @PostMapping("login")
    public void login(@RequestBody @Valid LoginForm form, BindingResult bindingResult) {
        final User user = repository.findOneByLoginId(form.getLoginId());

        if (user == null)
            throw new IllegalArgumentException(message.getText("error.login.noexist"));

        if (!BCrypt.checkpw(form.getPassword(), user.getPassword()))
            throw new IllegalArgumentException(message.getText("error.login.password"));

        g.invalidateSession();
        g.setCurrentUser(user);
    }

    @GetMapping("logout")
    public void logout() {
        g.invalidateSession();
    }
}
