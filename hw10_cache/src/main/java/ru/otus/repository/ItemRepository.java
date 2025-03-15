package ru.otus.repository;

import ru.otus.model.Customer;
import ru.otus.model.Item;

public interface ItemRepository {
    Customer findCustomersByItemId(int itemId);

    void remove(Item item);
}
