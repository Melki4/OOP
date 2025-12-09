package ru.ssau.tk._repfor2lab_._OOP_.controller.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DaoException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;
import ru.ssau.tk._repfor2lab_._OOP_.model.service.UserService;

@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(UsersServlet.class.getName());

    // Паттерн для извлечения логина (после определенных префиксов)
    private static final Pattern LOGIN_ACTION_PATTERN =
            Pattern.compile("^/(get|get-id-by-login|check-by-login)/([a-zA-Z0-9._-]+)$");
    // ^/ - начало pathInfo
    // (get|get-id-by-login|check) - одна из трех операций
    // / - разделитель
    // ([a-zA-Z0-9._-]+) - логин (группа 2)

    // Паттерн для извлечения ID (после определенных префиксов)
    private static final Pattern ID_ACTION_PATTERN =
            Pattern.compile("^/(check-by-id|update/factory-type|update/password|update/login|update/role|delete/user)/(\\d+)$");
    // ^/ - начало pathInfo
    // (check|update/factory-type|... - одна из операций
    // / - разделитель
    // (\\d+) - ID (группа 2)

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

        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
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
                // Проверка, что пользователь - админ
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

            else if (pathInfo.startsWith("/get/")) {
                logger.info("GET запрос: получение пользователя по логину пользователем " + currentUser.getLogin());
                UserDTO userDTO;

                String path = request.getPathInfo();

                Matcher matcher = LOGIN_ACTION_PATTERN.matcher(path);

                String login;
                if (matcher.matches()) {
                    login = matcher.group(2); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if (!AuthorizationService.canAccessUserDataByLogin(currentUser, login)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                userDTO = userService.findByLogin(login);

                if (userDTO == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Пользователь не найден\"}");
                    return;
                }

                String json = mapper.writeValueAsString(userDTO);
                response.getWriter().write(json);
                logger.info("Успешно возвращен пользователь по логину: " + userDTO.getLogin());
            }

            else if (pathInfo.startsWith("/get-id-by-login/")) {
                logger.info("GET запрос: получение ID пользователя по логину пользователем " + currentUser.getLogin());

                String path = request.getPathInfo();

                Matcher matcher = LOGIN_ACTION_PATTERN.matcher(path);

                String login;
                if (matcher.matches()) {
                    login = matcher.group(2); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if (!AuthorizationService.canAccessUserDataByLogin(currentUser, login)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }
                Integer id = userService.findIdByLogin(login);

                // Возврат ID
                String json = mapper.writeValueAsString(id);
                response.getWriter().write(json);
                logger.info("Успешно возвращен ID пользователя: " + id);
            }

            else if (pathInfo.startsWith("/check-by-login/")) {

                String path = request.getPathInfo();

                Matcher matcher = LOGIN_ACTION_PATTERN.matcher(path);

                String login;
                if (matcher.matches()) {
                    login = matcher.group(2); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if (!AuthorizationService.canAccessUserDataByLogin(currentUser, login)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                boolean exists = userService.existsByLogin(login);
                response.getWriter().write("{\"exists\": " + exists + "}");
                logger.info("Результат проверки существования пользователя с логином " + login + ": " + exists);
            }

            else if (pathInfo.startsWith("/check-by-id/")) {

                String path = request.getPathInfo();

                Matcher matcher = ID_ACTION_PATTERN.matcher(path);

                Integer id;
                if (matcher.matches()) {
                    id = Integer.parseInt(matcher.group(2)); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if (!AuthorizationService.canAccessUserDataById(currentUser, id)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                boolean exists = userService.existsById(id);
                response.getWriter().write("{\"exists\": " + exists + "}");
                logger.info("Результат проверки существования пользователя с айди " + id + ": " + exists);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Пользователи не найдены\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
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
            }

            else if (pathInfo.startsWith("/update/factory-type/")){
                logger.info("Начинаем обновление типа фабрики для " + currentUser.getLogin());

                String path = request.getPathInfo();

                Matcher matcher = ID_ACTION_PATTERN.matcher(path);

                Integer id;
                if (matcher.matches()) {
                    id = Integer.parseInt(matcher.group(2)); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                String requestBody = request.getReader().lines().reduce("", String::concat);

                if (!AuthorizationService.canAccessById(currentUser, id)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                String factoryType = mapper.readTree(requestBody).get("factory-type").asText();
                userService.updateFactory(factoryType, id);
                response.getWriter().write("{\"Успешно\": \"Успешно обновлено\"}");
                logger.info("PUT запрос: обновление factoryType для пользователя ID: " + id + " пользователем " + currentUser.getLogin());
            }

            else if (pathInfo.startsWith("/update/password/")) {
                logger.info("Начинаем обновление пароля для " + currentUser.getLogin());

                String path = request.getPathInfo();

                Matcher matcher = ID_ACTION_PATTERN.matcher(path);

                Integer id;
                if (matcher.matches()) {
                    id = Integer.parseInt(matcher.group(2)); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }


                String requestBody = request.getReader().lines().reduce("", String::concat);

                if (!AuthorizationService.canAccessById(currentUser, id)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                String password = mapper.readTree(requestBody).get("password").asText();
                userService.updatePassword(password, id);
                response.getWriter().write("{\"Успешно\": \"Успешно обновлено\"}");
                logger.info("PUT запрос: обновление factoryType для пользователя ID: " + id + " пользователем " + currentUser.getLogin());
            }

            else if (pathInfo.startsWith("/update/role/")) {
                // Только admin может менять роли
                logger.info("Начинаем обновление роли для " + currentUser.getLogin());

                String path = request.getPathInfo();

                Matcher matcher = ID_ACTION_PATTERN.matcher(path);

                Integer id;
                if (matcher.matches()) {
                    id = Integer.parseInt(matcher.group(2)); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if (!"Admin".equals(currentUser.getRole())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Только Admin может изменять роли\"}");
                    return;
                }
                String requestBody = request.getReader().lines().reduce("", String::concat);

                String role = mapper.readTree(requestBody).get("role").asText();

                userService.updateRole(role, id);
                response.getWriter().write("{\"Успешно\": \"Успешно обновлено\"}");
                logger.info("PUT запрос: обновление роли для пользователя ID: " + id + " пользователем " + currentUser.getLogin());
            }

            else if (pathInfo.startsWith("/update/login/")){
                logger.info("Начинаем обновление логина для " + currentUser.getLogin());

                String path = request.getPathInfo();

                Matcher matcher = ID_ACTION_PATTERN.matcher(path);

                Integer id;
                if (matcher.matches()) {
                    id = Integer.parseInt(matcher.group(2)); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                String requestBody = request.getReader().lines().reduce("", String::concat);

                if (!AuthorizationService.canAccessById(currentUser, id)) {
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

        } catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Пользователи не найдены\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
        }
        catch (NumberFormatException e) {
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

        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        if (!AuthorizationService.hasAdminAccess(currentUser, "DELETE", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
            return;
        }

        try {
            if (pathInfo.equals("/delete")) {
                logger.info("DELETE запрос: удаление всех пользователей админом " + currentUser.getLogin());
                userService.deleteAllUsers();
                response.getWriter().write("{\"status\": \"Все пользователи успешно удалены\"}");
                logger.info("Успешно удалены все пользователи");

            }

            else if (pathInfo.startsWith("/delete/user/")) {
                String path = request.getPathInfo();

                Matcher matcher = ID_ACTION_PATTERN.matcher(path);

                Integer id;
                if (matcher.matches()) {
                    id = Integer.parseInt(matcher.group(2)); // john123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

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
        }
        catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Пользователи не найдены\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
        }catch (NumberFormatException e) {
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