package ru.otus.server;

import java.util.Arrays;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import ru.otus.security.UserAuthService;
import ru.otus.service.ClientService;
import ru.otus.service.TemplateProcessor;
import ru.otus.servlet.AuthorizationFilter;
import ru.otus.servlet.LoginServlet;

public class UsersWebServerExtended extends UsersWebServerBasic {
    private final UserAuthService authService;

    public UsersWebServerExtended(
            int port,
            UserAuthService authService,
            TemplateProcessor templateProcessor,
            ClientService clientService) {
        super(port, templateProcessor, clientService);
        this.authService = authService;
    }

    /**
     * Докручивание контекста сервлетов через добавление туда сервлета, отвечающего за страницу с логином.
     * После чего в этот контекст добавляется фильтр, который будет контроллировать доступ на основе сессии.
     * Общая схема взаимодействия сервлета и фильтра выглядит так
     * Пользователь -> Запрос к /client
     *  -> AuthorizationFilter (нет сессии)
     *  -> Перенаправление на /login
     *  -> Ввод данных -> LoginServlet (создаёт сессию)
     *  -> Перенаправление на /client
     *  -> AuthorizationFilter (есть сессия)
     *  -> Доступ разрешён.
     *
     * @param servletContextHandler
     * @param paths
     * @return
     */
    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {

        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, authService)), "/login");
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths)
                .forEachOrdered(
                        path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));
        return servletContextHandler;
    }
}
