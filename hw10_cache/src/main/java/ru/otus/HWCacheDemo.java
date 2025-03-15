package ru.otus;

import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.HwListener;
import ru.otus.cache.MyCache;
import ru.otus.configuration.HibernateConfiguration;
import ru.otus.model.Customer;
import ru.otus.model.Item;
import ru.otus.repository.CustomerRepository;
import ru.otus.repository.CustomerRepositoryImpl;
import ru.otus.repository.ItemRepository;
import ru.otus.repository.ItemRepositoryImpl;

@Slf4j
public class HWCacheDemo {
    private static final Logger logger = LoggerFactory.getLogger(HWCacheDemo.class);

    public static void main(String[] args) {
        flywayMigrations(HibernateConfiguration.getDataSource());
        new HWCacheDemo().demo();
    }

    private void demo() {

        // пример, когда Idea предлагает упростить код, при этом может появиться "спец"-эффект
        @SuppressWarnings("java:S1604")
        HwListener<Integer, Customer> customerListener = new HwListener<Integer, Customer>() {
            @Override
            public void notify(Integer key, Customer value, String action) {
                logger.info("!Customer Listener! key:{}, value:{}, action: {}", key, value, action);
            }
        };
        MyCache<Integer, Customer> customerCache = new MyCache<>();
        customerCache.addListener(customerListener);

        HwListener<Integer, Item> itemListener = new HwListener<Integer, Item>() {
            @Override
            public void notify(Integer key, Item value, String action) {
                logger.info("!Item Listener! key:{}, value:{}, action: {}", key, value, action);
            }
        };
        MyCache<Integer, Item> itemCache = new MyCache<>();
        itemCache.addListener(itemListener);

        CustomerRepository customerRepository = new CustomerRepositoryImpl(customerCache);
        List<Item> itemsByCustomerId = customerRepository.getItemsByCustomerId(1);
        System.out.printf("Items for customer with ID=%d is: {%s}%n", 1, itemsByCustomerId);

        ItemRepository itemRepository = new ItemRepositoryImpl(itemCache);
        Customer customer = itemRepository.findCustomersByItemId(3);
        System.out.printf("Customer for item with ID=%d is: {%s}%n", 3, customer);

        System.out.printf("Prepare for deleting item {%s}%n", itemsByCustomerId.get(0));
        itemRepository.remove(itemsByCustomerId.get(0));

        System.out.printf("Prepare for deleting customer {%s}%n", customer);
        customerRepository.remove(customer);
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
