package ru.otus.repository;

import java.util.List;
import ru.otus.model.Client;

public interface ClientRepository {

    List<Client> findAll();

    Client save(Client client);
}
