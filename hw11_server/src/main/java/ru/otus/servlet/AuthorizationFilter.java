package ru.otus.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Фильтр отрабатывает при попытке обратиться к ресурсам (страницам) которые я укажу в конфиге.
 * То есть страницу index.html я могу получить легко, так как она не защищена (и не должна быть).
 * Но вот при попытке доступа к клиентам фильтр и отработает.
 * Тут проверяется наличие сессии. Если есть - возможен доступ куда надо.
 * Если сессии нет - перенаправление на страницу логина в {@link LoginServlet}.
 */
public class AuthorizationFilter implements Filter {

    private ServletContext context;

    @Override
    public void init(FilterConfig filterConfig) {
        this.context = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        this.context.log("Requested Resource:" + uri);

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/login");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        // Not implemented
    }
}
