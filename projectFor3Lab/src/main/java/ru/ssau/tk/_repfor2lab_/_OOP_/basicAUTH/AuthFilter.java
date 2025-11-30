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
            logger.info("AuthFilter initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize AuthFilter: " + e.getMessage());
            throw new ServletException("Failed to initialize AuthFilter", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        // Если нет заголовка авторизации - пропускаем запрос
        if (authHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Basic ")) {
            logger.warning("Invalid Authorization header");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Math Functions API\"");
            return;
        }

        try {
            // Декодируем Basic Auth
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                logger.warning("Invalid Basic Auth format");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String login = values[0];
            String password = values[1];

            // Проверяем пользователя в БД
            if (!userRepo.authenticateUser(login, password)) {
                logger.warning("Authentication failed for user: " + login);
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            logger.info("User authenticated: " + login);

            // Получаем роль пользователя и сохраняем в запросе
            Users user = userRepo.findByLogin(login);
            httpRequest.setAttribute("currentUser", user);

            chain.doFilter(request, response);

        } catch (Exception e) {
            logger.severe("Error in AuthFilter: " + e.getMessage());
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}