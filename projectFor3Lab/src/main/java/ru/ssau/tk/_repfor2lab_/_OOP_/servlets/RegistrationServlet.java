package ru.ssau.tk._repfor2lab_._OOP_.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

@WebServlet("/auth/register")
public class RegistrationServlet extends HttpServlet {
    private JdbcUserRepository userRepository = new JdbcUserRepository();
    private static final Logger logger = Logger.getLogger(RegistrationServlet.class.getName());
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        logger.info("RegistrationServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        try {
            logger.info("Starting user creation process");

            // Читаем данные пользователя из тела запроса
            String requestBody = request.getReader().lines().reduce("", String::concat);
            logger.info("Request body: " + requestBody);

            if (requestBody == null || requestBody.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Empty request body\"}");
                return;
            }

            // Парсим JSON
            var jsonNode = mapper.readTree(requestBody);

            // Проверяем обязательные поля
            if (!jsonNode.has("login") || !jsonNode.has("password")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Login and password are required\"}");
                return;
            }

            String factoryType = jsonNode.has("factoryType") ? jsonNode.get("factoryType").asText() : "array";
            String login = jsonNode.get("login").asText().trim();
            String password = jsonNode.get("password").asText().trim();

            if (login.isEmpty() || password.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Login and password cannot be empty\"}");
                return;
            }

            logger.info("Parsed user data - login: " + login + ", factoryType: " + factoryType);

            String authHeader = request.getHeader("Authorization");
            String role = "user"; // Роль по умолчанию

            if (authHeader != null && authHeader.startsWith("Basic ")) {
                try {
                    String base64Credentials = authHeader.substring("Basic ".length());
                    String credentials = new String(Base64.getDecoder().decode(base64Credentials));
                    String[] values = credentials.split(":", 2);

                    if (values.length == 2) {
                        Users currentUser = userRepository.findByLogin(values[0]);
                        if (currentUser != null && "admin".equals(currentUser.getRole())) {
                            // ADMIN может задавать любую роль
                            if (jsonNode.has("role")) {
                                role = jsonNode.get("role").asText();
                            }
                            logger.info("Admin creating user with role: " + role);
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Error processing Authorization header: " + e.getMessage());
                    // Продолжаем с ролью по умолчанию
                }
            }

            // Проверяем, существует ли пользователь
            if (userRepository.existsUserByLogin(login)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\": \"User already exists\"}");
                return;
            }

            // Создаем пользователя
            userRepository.createUser(factoryType, login, password, role);

            // Успешный ответ
            response.setStatus(HttpServletResponse.SC_CREATED);
            String successResponse = "{\"status\": \"User created successfully\", \"role\": \"" + role + "\"}";
            response.getWriter().write(successResponse);
            logger.info("User created successfully: " + login + " with role: " + role);

        } catch (Exception e) {
            logger.severe("Error in user registration: " + e.getMessage());
            e.printStackTrace();

            // Более информативная ошибка
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Registration failed: " + errorMessage + "\"}");
        }
    }
}