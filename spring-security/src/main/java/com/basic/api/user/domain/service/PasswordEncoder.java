package com.basic.api.user.domain.service;

public interface PasswordEncoder {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
