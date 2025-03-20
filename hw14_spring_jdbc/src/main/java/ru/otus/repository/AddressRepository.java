package ru.otus.repository;

import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.otus.model.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {

    @Query("SELECT * FROM address WHERE client_id = :clientId")
    Optional<Address> findAddressByClientId(Long clientId);
}
