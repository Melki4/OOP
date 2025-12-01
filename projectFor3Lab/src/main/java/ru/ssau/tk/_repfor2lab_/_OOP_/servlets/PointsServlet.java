package ru.ssau.tk._repfor2lab_._OOP_.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcMathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcPointRepository;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@WebServlet("/points/*")
public class PointsServlet extends HttpServlet {
    private JdbcPointRepository pointRepository;
    private JdbcMathFunctionRepository mathFunctionRepository;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(PointsServlet.class.getName());

    @Override
    public void init() {
        this.pointRepository = new JdbcPointRepository();
        this.mathFunctionRepository = new JdbcMathFunctionRepository();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет PointsServlet успешно инициализирован");
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
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите ID функции\"}");

            } else if (pathInfo.startsWith("/function/")) {
                String[] pathParts = pathInfo.split("/");

                if (pathParts.length >= 3 && pathParts[2].matches("\\d+")) {
                    int functionId = Integer.parseInt(pathParts[2]);

                    if (pathParts.length >= 4 && "sorted".equals(pathParts[3])) {
                        // GET /points/function/{functionId}/sorted - получение точек по ID функции (отсортированные)
                        logger.info("GET запрос: получение отсортированных точек для функции ID: " + functionId);

                        MathFunctionsDTO function = mathFunctionRepository.findMathFunctionByFunctionId(functionId);

                        if (!Objects.equals(function.getOwnerId(), currentUser.getUserId()) && !currentUser.getRole().equals("Admin")){
                            logger.severe("Доступ к точкам функции запрещен");
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            response.getWriter().write("{\"error\": \"Точки для функции не найдены\"}");
                            return;
                        }

                        List<PointsDTO> points = pointRepository.findPointsByFunctionIdSortedAsDTO(functionId);
                        String json = mapper.writeValueAsString(points);
                        response.getWriter().write(json);
                        logger.info("Успешно возвращено " + points.size() + " отсортированных точек для функции ID: " + functionId);

                    } else {
                        // GET /points/function/{functionId} - получение точек по ID функции
                        logger.info("GET запрос: получение точек для функции ID: " + functionId);

                        MathFunctionsDTO function = mathFunctionRepository.findMathFunctionByFunctionId(functionId);

                        if (!Objects.equals(function.getOwnerId(), currentUser.getUserId()) && !currentUser.getRole().equals("Admin")){
                            logger.severe("Доступ к точкам функции запрещен");
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            response.getWriter().write("{\"error\": \"Точки для функции не найдены\"}");
                            return;
                        }

                        List<PointsDTO> points = pointRepository.findPointsByFunctionIdAsDTO(functionId);
                        String json = mapper.writeValueAsString(points);
                        response.getWriter().write(json);
                        logger.info("Успешно возвращено " + points.size() + " точек для функции ID: " + functionId);
                    }
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
                response.getWriter().write("{\"error\": \"Укажите конечную точку (single или bulk)\"}");

            } else if (pathInfo.equals("/single")) {
                // POST /points/single - создание одной точки
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                double xValue = jsonNode.get("x_value").asDouble();
                double yValue = jsonNode.get("y_value").asDouble();
                int functionId = jsonNode.get("function_id").asInt();

                MathFunctionsDTO function = mathFunctionRepository.findMathFunctionByFunctionId(functionId);
                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId()) && !currentUser.getRole().equals("Admin")){
                    logger.severe("Попытка создания точки для чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к созданию точек для этой функции запрещен\"}");
                    return;
                }

                logger.info("POST запрос: создание точки для функции ID: " + functionId + " с координатами (" + xValue + ", " + yValue + ")");

                pointRepository.createPoint(xValue, yValue, functionId);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Точка успешно создана\"}");
                logger.info("Успешно создана точка для функции ID: " + functionId);

            } else if (pathInfo.equals("/bulk")) {
                // POST /points/bulk - создание нескольких точек
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                int functionId = jsonNode.get("function_id").asInt();

                MathFunctionsDTO function = mathFunctionRepository.findMathFunctionByFunctionId(functionId);

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

                pointRepository.addManyPoints(points, functionId);
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
                response.getWriter().write("{\"error\": \"Укажите переменную для обновления (x или y)\"}");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                String variable = pathParts[1];

                // Чтение значений из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                int functionId = jsonNode.get("id").asInt();
                double oldValue = jsonNode.get("oldValue").asDouble();
                double newValue = jsonNode.get("newValue").asDouble();

                MathFunctionsDTO function = mathFunctionRepository.findMathFunctionByFunctionId(functionId);
                if (!Objects.equals(function.getOwnerId(), currentUser.getUserId()) && !currentUser.getRole().equals("Admin")){
                    logger.severe("Попытка обновления точек чужой функции");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к обновлению точек этой функции запрещен\"}");
                    return;
                }

                switch (variable) {
                    case "x":
                        logger.info("PUT запрос: обновление x для функции ID: " + functionId);
                        pointRepository.updateXValueByFunctionIdAndOldX(oldValue, functionId, newValue);
                        break;
                    case "y":
                        logger.info("PUT запрос: обновление y для функции ID: " + functionId);
                        pointRepository.updateYValueByFunctionIdAndOldY(oldValue, functionId, newValue);
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\": \"Неизвестная переменная: " + variable + "\"}");
                        return;
                }

                response.getWriter().write("{\"status\": \"Значение успешно обновлено\"}");
                logger.info("Успешно обновлено значение " + variable + " у функции с ID: " + functionId);
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

        // Проверка аутентификации
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // DELETE /points - удаление всех точек

                // Проверка авторизации
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Недостаточно прав\"}");
                    return;
                }

                logger.warning("DELETE запрос: удаление всех точек");
                pointRepository.deleteAllPoints();
                response.getWriter().write("{\"status\": \"Все точки успешно удалены\"}");
                logger.warning("Успешно удалены все точки");

            } else if (pathInfo.startsWith("/function/")) {
                // DELETE /points/function/{functionId} - удаление точек по ID функции
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && pathParts[2].matches("\\d+")) {
                    int functionId = Integer.parseInt(pathParts[2]);
                    logger.info("DELETE запрос: удаление точек для функции ID: " + functionId);

                    MathFunctionsDTO function = mathFunctionRepository.findMathFunctionByFunctionId(functionId);
                    if (!Objects.equals(function.getOwnerId(), currentUser.getUserId()) && !currentUser.getRole().equals("Admin")){
                        logger.severe("Попытка удаления точек чужой функции");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к удалению точек этой функции запрещен\"}");
                        return;
                    }

                    pointRepository.deletePointsByFunctionId(functionId);
                    response.getWriter().write("{\"status\": \"Точки для функции успешно удалены\"}");
                    logger.info("Успешно удалены точки для функции ID: " + functionId);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат ID функции\"}");
                }
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