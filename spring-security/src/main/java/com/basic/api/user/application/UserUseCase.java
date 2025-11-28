package com.basic.api.user.application;

import com.basic.api.user.domain.model.entity.User;

public interface UserUseCase {
    User getUserInfo(Long userId);
}
