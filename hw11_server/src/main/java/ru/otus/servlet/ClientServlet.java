package ru.otus.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.service.ClientService;
import ru.otus.service.TemplateProcessor;

public class ClientServlet extends HttpServlet {
    private final ClientService clientService;
    private final TemplateProcessor templateProcessor;
    private static final String CLIENT_TEMPLATE = "client.ftl";

    public ClientServlet(ClientService clientService, TemplateProcessor templateProcessor) {
        this.clientService = clientService;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Client> clients = clientService.findAll();
        Map<String, Object> params = new HashMap<>();
        params.put("clients", clients);

        resp.setContentType("text/html");
        resp.getWriter().println(templateProcessor.getPage(CLIENT_TEMPLATE, params));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String street = req.getParameter("street");
        String[] phones = req.getParameterValues("phones");

        Client client = new Client();
        client.setName(name);

        Address address = new Address();
        address.setStreet(street);
        client.setAddress(address);

        List<Phone> phoneList = Arrays.stream(phones)
                .filter(p -> !p.isEmpty())
                .map(p -> new Phone(null, p))
                .toList();

        client.setPhones(phoneList);
        phoneList.forEach(p -> p.setClient(client));

        clientService.saveClient(client);
        resp.sendRedirect("/client"); // Перенаправление после успешного сохранения
    }
}
