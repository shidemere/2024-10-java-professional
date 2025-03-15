package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        MigrationsExecutorFlyway flyway = new MigrationsExecutorFlyway(HibernateConfiguration.getDataSource());
        flyway.executeMigrations();

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UsersWebServer usersWebServer = getUsersWebServer(gson, templateProcessor);
        usersWebServer.join();
    }

    private static UsersWebServer getUsersWebServer(Gson gson, TemplateProcessor templateProcessor) throws Exception {
        UserRepository userRepository = new UserRepositoryImpl();
        UserAuthService authService = new UserAuthServiceImpl(userRepository);
        ClientRepository clientRepository = new ClientRepositoryImpl();
        ClientService clientService = new ClientServiceImpl(clientRepository);
        UsersWebServer usersWebServer = new UsersWebServerExtended(
                WEB_SERVER_PORT, authService, templateProcessor, clientService);

        usersWebServer.start();
        return usersWebServer;
    }
}
