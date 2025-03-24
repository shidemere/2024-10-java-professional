package ru.otus.service;

import java.util.List;
import ru.otus.model.Client;

public interface ClientService {

    Client saveClient(Client client);

    List<Client> findAll();
}
