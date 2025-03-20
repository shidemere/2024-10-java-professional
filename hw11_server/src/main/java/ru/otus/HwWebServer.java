package ru.otus;

import ru.otus.configuration.HibernateConfiguration;
import ru.otus.migration.MigrationsExecutorFlyway;
import ru.otus.repository.ClientRepository;
import ru.otus.repository.ClientRepositoryImpl;
import ru.otus.repository.UserRepository;
import ru.otus.repository.UserRepositoryImpl;
import ru.otus.security.UserAuthService;
import ru.otus.security.UserAuthServiceImpl;
import ru.otus.server.UsersWebServer;
import ru.otus.server.UsersWebServerExtended;
import ru.otus.service.ClientService;
import ru.otus.service.ClientServiceImpl;
import ru.otus.service.TemplateProcessor;
import ru.otus.service.TemplateProcessorImpl;

public class HwWebServer {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {
        HibernateConfiguration hibernateConfiguration = new HibernateConfiguration();
        MigrationsExecutorFlyway flyway = new MigrationsExecutorFlyway(hibernateConfiguration.getDataSource());
        flyway.executeMigrations();

        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UsersWebServer usersWebServer = getUsersWebServer(hibernateConfiguration, templateProcessor);
        usersWebServer.join();
    }

    private static UsersWebServer getUsersWebServer(
            HibernateConfiguration hibernateConfiguration, TemplateProcessor templateProcessor) throws Exception {
        UserRepository userRepository = new UserRepositoryImpl(hibernateConfiguration);
        UserAuthService authService = new UserAuthServiceImpl(userRepository);
        ClientRepository clientRepository = new ClientRepositoryImpl(hibernateConfiguration);
        ClientService clientService = new ClientServiceImpl(clientRepository);
        UsersWebServer usersWebServer =
                new UsersWebServerExtended(WEB_SERVER_PORT, authService, templateProcessor, clientService);

        usersWebServer.start();
        return usersWebServer;
    }
}
