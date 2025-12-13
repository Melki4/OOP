package ru.ssau.tk._repfor2lab_._OOP_.controller.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DaoException;
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
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern ALL_ID_PATTERNS =
            Pattern.compile("^/(get-points-by-function-id|get-points-by-function-id-sorted|" +
                    "create-point|create-points|update-x|update-y|delete-points-for-function)/(\\d+)$");
    // ^/ - начало pathInfo
    // (get-points-by-function-id|get-points-by-function-id-sorted|create-point|create-points|update-x|update-y|delete-points-for-function) - операция
    // / - разделитель
    // (\\d+) - ID функции (только цифры)

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

            } else if (pathInfo.startsWith("/get-points-by-function-id/")) {

                String path = request.getPathInfo();

                Matcher matcher = ALL_ID_PATTERNS.matcher(path);

                int function_id;
                if (matcher.matches()) {
                    function_id = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

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
            } else if (pathInfo.startsWith("/get-points-by-function-id-sorted/")) {

                String path = request.getPathInfo();

                Matcher matcher = ALL_ID_PATTERNS.matcher(path);

                int function_id;
                if (matcher.matches()) {
                    function_id = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

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
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
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

            } else if (pathInfo.startsWith("/create-point/")) {

                String path = request.getPathInfo();

                Matcher matcher = ALL_ID_PATTERNS.matcher(path);

                int function_id;
                if (matcher.matches()) {
                    function_id = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                double xValue = jsonNode.get("xvalue").asDouble();
                double yValue = jsonNode.get("yvalue").asDouble();

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(function_id);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId())){
                    logger.severe("Попытка создания точки для чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к созданию точек для этой функции запрещен\"}");
                    return;
                }

                logger.info("POST запрос: создание точки для функции ID: " + function_id + " с координатами (" + xValue + ", " + yValue + ")");

                pointService.createPoint(xValue, yValue, function_id);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Точка успешно создана\"}");
                logger.info("Успешно создана точка для функции ID: " + function_id);

            } else if (pathInfo.startsWith("/create-points/")) {

                String path = request.getPathInfo();

                Matcher matcher = ALL_ID_PATTERNS.matcher(path);

                int function_id;
                if (matcher.matches()) {
                    function_id = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(function_id);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId()) && !currentUser.getRole().equals("Admin")){
                    logger.severe("Попытка создания точек для чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к созданию точек для этой функции запрещен\"}");
                    return;
                }

                List<PointsDTO> points = mapper.readValue(
                        jsonNode.get("points").toString(),
                        new TypeReference<>(){}
                );

                List<Point> pointS = new java.util.ArrayList<>(List.of());

                for (var el : points){
                    pointS.add(new Point(el.getXValue(), el.getYValue()));
                }

                logger.info("POST запрос: создание " + points.size() + " точек для функции ID: " + function_id);

                pointService.createManyPoints(pointS, function_id);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Точки успешно созданы\"}");
                logger.info("Успешно создано " + points.size() + " точек для функции ID: " + function_id);

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Точки не найдены\"}");
        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа запросе: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
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
            } else if (pathInfo.startsWith("/update-x/")) {

                String path = request.getPathInfo();

                Matcher matcher = ALL_ID_PATTERNS.matcher(path);

                int function_id;
                if (matcher.matches()) {
                    function_id = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                // Чтение значений из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                double oldValue = jsonNode.get("old-value").asDouble();
                double newValue = jsonNode.get("new-value").asDouble();

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(function_id);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId())){
                    logger.severe("Попытка обновления точек чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к обновлению точек этой функции запрещен\"}");
                    return;
                }

                logger.info("PUT запрос: обновление x для функции ID: " + function_id);
                pointService.updateXValue(function_id, oldValue, newValue);
                response.getWriter().write("{\"status\": \"Значение успешно обновлено\"}");
                logger.info("Успешно обновлено значение x у функции с ID: " + function_id);

            } else if (pathInfo.startsWith("/update-y/")) {

                String path = request.getPathInfo();

                Matcher matcher = ALL_ID_PATTERNS.matcher(path);

                int function_id;
                if (matcher.matches()) {
                    function_id = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                // Чтение значений из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                double oldValue = jsonNode.get("old-value").asDouble();
                double newValue = jsonNode.get("new-value").asDouble();

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(function_id);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId())){
                    logger.severe("Попытка обновления точек чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к обновлению точек этой функции запрещен\"}");
                    return;
                }

                logger.info("PUT запрос: обновление x для функции ID: " + function_id);
                pointService.updateYValue(function_id, oldValue, newValue);
                response.getWriter().write("{\"status\": \"Значение успешно обновлено\"}");
                logger.info("Успешно обновлено значение x у функции с ID: " + function_id);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат URL для обновления\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Точки не найдены\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
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

            } else if (pathInfo.startsWith("/delete-points-for-function/")) {

                String path = request.getPathInfo();

                Matcher matcher = ALL_ID_PATTERNS.matcher(path);

                int function_id;
                if (matcher.matches()) {
                    function_id = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                MathFunctionsDTO function = mathFunctionService.findMathFunctionByFunctionId(function_id);

                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId())) {
                    logger.severe("Попытка удаления точек чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к удалению точек этой функции запрещен\"}");
                    return;
                }

                pointService.deletePointsByFunctionId(function_id);
                response.getWriter().write("{\"status\": \"Точки для функции успешно удалены\"}");
                logger.info("Успешно удалены точки для функции ID: " + function_id);

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат запроса для удаления\"}");
            }

        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
        } catch (DataDoesNotExistException e) {
            logger.severe("Таких данных в таблице нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Точки не найдены\"}");
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