package ru.ssau.tk._repfor2lab_._OOP_.controller.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.model.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.UserDTO;
import ru.ssau.tk._repfor2lab_._OOP_.model.databaseEnteties.Users;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;
import ru.ssau.tk._repfor2lab_._OOP_.model.service.UserService;

@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(UsersServlet.class.getName());

    @Override
    public void init() {
        userService = new UserService();
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

                var users = userService.findAllUsers();

                String json = mapper.writeValueAsString(users);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + users.size() + " пользователей");
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

                List<UserDTO> users = userService.findAllUsersSorted();
                String json = mapper.writeValueAsString(users);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + users.size() + " отсортированных пользователей");
            }

            else if (pathInfo.equals("/get")) {
                logger.info("GET запрос: получение пользователя по логину пользователем " + currentUser.getLogin());
                UserDTO userDTO = null;
                Map<String, String[]> parameters = request.getParameterMap();

                if(parameters.size() > 1) throw new RuntimeException("Слишком много параметров в запросе");

                if (parameters.containsKey("login")){
                    String login = parameters.get("login")[0];

                    if (!AuthorizationService.canAccessUserDataByLogin(currentUser, login)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }
                   userDTO = userService.findByLogin(login);
                } else throw new RuntimeException("Некорректный параметр запроса");

                if (userDTO == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Пользователь не найден\"}");
                    return;
                }

                String json = mapper.writeValueAsString(userDTO);
                response.getWriter().write(json);
                logger.info("Успешно возвращен пользователь по логину: " + userDTO.getLogin());
            }

            else if (pathInfo.equals("/get-id-by-login")) {
                // GET /users/id/login - получение ID пользователя по логину из тела запроса
                logger.info("GET запрос: получение ID пользователя по логину пользователем " + currentUser.getLogin());

                //!!
                Map<String, String[]> parameters = request.getParameterMap();
                Integer id;
                if(parameters.size() > 1) throw new RuntimeException("Слишком много параметров в запросе");

                if (parameters.containsKey("login")){
                    String login = parameters.get("login")[0];
                    if (!AuthorizationService.canAccessUserDataByLogin(currentUser, login)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }
                    id = userService.findIdByLogin(login);
                } else throw new RuntimeException("Некорректный параметр запроса");

                //!!

                // Возврат ID
                String json = mapper.writeValueAsString(id);
                response.getWriter().write(json);
                logger.info("Успешно возвращен ID пользователя: " + id);
            }

            else if (pathInfo.startsWith("/check")) {
                Map<String, String[]> parameters = request.getParameterMap();

                if(parameters.size() > 1) throw new RuntimeException("Слишком много параметров в запросе");

                if (parameters.containsKey("login")){
                    if(parameters.get("login").length > 1) throw new RuntimeException("Для параметра указано несколько значений");
                    String login = parameters.get("login")[0];

                    if (!AuthorizationService.canAccessUserDataByLogin(currentUser, login)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }
                    boolean exists = userService.existsByLogin(login);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("Результат проверки существования пользователя с логином " + login + ": " + exists);
                } else if (parameters.containsKey("id")){
                    Integer id = Integer.parseInt(parameters.get("id")[0]);

                    if (!AuthorizationService.canAccessUserDataById(currentUser, id)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }
                    boolean exists = userService.existsById(id);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("Результат проверки существования пользователя с айди " + id + ": " + exists);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    throw new RuntimeException("Некорректный параметр запроса");
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

            else if (pathInfo.equals("/update/factory-type")){
                // Чтение значения для обновления из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);

                Integer id = mapper.readTree(requestBody).get("id").asInt();

                if (!AuthorizationService.canAccessUserDataById(currentUser, id)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                String factoryType = mapper.readTree(requestBody).get("factory-type").asText();
                userService.updateFactory(factoryType, id);
                response.getWriter().write("{\"Успешно\": \"Успешно обновлено\"}");
                logger.info("PUT запрос: обновление factoryType для пользователя ID: " + id + " пользователем " + currentUser.getLogin());
            }

            else if (pathInfo.equals("/update/password")) {
                // Чтение значения для обновления из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);

                Integer id = mapper.readTree(requestBody).get("id").asInt();

                if (!AuthorizationService.canAccessUserDataById(currentUser, id)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                String password = mapper.readTree(requestBody).get("password").asText();
                userService.updatePassword(password, id);
                response.getWriter().write("{\"Успешно\": \"Успешно обновлено\"}");
                logger.info("PUT запрос: обновление factoryType для пользователя ID: " + id + " пользователем " + currentUser.getLogin());
            }

            else if (pathInfo.equals("/update/role")) {
                // Только admin может менять роли

                if (!"Admin".equals(currentUser.getRole())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Только Admin может изменять роли\"}");
                    return;
                }

                // Чтение значения для обновления из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);

                Integer id = mapper.readTree(requestBody).get("id").asInt();

                String role = mapper.readTree(requestBody).get("role").asText();
                userService.updateRole(role, id);
                response.getWriter().write("{\"Успешно\": \"Успешно обновлено\"}");
                logger.info("PUT запрос: обновление роли для пользователя ID: " + id + " пользователем " + currentUser.getLogin());
            }

            else if (pathInfo.equals("/update-by-login")){
                // Чтение значения для обновления из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);

                Integer id = mapper.readTree(requestBody).get("id").asInt();

                if (!AuthorizationService.canAccessUserDataById(currentUser, id)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                String login = mapper.readTree(requestBody).get("login").asText();
                userService.updateLogin(login, id);
                response.getWriter().write("{\"Успешно\": \"Успешно обновлено\"}");
                logger.info("PUT запрос: обновление логина для пользователя ID: " + id + " пользователем " + currentUser.getLogin());
            }

            else {
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
        if (!AuthorizationService.hasAdminAccess(currentUser, "DELETE", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
            return;
        }

        try {
            if (pathInfo.equals("/delete")) {
                // DELETE /users - удаление всех пользователей (только для ADMIN)
                logger.info("DELETE запрос: удаление всех пользователей админом " + currentUser.getLogin());
                userService.deleteAllUsers();
                response.getWriter().write("{\"status\": \"Все пользователи успешно удалены\"}");
                logger.info("Успешно удалены все пользователи");

            }

            else if (pathInfo.equals("/delete/user")) {

                String requestBody = request.getReader().lines().reduce("", String::concat);

                Integer id = mapper.readTree(requestBody).get("id").asInt();

                if (!AuthorizationService.canAccessUserDataById(currentUser, id)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                logger.info("DELETE запрос: удаление пользователя с ID: " + id + " админом " + currentUser.getLogin());
                userService.deleteUser(id);
                response.getWriter().write("{\"status\": \"Пользователь успешно удален\"}");
                logger.info("Успешно удален пользователь ID: " + id);
            }

            else {
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