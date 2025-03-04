package ru.otus.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import ru.otus.cache.MyCache;
import ru.otus.configuration.HibernateConfiguration;
import ru.otus.model.Customer;
import ru.otus.model.Item;

public class CustomerRepositoryImpl implements CustomerRepository {
    private final MyCache<Long, Customer> cache;

    public CustomerRepositoryImpl(MyCache<Long, Customer> cache) {
        this.cache = cache;
    }

    @Override
    public List<Item> getItemsByCustomerId(long customerId) {
        if (cache.get(customerId) != null) {
            Customer customer = cache.get(customerId);
            return customer.getItems();
        }

        try (Session session = HibernateConfiguration.getSession()) {
            session.beginTransaction();
            RootGraph<?> rootGraph = session.getEntityGraph("customer_items");

            Customer customer = session.find(
                    Customer.class, customerId, Map.of(GraphSemantic.LOAD.getJakartaHintName(), rootGraph));
            session.getTransaction().commit();
            cache.put(customerId, customer);
            return customer.getItems();
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void remove(Customer customer) {
        cache.remove(customer.getId());

        try (Session session = HibernateConfiguration.getSession()) {
            session.beginTransaction();
            session.remove(customer);
            session.getTransaction().commit();
        }
    }
}
