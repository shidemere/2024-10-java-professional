package ru.otus.repository;

import java.util.Map;
import org.hibernate.Session;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import ru.otus.cache.MyCache;
import ru.otus.configuration.HibernateConfiguration;
import ru.otus.model.Customer;
import ru.otus.model.Item;

public class ItemRepositoryImpl implements ItemRepository {
    private final MyCache<Long, Item> itemCache;

    public ItemRepositoryImpl(MyCache<Long, Item> itemCache) {
        this.itemCache = itemCache;
    }

    @Override
    public Customer findCustomersByItemId(long itemId) {
        if (itemCache.get(itemId) != null) {
            Item item = itemCache.get(itemId);
            return item.getCustomer();
        }

        try (Session session = HibernateConfiguration.getSession()) {
            session.beginTransaction();

            RootGraph<?> rootGraph = session.getEntityGraph("item_customer");
            Item item = session.find(Item.class, itemId, Map.of(GraphSemantic.LOAD.getJakartaHintName(), rootGraph));

            session.getTransaction().commit();
            itemCache.put(itemId, item);
            return item.getCustomer();
        }
    }

    @Override
    public void remove(Item item) {
        itemCache.remove(item.getId());
        try (Session session = HibernateConfiguration.getSession()) {
            session.beginTransaction();
            session.remove(item);
            session.getTransaction().commit();
        }
    }
}
