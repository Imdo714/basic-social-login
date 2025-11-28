package com.basic.api.user.application;

import com.basic.api.user.domain.model.entity.User;
import com.basic.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;

    @Override
    public User getUserInfo(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
