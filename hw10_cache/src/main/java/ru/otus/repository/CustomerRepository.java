package ru.otus.repository;

import java.util.List;
import ru.otus.model.Customer;
import ru.otus.model.Item;

public interface CustomerRepository {

    List<Item> getItemsByCustomerId(long customerId);

    void remove(Customer customer);
}
