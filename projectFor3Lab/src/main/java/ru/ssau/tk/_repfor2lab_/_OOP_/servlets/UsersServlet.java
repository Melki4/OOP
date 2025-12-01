package ru.ssau.tk._repfor2lab_._OOP_.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserReturnDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
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

        // Проверка аутентификации
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        // Проверка авторизации
        if (!AuthorizationService.hasAccess(currentUser, "GET", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
            return;
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("GET запрос: получение всех пользователей пользователем " + currentUser.getLogin());

                // Проверка, что пользователь - админ
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                List<UserReturnDTO> users = userRepository.findAllUsersAsDTO();
                String json = mapper.writeValueAsString(users);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + users.size() + " пользователей");
            }

            else if (pathInfo.equals("/get")) {
                // GET /users/get - получение пользователя по логину из тела запроса
                logger.info("GET запрос: получение пользователя по логину пользователем " + currentUser.getLogin());

                // Проверка, что пользователь - админ
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                // Чтение тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);

                if (requestBody == null || requestBody.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Пустое тело запроса\"}");
                    return;
                }

                // Извлечение логина из тела запроса
                String login = mapper.readTree(requestBody).get("value").asText();

                if (login == null || login.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Логин обязателен в поле 'value'\"}");
                    return;
                }

                // Получение пользователя по логину
                Users user = userRepository.findByLogin(login);

                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Пользователь не найден\"}");
                    return;
                }

                // Создание DTO для возврата (без пароля)
                UserReturnDTO userDTO = new UserReturnDTO();
                userDTO.setUserId(user.getUserId());
                userDTO.setLogin(user.getLogin());
                userDTO.setRole(user.getRole());
                userDTO.setFactoryType(user.getFactoryType());

                String json = mapper.writeValueAsString(userDTO);
                response.getWriter().write(json);
                logger.info("Успешно возвращен пользователь по логину: " + login);
            }


            else if (pathInfo.equals("/id/login")) {
                // GET /users/id/login - получение ID пользователя по логину из тела запроса
                logger.info("GET запрос: получение ID пользователя по логину пользователем " + currentUser.getLogin());

                // Чтение тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);

                if (requestBody == null || requestBody.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Пустое тело запроса\"}");
                    return;
                }

                // Извлечение логина из тела запроса
                String login = mapper.readTree(requestBody).get("value").asText();

                if (login == null || login.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Логин обязателен в поле 'value'\"}");
                    return;
                }

                int userId = userRepository.selectIdByLogin(login);

                // Проверка доступа к данным (USER может получать только свои данные)
                if (!AuthorizationService.canAccessUserData(currentUser, userId, login)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                // Возврат ID
                String json = mapper.writeValueAsString(userId);
                response.getWriter().write(json);
                logger.info("Успешно возвращен ID пользователя: " + userId + " для логина: " + login);
            }

            else if (pathInfo.startsWith("/by-login/")) {
                // GET /users/by-login - получение пользователя по логину из урла
                String login = pathInfo.substring(10); // Убираем "/by-login/"

                // Проверка доступа к данным
                if (!AuthorizationService.canAccessUserDataByLogin(currentUser, login)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                Users user = userRepository.findByLogin(login);
                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Пользователь не найден\"}");
                    return;
                }

                // Создание DTO для возврата (без пароля)
                UserReturnDTO userDTO = new UserReturnDTO();
                userDTO.setUserId(user.getUserId());
                userDTO.setLogin(user.getLogin());
                userDTO.setRole(user.getRole());
                userDTO.setFactoryType(user.getFactoryType());

                String json = mapper.writeValueAsString(userDTO);
                response.getWriter().write(json);
                logger.info("Успешно возвращен пользователь по логину: " + login);
            }
            else if (pathInfo.equals("/sorted")) {
                // GET /users/sorted - получение пользователей отсортированных по логину (только для ADMIN)

                // Проверка авторизации
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                logger.info("GET запрос: получение всех пользователей отсортированных по логину пользователем " + currentUser.getLogin());
                List<UserReturnDTO> users = userRepository.findAllUsersSortedByLoginAsDTO();
                String json = mapper.writeValueAsString(users);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + users.size() + " отсортированных пользователей");
            }

            else if (pathInfo.startsWith("/check/")) {

                String[] pathParts = pathInfo.split("/");

                if (pathParts.length == 4 && "id".equals(pathParts[2])) {
                    // GET /users/check/id/{id} - проверка существования пользователя по ID
                    int userId = Integer.parseInt(pathParts[3]);

                    // Проверка доступа к данным (USER может проверять только себя)
                    if (!AuthorizationService.canAccessUserDataById(currentUser, userId)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }

                    logger.info("GET запрос: проверка существования пользователя с ID: " + userId + " пользователем " + currentUser.getLogin());
                    boolean exists = userRepository.existsUserById(userId);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("Результат проверки существования пользователя с ID " + userId + ": " + exists);

                } else if (pathParts.length == 4 && "login".equals(pathParts[2])) {
                    // GET /users/check/login/{login} - проверка существования пользователя по логину
                    String login = pathParts[3];

                    // Проверка доступа к данным (USER может проверять только себя)
                    if (!AuthorizationService.canAccessUserDataByLogin(currentUser, login)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }

                    logger.info("GET запрос: проверка существования пользователя с логином: " + login + " пользователем " + currentUser.getLogin());
                    boolean exists = userRepository.existsUserByLogin(login);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("Результат проверки существования пользователя с логином " + login + ": " + exists);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат проверки\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Таблица пользователей пуста: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Пользователи не найдены\"}");
        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в GET запросе пользователей: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (Exception e) {
            logger.severe("Ошибка в GET запросе пользователей: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        // Проверка аутентификации
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите ID пользователя\"}");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length >= 3 && pathParts[1].matches("\\d+")) {
                int userId = Integer.parseInt(pathParts[1]);
                String action = pathParts[2];

                // Проверка доступа к данным (USER может обновлять только себя)
                if (!AuthorizationService.canAccessUserDataById(currentUser, userId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Можно обновлять только свои данные\"}");
                    return;
                }

                // Проверка авторизации для конкретного действия
                String servletPath = request.getServletPath();
                String fullPath = servletPath + pathInfo;

                if (!AuthorizationService.hasAccess(currentUser, "PUT", fullPath)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                // Чтение значения для обновления из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                String newValue = mapper.readTree(requestBody).get("value").asText();

                switch (action) {
                    case "factoryType":
                        logger.info("PUT запрос: обновление factoryType для пользователя ID: " + userId + " пользователем " + currentUser.getLogin());
                        userRepository.updateFactoryTypeById(newValue, userId);
                        break;
                    case "password":
                        logger.info("PUT запрос: обновление пароля для пользователя ID: " + userId + " пользователем " + currentUser.getLogin());
                        userRepository.updatePasswordById(newValue, userId);
                        break;
                    case "login":
                        logger.info("PUT запрос: обновление логина для пользователя ID: " + userId + " пользователем " + currentUser.getLogin());
                        userRepository.updateLoginById(newValue, userId);
                        break;
                    case "role":
                        // Только admin может менять роли
                        if (!"admin".equals(currentUser.getRole())) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\": \"Только admin может изменять роли\"}");
                            return;
                        }
                        logger.info("PUT запрос: обновление роли для пользователя ID: " + userId + " пользователем " + currentUser.getLogin());
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

        // Проверка аутентификации
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        // Проверка авторизации
        if (!AuthorizationService.hasAccess(currentUser, "DELETE", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
            return;
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // DELETE /users - удаление всех пользователей (только для ADMIN)
                logger.info("DELETE запрос: удаление всех пользователей пользователем " + currentUser.getLogin());
                userRepository.deleteAllUsers();
                response.getWriter().write("{\"status\": \"Все пользователи успешно удалены\"}");
                logger.info("Успешно удалены все пользователи");

            } else if (pathInfo.matches("/\\d+")) {
                // DELETE /users/{id} - удаление пользователя по ID
                int userId = Integer.parseInt(pathInfo.substring(1));

                // Проверка доступа к данным (USER может удалять только себя)
                if (!AuthorizationService.canAccessUserDataById(currentUser, userId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Можно удалять только свой аккаунт\"}");
                    return;
                }

                logger.info("DELETE запрос: удаление пользователя с ID: " + userId + " пользователем " + currentUser.getLogin());
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