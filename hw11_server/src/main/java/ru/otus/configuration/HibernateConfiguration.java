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
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.model.User;

@Slf4j
public class HibernateConfiguration {
    private final String URL = "jdbc:postgresql://localhost:5430/main?useUnicode=true&characterEncoding=UTF-8";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private SessionFactory sessionFactory;

    private SessionFactory getSessionFactory() {
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

    public Session getSession() {
        return getSessionFactory().openSession();
    }

    private Configuration getConfiguration() {
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

        configuration.addAnnotatedClass(Address.class);
        configuration.addAnnotatedClass(Client.class);
        configuration.addAnnotatedClass(Phone.class);
        configuration.addAnnotatedClass(User.class);

        configuration.setProperties(props);
        return configuration;
    }

    public DataSource getDataSource() {
        return new DriverDataSource(
                HibernateConfiguration.class.getClassLoader(), "org.postgresql.Driver", URL, USERNAME, PASSWORD);
    }
}
