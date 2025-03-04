package ru.otus.repository;

import ru.otus.model.Customer;
import ru.otus.model.Item;

public interface ItemRepository {
    Customer findCustomersByItemId(long itemId);

    void remove(Item item);
}
