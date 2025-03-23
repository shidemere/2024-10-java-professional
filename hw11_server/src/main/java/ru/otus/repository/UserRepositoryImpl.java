package ru.otus.repository;

import jakarta.persistence.NoResultException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import ru.otus.configuration.HibernateConfiguration;
import ru.otus.model.User;

@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final HibernateConfiguration hibernateConfiguration;

    @Override
    public Optional<User> findByLogin(String login) {
        try (Session session = hibernateConfiguration.getSession()) {
            return Optional.ofNullable(session.createQuery("FROM User WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
