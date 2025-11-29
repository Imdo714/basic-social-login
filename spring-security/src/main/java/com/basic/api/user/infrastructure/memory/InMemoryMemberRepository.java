package com.basic.api.user.infrastructure.memory;

import com.basic.api.user.domain.model.entity.User;
import com.basic.api.user.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMemberRepository implements UserRepository {
    private final Map<Long, User> data = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (user.getId() == null) user.setId(sequence.getAndIncrement());

        data.put(Objects.requireNonNull(user).getId(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return data.values().stream()
                .filter(user -> user.getProviderId().equals(providerId))
                .findFirst();
    }
}
