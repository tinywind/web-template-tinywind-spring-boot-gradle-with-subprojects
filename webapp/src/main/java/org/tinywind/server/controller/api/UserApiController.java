package org.tinywind.server.controller.api;

import org.tinywind.server.controller.BaseController;
import org.tinywind.server.model.User;
import org.tinywind.server.model.form.UserForm;
import org.tinywind.server.model.search.UserSearch;
import org.tinywind.server.repository.UserRepository;
import org.tinywind.server.util.page.Pagination;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserApiController extends BaseController {
    private final UserRepository repository;

    @GetMapping("")
    public Pagination<User> pagination(UserSearch search) {
        return new Pagination<>(repository.search(search), repository.count(search), search.getPage(), search.getLimit());
    }

    @GetMapping("search")
    public List<User> search(UserSearch search) {
        search.setPage(null);
        search.setLimit(null);
        return repository.search(search);
    }

    @GetMapping("{id}")
    public User get(@PathVariable UUID id) {
        return repository.findOne(id);
    }

    @PostMapping("")
    public UUID post(@Valid @RequestBody UserForm form, BindingResult bindingResult) {
        form.setPassword(BCrypt.hashpw(form.getPassword(), BCrypt.gensalt()));
        repository.insert(form);
        return form.getId();
    }

    @PutMapping("{id}")
    public void put(@PathVariable UUID id, @Valid @RequestBody UserForm form, BindingResult bindingResult) {
        form.setId(id);
        form.setPassword(BCrypt.hashpw(form.getPassword(), BCrypt.gensalt()));
        repository.update(form);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        repository.delete(id);
    }
}
