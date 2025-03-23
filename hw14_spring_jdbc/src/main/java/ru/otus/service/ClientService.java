package ru.otus.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.ClientRequest;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Iterable<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Transactional
    public Client createClient(ClientRequest client) {
        Address address = new Address(null, client.getAddress().getStreet());
        Set<Phone> phones = client.getPhones().stream()
                .map(phone -> new Phone(null, phone.getNumber()))
                .collect(Collectors.toSet());
        Client forSave = new Client(null, client.getName(), address, phones);
        return clientRepository.save(forSave);
    }

    @Transactional(readOnly = true)
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }


}
