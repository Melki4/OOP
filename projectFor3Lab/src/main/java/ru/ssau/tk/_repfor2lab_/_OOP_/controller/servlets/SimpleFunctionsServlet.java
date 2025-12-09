package ru.ssau.tk._repfor2lab_._OOP_.controller.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DaoException;
import ru.ssau.tk._repfor2lab_._OOP_.model.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.SimpleFunctionsDTO;
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
import ru.ssau.tk._repfor2lab_._OOP_.model.service.SimpleFunctionService;

@WebServlet("/simple-functions/*")
public class SimpleFunctionsServlet extends HttpServlet {
    private SimpleFunctionService simpleFunctionService;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(SimpleFunctionsServlet.class.getName());

    @Override
    public void init() {
        simpleFunctionService = new SimpleFunctionService();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет SimpleFunctionsServlet успешно инициализирован");
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
                logger.info("GET запрос: получение всех простых функций");

                var functions = simpleFunctionService.findAllSimpleFunctions();

                String json = mapper.writeValueAsString(functions);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + functions.size() + " простых функций");
            }

            else if (pathInfo.equals("/sorted")) {
                logger.info("GET запрос: получение всех простых функций отсортированных по имени");

                List<SimpleFunctionsDTO> functions = simpleFunctionService.findAllSimpleFunctionsSorted();

                String json = mapper.writeValueAsString(functions);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + functions.size() + " отсортированных простых функций");
            }

            else if (pathInfo.startsWith("/check")) {
                Map<String, String[]> parameters = request.getParameterMap();

                if(parameters.size() > 1) throw new RuntimeException("Слишком много параметров в запросе");

                if (parameters.containsKey("local-name")) {
                    if (parameters.get("local-name").length > 1)
                        throw new RuntimeException("Для параметра указано несколько значений");
                    String local_name = parameters.get("local-name")[0];


                    boolean exists = simpleFunctionService.existsByLocalName(local_name);
                    response.getWriter().write("{\"exists\": " + exists + "}");
                    logger.info("Результат проверки существования простой ф-ции  " + local_name + ": " + exists);
                }
                else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    throw new RuntimeException("Некорректный параметр запроса");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
        } catch (DataDoesNotExistException e) {
            logger.severe("Таблица с простыми функциями пуста: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Таких данных в таблице нет\"}");
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

        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
            return;
        }

        try {
            if (pathInfo.equals("/create")) {
                String requestBody = request.getReader().lines().reduce("", String::concat);

                String localName = mapper.readTree(requestBody).get("local-name").asText();

                logger.info("POST запрос: создание простой функции: " + localName);
                SimpleFunctionsDTO simpleFunctionsDTO = simpleFunctionService.createSimpleFunction(localName);

                response.setStatus(HttpServletResponse.SC_CREATED);
                String json = mapper.writeValueAsString(simpleFunctionsDTO);

                response.getWriter().write(json);
                logger.info("Успешно создана простая функция: " + localName);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Простые функции не найдены\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
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

        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
            return;
        }

        try {
            if (pathInfo.equals("/update")) {
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                String oldName = jsonNode.get("oldName").asText();
                String newName = jsonNode.get("newName").asText();

                logger.info("PUT запрос: обновление имени простой функции с " + oldName + " на " + newName);
                simpleFunctionService.updateSimpleFunction(oldName, newName);

                response.getWriter().write("{\"status\": \"Имя простой функции успешно обновлено с " + oldName +
                        " на " + newName + "\"}");
                logger.info("Успешно обновлено имя простой функции");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Простые функции не найдены\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
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

        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
            return;
        }

        try {
            if (pathInfo.equals("/delete")) {

                logger.warning("DELETE запрос: удаление всех простых функций");
                simpleFunctionService.deleteAllFunctions();

                response.getWriter().write("{\"status\": \"Все простые функции успешно удалены\"}");
                logger.warning("Успешно удалены все простые функции");

            } else if (pathInfo.startsWith("/delete-by-name")) {

                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                String name = jsonNode.get("name").asText();

                logger.info("DELETE запрос: удаление простой функции: " + name);
                simpleFunctionService.deleteSimpleFunction(name);

                response.getWriter().write("{\"status\": \"Простая функция успешно удалена\"}");
                logger.info("Успешно удалена простая функция: " + name);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат запроса для удаления\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Простые функции не найдены\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
        } catch (Exception e) {
            logger.severe("Ошибка удаления простых функций: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }
}