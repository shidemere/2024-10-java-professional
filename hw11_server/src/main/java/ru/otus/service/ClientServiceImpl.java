package ru.otus.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.model.Client;
import ru.otus.repository.ClientRepository;

@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    @Override
    public Client saveClient(Client client) {
        return repository.save(client);
    }

    @Override
    public List<Client> findAll() {
        return repository.findAll();
    }
}
