package org.tinywind.server.repository;

import org.tinywind.server.model.User;
import org.tinywind.server.model.form.UserForm;
import org.tinywind.server.model.search.UserSearch;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository {
    List<User> search(UserSearch search);

    Integer count(UserSearch search);

    void insert(UserForm form);

    void update(UserForm form);

    void delete(UUID id);

    User findOne(UUID id);

    User findOneByLoginId(String loginId);

    List<User> findAll();
}