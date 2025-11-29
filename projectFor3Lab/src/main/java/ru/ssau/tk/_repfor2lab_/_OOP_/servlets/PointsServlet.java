package ru.ssau.tk._repfor2lab_._OOP_.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcPointRepository;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@WebServlet("/points/*")
public class PointsServlet extends HttpServlet {
    private JdbcPointRepository pointRepository;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(PointsServlet.class.getName());

    @Override
    public void init() {
        this.pointRepository = new JdbcPointRepository();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет PointsServlet успешно инициализирован");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите ID функции\"}");

            } else if (pathInfo.startsWith("/function/")) {
                String[] pathParts = pathInfo.split("/");

                if (pathParts.length >= 3 && pathParts[2].matches("\\d+")) {
                    int functionId = Integer.parseInt(pathParts[2]);

                    if (pathParts.length >= 4 && "sorted".equals(pathParts[3])) {
                        // GET /points/function/{functionId}/sorted - получить точки по ID функции (отсортированные)
                        logger.info("GET запрос: получение отсортированных точек для функции ID: " + functionId);
                        List<PointsDTO> points = pointRepository.findPointsByFunctionIdSortedAsDTO(functionId);
                        String json = mapper.writeValueAsString(points);
                        response.getWriter().write(json);
                        logger.info("Успешно возвращено " + points.size() + " отсортированных точек для функции ID: " + functionId);

                    } else {
                        // GET /points/function/{functionId} - получить точки по ID функции
                        logger.info("GET запрос: получение точек для функции ID: " + functionId);
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
            logger.severe("Точек для такой ф-ции нет: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Нет точек для ф-ции\"}");
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

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите конечную точку (single или bulk)\"}");

            } else if (pathInfo.equals("/single")) {
                // POST /points/single - создать одну точку
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                double xValue = jsonNode.get("x_value").asDouble();
                double yValue = jsonNode.get("y_value").asDouble();
                int functionId = jsonNode.get("function_id").asInt();

                logger.info("POST запрос: создание точки для функции ID: " + functionId + " с координатами (" + xValue + ", " + yValue + ")");

                pointRepository.createPoint(xValue, yValue, functionId);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Точка успешно создана\"}");
                logger.info("Успешно создана точка для функции ID: " + functionId);

            } else if (pathInfo.equals("/bulk")) {
                // POST /points/bulk - создать несколько точек
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                List<Point> points = mapper.readValue(
                        jsonNode.get("points").toString(),
                        new TypeReference<>(){}
                );

                int functionId = jsonNode.get("function_id").asInt();

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

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите переменную для обновления (x или y)\"}");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                String variable = pathParts[1];

                // Читаем значения из тела запроса
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                int functionId = jsonNode.get("id").asInt();
                double oldValue = jsonNode.get("oldValue").asDouble();
                double newValue = jsonNode.get("newValue").asDouble();

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

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // DELETE /points - удалить все точки
                logger.warning("DELETE запрос: удаление всех точек");
                pointRepository.deleteAllPoints();
                response.getWriter().write("{\"status\": \"Все точки успешно удалены\"}");
                logger.warning("Успешно удалены все точки");

            } else if (pathInfo.startsWith("/function/")) {
                // DELETE /points/function/{functionId} - удалить точки по ID функции
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && pathParts[2].matches("\\d+")) {
                    int functionId = Integer.parseInt(pathParts[2]);
                    logger.info("DELETE запрос: удаление точек для функции ID: " + functionId);

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