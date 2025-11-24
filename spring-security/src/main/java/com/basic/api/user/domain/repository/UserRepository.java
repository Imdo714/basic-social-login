package com.basic.api.user.domain.repository;

import com.basic.api.user.domain.model.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
}
