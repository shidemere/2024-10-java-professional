package ru.otus.security;

public interface UserAuthService {
    boolean authenticate(String login, String password);
}
