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
        logger.info("UsersServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        // Проверяем аутентификацию
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Authentication required\"}");
            return;
        }

        // Проверяем авторизацию
        if (!AuthorizationService.hasAccess(currentUser, "GET", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Insufficient permissions\"}");
            return;
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Проверяем авторизацию
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                    return;
                }

                logger.info("GET request: fetching all users by " + currentUser.getLogin());
                List<UserReturnDTO> users = userRepository.findAllUsersAsDTO();
                String json = mapper.writeValueAsString(users);
                response.getWriter().write(json);
                logger.info("Successfully returned " + users.size() + " users");
            }

            else if (pathInfo.equals("/get")) {
                // GET /users/get - получить пользователя по логину из тела запроса
                logger.info("GET request: getting user by login by " + currentUser.getLogin());

                // Проверяем авторизацию
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                    return;
                }

                // Читаем тело запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);

                if (requestBody == null || requestBody.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Empty request body\"}");
                    return;
                }

                // Извлекаем логин из тела запроса
                String login = mapper.readTree(requestBody).get("value").asText();

                if (login == null || login.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Login is required in 'value' field\"}");
                    return;
                }

                // Получаем пользователя по логину
                Users user = userRepository.findByLogin(login);

                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"User not found\"}");
                    return;
                }
                // Создаем DTO для возврата (без пароля)
                UserReturnDTO userDTO = new UserReturnDTO();
                userDTO.setUserId(user.getUserId());
                userDTO.setLogin(user.getLogin());
                userDTO.setRole(user.getRole());
                userDTO.setFactoryType(user.getFactoryType());

                String json = mapper.writeValueAsString(userDTO);
                response.getWriter().write(json);
                logger.info("Successfully returned user by login: " + login);

            }

            else if (pathInfo.equals("/id/login")) {
                // GET /users/id/login - получить ID пользователя по логину из тела запроса
                logger.info("GET request: getting user ID by login by " + currentUser.getLogin());

                // Читаем тело запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);

                if (requestBody == null || requestBody.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Empty request body\"}");
                    return;
                }

                // Извлекаем логин из тела запроса
                String login = mapper.readTree(requestBody).get("value").asText();

                if (login == null || login.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Login is required in 'value' field\"}");
                    return;
                }

                int userId = userRepository.selectIdByLogin(login);

                // Проверяем доступ к данным (USER может получать только свои данные)
                if (!AuthorizationService.canAccessUserData(currentUser, userId, login)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Access denied to this user data\"}");
                    return;
                }

                // Возвращаем ID
                String json = mapper.writeValueAsString(userId);
                response.getWriter().write(json);
                logger.info("Successfully returned user ID: " + userId + " for login: " + login);
            }

            else if (pathInfo.startsWith("/by-login/")) {
                String login = pathInfo.substring(10); // Убираем "/by-login/"

                // Проверяем доступ к данным
                if (!AuthorizationService.canAccessUserData(currentUser, userRepository.selectIdByLogin(login), login)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Access denied to this user data\"}");
                    return;
                }

                Users user = userRepository.findByLogin(login);
                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"User not found\"}");
                    return;
                }

                // Создаем DTO для возврата (без пароля)
                UserReturnDTO userDTO = new UserReturnDTO();
                userDTO.setUserId(user.getUserId());
                userDTO.setLogin(user.getLogin());
                userDTO.setRole(user.getRole());
                userDTO.setFactoryType(user.getFactoryType());

                String json = mapper.writeValueAsString(userDTO);
                response.getWriter().write(json);
                logger.info("Successfully returned user by login: " + login);
            }

            else if (pathInfo.equals("/sorted")) {
                // GET /users/sorted - получить пользователей отсортированных по логину (только для ADMIN)

                // Проверяем авторизацию
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                    return;
                }

                logger.info("GET request: fetching all users sorted by login by " + currentUser.getLogin());
                List<UserReturnDTO> users = userRepository.findAllUsersSortedByLoginAsDTO();
                String json = mapper.writeValueAsString(users);
                response.getWriter().write(json);
                logger.info("Successfully returned " + users.size() + " sorted users");

            }

            else if (pathInfo.startsWith("/check/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 4 && "id".equals(pathParts[2])) {
                    // GET /users/check/id/{id} - проверить существование пользователя по ID
                    int userId = Integer.parseInt(pathParts[3]);

                    // Проверяем доступ к данным (USER может проверять только себя)
                    if (!AuthorizationService.canAccessUserData(currentUser, userId, null)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Access denied to this user data\"}");
                        return;
                    }

                    logger.info("GET request: checking if user exists with ID: " + userId + " by " + currentUser.getLogin());
                    boolean exists = userRepository.existsUserById(userId);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("User exists check result for ID " + userId + ": " + exists);

                } else if (pathParts.length == 4 && "login".equals(pathParts[2])) {
                    // GET /users/check/login/{login} - проверить существование пользователя по логину
                    String login = pathParts[3];

                    // Проверяем доступ к данным (USER может проверять только себя)
                    if (!AuthorizationService.canAccessUserData(currentUser, null, login)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Access denied to this user data\"}");
                        return;
                    }

                    logger.info("GET request: checking if user exists with login: " + login + " by " + currentUser.getLogin());
                    boolean exists = userRepository.existsUserByLogin(login);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("User exists check result for login " + login + ": " + exists);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Invalid check format\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Resource not found\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Users table is empty: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"No users found\"}");
        } catch (NumberFormatException e) {
            logger.severe("Number format error in GET users: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid ID format\"}");
        } catch (Exception e) {
            logger.severe("Error in GET users: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        // Проверяем аутентификацию
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Authentication required\"}");
            return;
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Specify user ID\"}");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length >= 3 && pathParts[1].matches("\\d+")) {
                int userId = Integer.parseInt(pathParts[1]);
                String action = pathParts[2];

                // Проверяем доступ к данным (USER может обновлять только себя)
                if (!AuthorizationService.canAccessUserData(currentUser, userId, null)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Can only update your own data\"}");
                    return;
                }

                // Проверяем авторизацию для конкретного действия
                String servletPath = request.getServletPath();
                String fullPath = servletPath + pathInfo;

                if (!AuthorizationService.hasAccess(currentUser, "PUT", fullPath)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                    return;
                }

                // Читаем значение для обновления из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                String newValue = mapper.readTree(requestBody).get("value").asText();

                switch (action) {
                    case "factoryType":
                        logger.info("PUT request: updating factoryType for user ID: " + userId + " by " + currentUser.getLogin());
                        userRepository.updateFactoryTypeById(newValue, userId);
                        break;
                    case "password":
                        logger.info("PUT request: updating password for user ID: " + userId + " by " + currentUser.getLogin());
                        userRepository.updatePasswordById(newValue, userId);
                        break;
                    case "login":
                        logger.info("PUT request: updating login for user ID: " + userId + " by " + currentUser.getLogin());
                        userRepository.updateLoginById(newValue, userId);
                        break;
                    case "role":
                        // Только ADMIN может менять роли
                        if (!"ADMIN".equals(currentUser.getRole())) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\": \"Only ADMIN can change roles\"}");
                            return;
                        }
                        logger.info("PUT request: updating role for user ID: " + userId + " by " + currentUser.getLogin());
                        userRepository.updateRoleById(newValue, userId);
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Unknown action: " + action + "\"}");
                        return;
                }

                response.getWriter().write("{\"status\": \"User updated successfully\"}");
                logger.info("Successfully updated user ID: " + userId);

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid URL format for update\"}");
            }

        } catch (NumberFormatException e) {
            logger.severe("Number format error in PUT users: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid user ID format\"}");
        } catch (Exception e) {
            logger.severe("Error updating user: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Bad request: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        // Проверяем аутентификацию
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Authentication required\"}");
            return;
        }

        // Проверяем авторизацию
        if (!AuthorizationService.hasAccess(currentUser, "DELETE", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Insufficient permissions\"}");
            return;
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // DELETE /users - удалить всех пользователей (только для ADMIN)
                logger.info("DELETE request: deleting all users by " + currentUser.getLogin());
                userRepository.deleteAllUsers();
                response.getWriter().write("{\"status\": \"All users deleted successfully\"}");
                logger.info("Successfully deleted all users");

            } else if (pathInfo.matches("/\\d+")) {
                // DELETE /users/{id} - удалить пользователя по ID
                int userId = Integer.parseInt(pathInfo.substring(1));

                // Проверяем доступ к данным (USER может удалять только себя)
                if (!AuthorizationService.canAccessUserData(currentUser, userId, null)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Can only delete your own account\"}");
                    return;
                }

                logger.info("DELETE request: deleting user with ID: " + userId + " by " + currentUser.getLogin());
                userRepository.deleteUserById(userId);
                response.getWriter().write("{\"status\": \"User deleted successfully\"}");
                logger.info("Successfully deleted user ID: " + userId);

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid user ID format\"}");
            }

        } catch (NumberFormatException e) {
            logger.severe("Number format error in DELETE users: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid user ID format\"}");
        } catch (Exception e) {
            logger.severe("Error deleting user: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}