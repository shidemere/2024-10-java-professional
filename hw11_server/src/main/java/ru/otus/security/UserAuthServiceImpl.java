package ru.otus.security;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import ru.otus.model.User;
import ru.otus.repository.UserRepository;

@Slf4j
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository repository;

    public UserAuthServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean authenticate(String login, String password) {
        log.info("Попытка аутентификации для логина: {}", login);
        Optional<User> user = repository.findByLogin(login);
        boolean isAllowed = user.map(u -> u.getPassword().equals(password)).orElse(false);
        log.info("Результат аутентификации для {}: {}", login, isAllowed);
        return isAllowed;
    }
}
