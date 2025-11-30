package ru.ssau.tk._repfor2lab_._OOP_.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

@WebServlet("/simple-functions/*")
public class SimpleFunctionsServlet extends HttpServlet {
    private JdbcSimpleFunctionRepository simpleFunctionRepository;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(SimpleFunctionsServlet.class.getName());

    @Override
    public void init() {
        this.simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет SimpleFunctionsServlet успешно инициализирован");
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
                // GET /simple-functions - получение всех простых функций
                logger.info("GET запрос: получение всех простых функций");
                List<SimpleFunctionsDTO> functions = simpleFunctionRepository.findAllSimpleFunctionsAsDTO();
                String json = mapper.writeValueAsString(functions);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + functions.size() + " простых функций");

            } else if (pathInfo.equals("/sorted")) {
                // GET /simple-functions/sorted - получение функций отсортированных по имени
                logger.info("GET запрос: получение всех простых функций отсортированных по имени");
                List<SimpleFunctionsDTO> functions = simpleFunctionRepository.findAllSimpleFunctionsSortedByLocalNameAsDTO();
                String json = mapper.writeValueAsString(functions);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + functions.size() + " отсортированных простых функций");

            } else if (pathInfo.startsWith("/check/")) {
                // GET /simple-functions/check/{name} - проверка существования функции по имени
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3) {
                    String functionName = pathParts[2];
                    logger.info("GET запрос: проверка существования простой функции: " + functionName);
                    boolean exists = simpleFunctionRepository.existSimpleFunction(functionName);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("Результат проверки существования простой функции '" + functionName + "': " + exists);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат запроса для проверки\"}");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Таблица с простыми функциями пуста: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Таблица простых функций пуста\"}");
        } catch (Exception e) {
            logger.severe("Ошибка при выполнении GET запроса простых функций: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                // POST /simple-functions - создание новой простой функции

                // Проверка авторизации
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                String requestBody = request.getReader().lines().reduce("", String::concat);
                String localName = mapper.readTree(requestBody).get("value").asText();

                logger.info("POST запрос: создание простой функции: " + localName);
                simpleFunctionRepository.createSimpleFunction(localName);

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Простая функция успешно создана\"}");
                logger.info("Успешно создана простая функция: " + localName);

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (Exception e) {
            logger.severe("Ошибка создания простой функции: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный запрос: " + e.getMessage() + "\"}");
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
                // PUT /simple-functions - обновление имени функции

                // Проверка авторизации
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                String oldName = jsonNode.get("oldName").asText();
                String newName = jsonNode.get("newName").asText();

                logger.info("PUT запрос: обновление имени простой функции с '" + oldName + "' на '" + newName + "'");
                simpleFunctionRepository.updateSimpleFunctionName(oldName, newName);

                response.getWriter().write("{\"status\": \"Имя простой функции успешно обновлено\"}");
                logger.info("Успешно обновлено имя простой функции");

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (Exception e) {
            logger.severe("Ошибка обновления простой функции: " + e.getMessage());
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

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // DELETE /simple-functions - удаление всех простых функций

                // Проверка авторизации
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                logger.warning("DELETE запрос: удаление всех простых функций");
                simpleFunctionRepository.deleteAllFunctions();
                response.getWriter().write("{\"status\": \"Все простые функции успешно удалены\"}");
                logger.warning("Успешно удалены все простые функции");

            } else if (pathInfo.startsWith("/name/")) {
                // DELETE /simple-functions/name/{name} - удаление функции по имени

                // Проверка авторизации
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3) {
                    String functionName = pathParts[2];
                    logger.info("DELETE запрос: удаление простой функции: " + functionName);

                    simpleFunctionRepository.deleteSimpleFunctionByName(functionName);
                    response.getWriter().write("{\"status\": \"Простая функция успешно удалена\"}");
                    logger.info("Успешно удалена простая функция: " + functionName);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат имени функции\"}");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат запроса для удаления\"}");
            }

        } catch (Exception e) {
            logger.severe("Ошибка удаления простых функций: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }
}