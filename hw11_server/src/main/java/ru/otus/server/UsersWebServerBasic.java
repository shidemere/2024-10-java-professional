package ru.otus.server;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import ru.otus.service.ClientService;
import ru.otus.service.TemplateProcessor;
import ru.otus.servlet.ClientServlet;
import ru.otus.util.FileSystemHelper;

/**
 * Определяет базовую (без аутентификации) реализацию сервера.
 * Тут происходит прямое взаимодействие с Jetty, определяется приветственная страничка
 */
public class UsersWebServerBasic implements UsersWebServer {
    private static final String START_PAGE_NAME = "index.html";
    private static final String COMMON_RESOURCES_DIR = "templates";

    protected final TemplateProcessor templateProcessor;
    private final Server server;
    private final ClientService clientService;

    public UsersWebServerBasic(
            int port,
            TemplateProcessor templateProcessor,
            ClientService clientService) {
        this.templateProcessor = templateProcessor;
        server = new Server(port);
        this.clientService = clientService;
    }

    @Override
    public void start() throws Exception {
        if (server.getHandlers().isEmpty()) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    /**
     * Создание хэндлеров для сервера.
     * Здесь определяется:
     *  1. название приветственной странички (например index.html)
     *  2. папка с ресурсами
     *  3. секьюрность
     *  4. еще многие вещи которые я пока не тыкал
     */
    private void initContext() {

        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        Handler.Sequence sequence = new Handler.Sequence();
        sequence.addHandler(resourceHandler);
        sequence.addHandler(applySecurity(servletContextHandler, "/client", "/api/client/*"));
        server.setHandler(sequence);
    }

    @SuppressWarnings({"squid:S1172"})
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        return servletContextHandler;
    }

    /**
     * Настройка стартовой страницы и пути к файлам.
     * @return хэндлер для манипуляции со статическими файлами
     */
    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirAllowed(false);
        resourceHandler.setWelcomeFiles(START_PAGE_NAME);
        resourceHandler.setBaseResourceAsString(
                FileSystemHelper.localFileNameOrResourceNameToFullPath(COMMON_RESOURCES_DIR));
        return resourceHandler;
    }

    /**
     * Конструирование контекста для работы с сервлетами, добавление туда сервлета для работы с клиентами по заданноум пути
     * @return сконфигурированный контекст.
     */
    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(
                new ServletHolder(new ClientServlet(clientService, templateProcessor)), "/client");
        return servletContextHandler;
    }
}
