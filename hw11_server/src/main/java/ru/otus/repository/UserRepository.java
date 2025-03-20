package ru.otus.repository;

import java.util.Optional;
import ru.otus.model.User;

public interface UserRepository {
    Optional<User> findByLogin(String login);
}
