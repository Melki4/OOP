package ru.ssau.tk._repfor2lab_._OOP_.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserReturnDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {
    private JdbcUserRepository userRepository;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(UsersServlet.class.getName());

    @Override
    public void init() {
        this.userRepository = new JdbcUserRepository();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет UsersServlet успешно инициализирован");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /users - получить всех пользователей
                logger.info("GET запрос: получение всех пользователей");
                List<UserReturnDTO> users = userRepository.findAllUsersAsDTO();
                String json = mapper.writeValueAsString(users);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + users.size() + " пользователей");

            } else if (pathInfo.equals("/sorted")) {
                // GET /users/sorted - получить пользователей отсортированных по логину
                logger.info("GET запрос: получение всех пользователей отсортированных по логину");
                List<UserReturnDTO> users = userRepository.findAllUsersSortedByLoginAsDTO();
                String json = mapper.writeValueAsString(users);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + users.size() + " отсортированных пользователей");

            } else if (pathInfo.startsWith("/check/")) {
                // Обработка проверок существования
                String[] pathParts = pathInfo.split("/");

                if (pathParts.length >= 4 && "id".equals(pathParts[2])) {
                    // GET /users/check/id/{id} - проверить существование пользователя по ID (из URL)
                    int userId = Integer.parseInt(pathParts[3]);
                    logger.info("GET запрос: проверка существования пользователя с ID: " + userId);
                    boolean exists = userRepository.existsUserById(userId);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("Результат проверки существования пользователя с ID " + userId + ": " + exists);

                } else if (pathParts.length >= 4 && "login".equals(pathParts[2])) {
                    // GET /users/check/login/{login} - проверить существование пользователя по логину (из URL)
                    String login = pathParts[3];
                    logger.info("GET запрос: проверка существования пользователя с логином: " + login);
                    boolean exists = userRepository.existsUserByLogin(login);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("Результат проверки существования пользователя с логином " + login + ": " + exists);

                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат запроса для проверки\"}");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Таблица с пользователями пуста: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Данных в таблице нет\"}");
        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в GET запросе пользователей: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (Exception e) {
            logger.severe("Ошибка при выполнении GET запроса пользователей: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /users - создание нового пользователя
                UserDTO user = mapper.readValue(request.getReader(), UserDTO.class);
                logger.info("POST запрос: создание нового пользователя с логином: " + user.getLogin());

                userRepository.createUser(
                        user.getFactoryType(),
                        user.getLogin(),
                        user.getPassword(),
                        user.getRole()
                );

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Пользователь успешно создан\"}");
                logger.info("Успешно создан пользователь: " + user.getLogin());

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (Exception e) {
            logger.severe("Ошибка создания пользователя: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный запрос: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите ID пользователя\"}");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length >= 3 && pathParts[1].matches("\\d+")) {
                // PUT /users/{id}/{field} - обновление поля пользователя
                int userId = Integer.parseInt(pathParts[1]);
                String action = pathParts[2];

                // Читаем значение для обновления из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                String newValue = mapper.readTree(requestBody).get("value").asText();

                switch (action) {
                    case "factoryType":
                        logger.info("PUT запрос: обновление factoryType для пользователя ID: " + userId);
                        userRepository.updateFactoryTypeById(newValue, userId);
                        break;
                    case "password":
                        logger.info("PUT запрос: обновление пароля для пользователя ID: " + userId);
                        userRepository.updatePasswordById(newValue, userId);
                        break;
                    case "login":
                        logger.info("PUT запрос: обновление логина для пользователя ID: " + userId);
                        userRepository.updateLoginById(newValue, userId);
                        break;
                    case "role":
                        logger.info("PUT запрос: обновление роли для пользователя ID: " + userId);
                        userRepository.updateRoleById(newValue, userId);
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Неизвестное действие: " + action + "\"}");
                        return;
                }

                response.getWriter().write("{\"status\": \"Пользователь успешно обновлен\"}");
                logger.info("Успешно обновлен пользователь ID: " + userId);

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат URL для обновления\"}");
            }

        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в PUT запросе пользователей: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат ID пользователя\"}");
        } catch (Exception e) {
            logger.severe("Ошибка обновления пользователя: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный запрос: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // DELETE /users - удалить всех пользователей
                logger.warning("DELETE запрос: удаление всех пользователей");
                userRepository.deleteAllUsers();
                response.getWriter().write("{\"status\": \"Все пользователи успешно удалены\"}");
                logger.warning("Успешно удалены все пользователи");

            } else if (pathInfo.matches("/\\d+")) {
                // DELETE /users/{id} - удалить пользователя по ID
                int userId = Integer.parseInt(pathInfo.substring(1));
                logger.info("DELETE запрос: удаление пользователя с ID: " + userId);

                userRepository.deleteUserById(userId);
                response.getWriter().write("{\"status\": \"Пользователь успешно удален\"}");
                logger.info("Успешно удален пользователь ID: " + userId);

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат ID пользователя\"}");
            }

        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в DELETE запросе пользователей: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат ID пользователя\"}");
        } catch (Exception e) {
            logger.severe("Ошибка удаления пользователя: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }
}