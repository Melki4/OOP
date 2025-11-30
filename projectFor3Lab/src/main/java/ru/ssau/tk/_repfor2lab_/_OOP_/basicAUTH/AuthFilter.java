package ru.ssau.tk._repfor2lab_._OOP_.basicAUTH;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

public class AuthFilter implements Filter {
    private static final Logger logger = Logger.getLogger(AuthFilter.class.getName());
    private JdbcUserRepository userRepo;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            userRepo = new JdbcUserRepository();
            logger.info("Фильтр аутентификации успешно инициализирован");
        } catch (Exception e) {
            logger.severe("Ошибка инициализации фильтра аутентификации: " + e.getMessage());
            throw new ServletException("Ошибка инициализации фильтра аутентификации", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        // Если заголовок авторизации отсутствует - пропускаем запрос
        if (authHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Basic ")) {
            logger.warning("Неверный формат заголовка Authorization");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"API Математических Функций\"");
            return;
        }

        try {
            // Декодирование Basic Auth
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                logger.warning("Неверный формат Basic Auth");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String login = values[0];
            String password = values[1];

            // Проверка пользователя в БД
            if (!userRepo.authenticateUser(login, password)) {
                logger.warning("Ошибка аутентификации для пользователя: " + login);
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            logger.info("Пользователь аутентифицирован: " + login);

            // Получение роли пользователя и сохранение в запросе
            Users user = userRepo.findByLogin(login);
            httpRequest.setAttribute("currentUser", user);

            chain.doFilter(request, response);

        } catch (Exception e) {
            logger.severe("Ошибка в фильтре аутентификации: " + e.getMessage());
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}