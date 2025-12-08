package ru.ssau.tk._repfor2lab_._OOP_.controller.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.model.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.model.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import ru.ssau.tk._repfor2lab_._OOP_.model.service.MathFunctionService;
import ru.ssau.tk._repfor2lab_._OOP_.model.service.PointService;

@WebServlet("/points/*")
public class PointsServlet extends HttpServlet {
    private PointService pointService;
    private MathFunctionService mathFunctionService;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(PointsServlet.class.getName());

    @Override
    public void init() {
        pointService = new PointService();
        mathFunctionService = new MathFunctionService();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет PointsServlet успешно инициализирован");
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
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите ID функции\"}");

            } else if (pathInfo.startsWith("/get-points-by-function-id")) {

                Map<String, String[]> parameters = request.getParameterMap();

                if (parameters.size() > 1) throw new RuntimeException("Слишком много параметров в запросе");

                if (parameters.containsKey("function-id")) {
                    int function_id = Integer.parseInt(parameters.get("function-id")[0]);

                    MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(function_id);

                    if (!AuthorizationService.canAccessById(currentUser, function.getOwnerId())) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }

                    List<PointsDTO> points = pointService.findPointsByFunctionId(function_id);
                    String json = mapper.writeValueAsString(points);
                    response.getWriter().write(json);
                    logger.info("Успешно возвращено " + points.size() + " точек для функции ID: " + function_id);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат ID пользователя\"}");
                }
            } else if (pathInfo.startsWith("/get-points-by-function-id-sorted")) {

                Map<String, String[]> parameters = request.getParameterMap();

                if (parameters.size() > 1) throw new RuntimeException("Слишком много параметров в запросе");

                if (parameters.containsKey("function-id")) {
                    int function_id = Integer.parseInt(parameters.get("function-id")[0]);

                    MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(function_id);

                    if (!AuthorizationService.canAccessById(currentUser, function.getOwnerId())) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }
                    List<PointsDTO> points = pointService.findPointsByFunctionIdSorted(function_id);
                    String json = mapper.writeValueAsString(points);
                    response.getWriter().write(json);
                    logger.info("Успешно возвращено " + points.size() + " отсортированных точек для функции ID: " + function_id);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат ID функции\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }
        } catch (DataDoesNotExistException e) {
            logger.severe("Точек для функции не найдено: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Точек для функции не найдено\"}");
        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в GET запросе точек: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат ID функции\"}");
        } catch (Exception e) {
            logger.severe("Ошибка при выполнении GET запроса точек: " + e.getMessage());
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

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат запроса\"}");

            } else if (pathInfo.equals("/create-point")) {
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                double xValue = jsonNode.get("x_value").asDouble();
                double yValue = jsonNode.get("y_value").asDouble();
                int functionId = jsonNode.get("function-id").asInt();

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(functionId);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId())){
                    logger.severe("Попытка создания точки для чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к созданию точек для этой функции запрещен\"}");
                    return;
                }

                logger.info("POST запрос: создание точки для функции ID: " + functionId + " с координатами (" + xValue + ", " + yValue + ")");

                pointService.createPoint(xValue, yValue, functionId);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Точка успешно создана\"}");
                logger.info("Успешно создана точка для функции ID: " + functionId);

            } else if (pathInfo.equals("/create-points")) {
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                int functionId = jsonNode.get("function-id").asInt();

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(functionId);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId()) && !currentUser.getRole().equals("Admin")){
                    logger.severe("Попытка создания точек для чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к созданию точек для этой функции запрещен\"}");
                    return;
                }

                List<Point> points = mapper.readValue(
                        jsonNode.get("points").toString(),
                        new TypeReference<>(){}
                );

                logger.info("POST запрос: создание " + points.size() + " точек для функции ID: " + functionId);

                pointService.createManyPoints(points, functionId);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Точки успешно созданы\"}");
                logger.info("Успешно создано " + points.size() + " точек для функции ID: " + functionId);

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (Exception e) {
            logger.severe("Ошибка создания точек: " + e.getMessage());
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

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите переменную для обновления (x или y)\"}");
            } else if (pathInfo.equals("/update-x")) {
                // Чтение значений из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                int functionId = jsonNode.get("function-id").asInt();
                double oldValue = jsonNode.get("old-value").asDouble();
                double newValue = jsonNode.get("new-value").asDouble();

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(functionId);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId())){
                    logger.severe("Попытка обновления точек чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к обновлению точек этой функции запрещен\"}");
                    return;
                }

                logger.info("PUT запрос: обновление x для функции ID: " + functionId);
                pointService.updateXValue(functionId, oldValue, newValue);
                response.getWriter().write("{\"status\": \"Значение успешно обновлено\"}");
                logger.info("Успешно обновлено значение x у функции с ID: " + functionId);

            } else if (pathInfo.equals("/update-y")) {
                // Чтение значений из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                int functionId = jsonNode.get("function-id").asInt();
                double oldValue = jsonNode.get("old-value").asDouble();
                double newValue = jsonNode.get("new-value").asDouble();

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(functionId);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId())){
                    logger.severe("Попытка обновления точек чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к обновлению точек этой функции запрещен\"}");
                    return;
                }

                logger.info("PUT запрос: обновление x для функции ID: " + functionId);
                pointService.updateYValue(functionId, oldValue, newValue);
                response.getWriter().write("{\"status\": \"Значение успешно обновлено\"}");
                logger.info("Успешно обновлено значение x у функции с ID: " + functionId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат URL для обновления\"}");
            }

        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в PUT запросе точек: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат числа\"}");
        } catch (Exception e) {
            logger.severe("Ошибка обновления точек: " + e.getMessage());
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

        try {
            if (pathInfo.equals("/delete")) {
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                logger.warning("DELETE запрос: удаление всех точек");
                pointService.deleteAllPoints();
                response.getWriter().write("{\"status\": \"Все точки успешно удалены\"}");
                logger.warning("Успешно удалены все точки");

            } else if (pathInfo.startsWith("/delete-points-for-function")) {
                // Чтение значений из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                int functionId = jsonNode.get("function-id").asInt();

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(functionId);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId())) {
                    logger.severe("Попытка удаления точек чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к удалению точек этой функции запрещен\"}");
                    return;
                }

                pointService.deletePointsByFunctionId(functionId);
                response.getWriter().write("{\"status\": \"Точки для функции успешно удалены\"}");
                logger.info("Успешно удалены точки для функции ID: " + functionId);

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат запроса для удаления\"}");
            }

        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в DELETE запросе точек: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат ID функции\"}");
        } catch (Exception e) {
            logger.severe("Ошибка удаления точек: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }
}