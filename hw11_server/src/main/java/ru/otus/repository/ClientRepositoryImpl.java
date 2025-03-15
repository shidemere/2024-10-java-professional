package ru.otus.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.graph.RootGraph;
import ru.otus.configuration.HibernateConfiguration;
import ru.otus.model.Client;

@Slf4j
public class ClientRepositoryImpl implements ClientRepository {

    @Override
    public List<Client> findAll() {
        try (Session session = HibernateConfiguration.getSession()) {
            session.beginTransaction();
            RootGraph<?> graph = session.getEntityGraph("phones_address_entity_graph");
            Map<String, Object> hint = Map.of("jakarta.persistence.fetchgraph", graph);
            return session.createQuery("SELECT c FROM Client c", Client.class)
                    .setHint("jakarta.persistence.fetchgraph", graph)
                    .list();
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Client save(Client client) {
        try (Session session = HibernateConfiguration.getSession()) {
            session.beginTransaction();
            session.persist(client);
            session.getTransaction().commit();
            return client;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
