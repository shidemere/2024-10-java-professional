package ru.otus.configuration;

import java.util.Properties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.jdbc.DriverDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.otus.model.Customer;
import ru.otus.model.Item;

@Slf4j
public class HibernateConfiguration {
    private static final String URL = "jdbc:postgresql://localhost:5430/main?useUnicode=true&characterEncoding=UTF-8";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static volatile SessionFactory sessionFactory;

    private static SessionFactory getSessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        }
        try {
            Configuration configuration = getConfiguration();

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            log.info("Hibernate Java Config serviceRegistry created");

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            return sessionFactory;
        } catch (Throwable ex) {
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() {
        return getSessionFactory().openSession();
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();

        Properties props = new Properties();

        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.use_sql_comments", "true");

        props.put("hibernate.connection.url", URL);
        props.put("hibernate.connection.username", USERNAME);
        props.put("hibernate.connection.password", PASSWORD);
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.hbm2ddl.auto", "validate");

        configuration.addAnnotatedClass(Customer.class);
        configuration.addAnnotatedClass(Item.class);

        configuration.setProperties(props);
        return configuration;
    }

    public static DataSource getDataSource() {
        return new DriverDataSource(
                HibernateConfiguration.class.getClassLoader(), "org.postgresql.Driver", URL, USERNAME, PASSWORD);
    }
}
