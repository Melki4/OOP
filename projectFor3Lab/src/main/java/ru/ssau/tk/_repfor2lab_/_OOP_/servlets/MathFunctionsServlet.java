package ru.ssau.tk._repfor2lab_._OOP_.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.repositories.MathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcMathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/math-functions/*")
public class MathFunctionsServlet extends HttpServlet {
    private JdbcMathFunctionRepository mathFunctionRepository;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(MathFunctionsServlet.class.getName());

    @Override
    public void init() {
        this.mathFunctionRepository = new JdbcMathFunctionRepository();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет MathFunctionsServlet успешно инициализирован");
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
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Укажите параметры поиска\"}");
            } else if (pathInfo.startsWith("/user/")) {
                // GET /math-functions/user/{userId} - получить функции по ID пользователя
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && pathParts[2].matches("\\d+")) {
                    int userId = Integer.parseInt(pathParts[2]);
                    logger.info("GET запрос: получение математических функций для пользователя с ID: " + userId);

                    if (!AuthorizationService.canAccessUserDataById(currentUser, userId)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Access denied to this user data\"}");
                        return;
                    }

                    List<MathFunctionsDTO> functions = mathFunctionRepository.findMathFunctionsByUserIdAsDTO(userId);
                    String json = mapper.writeValueAsString(functions);
                    response.getWriter().write(json);
                    logger.info("Успешно возвращено " + functions.size() + " функций для пользователя ID: " + userId);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат ID пользователя\"}");
                }

            } else if (pathInfo.startsWith("/name/")) {
                // GET /math-functions/name/{functionName} - получить функции по имени
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3) {
                    String functionName = pathParts[2];
                    logger.info("GET запрос: поиск математических функций с именем: " + functionName);

                    List<MathFunctions> functions = mathFunctionRepository.findMathFunctionsByName(functionName);
                    List<MathFunctions> returnable_array = new ArrayList<>();

                    if (!currentUser.getRole().equals("admin")){
                        for (var el : functions){
                            if (el.getOwnerId() == currentUser.getUserId()) returnable_array.add(el);
                        }
                    } else{
                        returnable_array = functions;
                    }

                    String json = mapper.writeValueAsString(returnable_array);
                    response.getWriter().write(json);
                    logger.info("Успешно возвращено " + returnable_array.size() + " функций с именем: " + functionName);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат имени функции\"}");
                }

            } else if (pathInfo.startsWith("/complex/")) {
                String[] pathParts = pathInfo.split("/");

                if (pathParts.length >= 3 && "find".equals(pathParts[2])) {
                    // GET /math-functions/complex/find - сложный поиск функции (через query parameters)
                    double leftBoard = Double.parseDouble(request.getParameter("leftBoard"));
                    double rightBoard = Double.parseDouble(request.getParameter("rightBoard"));
                    int amountOfDots = Integer.parseInt(request.getParameter("amountOfDots"));
                    String functionName = request.getParameter("functionName");

                    logger.info("GET запрос: сложный поиск функции с параметрами: name=" + functionName +
                            ", leftBoard=" + leftBoard + ", rightBoard=" + rightBoard +
                            ", dots=" + amountOfDots);

                    MathFunctions function = mathFunctionRepository.findMathFunctionComplex(
                            leftBoard, rightBoard, amountOfDots, functionName);

                    if (function.getOwnerId() != currentUser.getUserId() && !currentUser.getRole().equals("admin")){
                        System.out.println(currentUser.getRole());
                        logger.severe("Математические функции не найдена: ");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\": \"Функция не найдена\"}");
                        return;
                    }

                    String json = mapper.writeValueAsString(function);
                    response.getWriter().write(json);
                    logger.info("Успешно выполнена сложный поиск функции");
                } else if (pathParts.length >= 3 && "id".equals(pathParts[2])) {
                    // GET /math-functions/complex/id - сложный поиск ID функции (через query parameters)
                    double leftBoard = Double.parseDouble(request.getParameter("leftBoard"));
                    double rightBoard = Double.parseDouble(request.getParameter("rightBoard"));
                    int amountOfDots = Integer.parseInt(request.getParameter("amountOfDots"));
                    String functionName = request.getParameter("functionName");

                    logger.info("GET запрос: сложный поиск ID функции с параметрами: name=" + functionName);

                    MathFunctions function = mathFunctionRepository.findMathFunctionComplex(
                            leftBoard, rightBoard, amountOfDots, functionName);

                    if (function.getOwnerId() != currentUser.getUserId() && !currentUser.getRole().equals("admin")){
                        logger.severe("Математические функции не найдена: ");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\": \"Функция не найдена\"}");
                        return;
                    }

                    Integer functionId = function.getFunctionId();
                    response.getWriter().write("{\"functionId\": " + functionId + "}");
                    logger.info("Успешно найден ID функции: " + functionId);
                }

            } else if (pathInfo.equals("/check-complex")) {
                // POST /math-functions/check-complex - сложная проверка существования (через тело запроса)
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                double leftBoard = jsonNode.get("leftBoard").asDouble();
                double rightBoard = jsonNode.get("rightBoard").asDouble();
                int amountOfDots = jsonNode.get("amountOfDots").asInt();
                String functionName = jsonNode.get("functionName").asText();

                logger.info("POST запрос: сложная проверка существования функции: " + functionName);

                boolean exists = mathFunctionRepository.existsFunctionComplex(
                        leftBoard, rightBoard, amountOfDots, functionName);

                if (exists){
                    MathFunctions function = mathFunctionRepository.findMathFunctionComplex(
                            leftBoard, rightBoard, amountOfDots, functionName);

                    if (function.getOwnerId() != currentUser.getUserId() && !currentUser.getRole().equals("admin")){
                        logger.severe("Математическая функция не найдена: ");
                        response.setStatus(HttpServletResponse.SC_OK);
                        exists = false;
                        response.getWriter().write("{\"exists\": " + exists + "}");
                        return;
                    }
                }

                response.getWriter().write("{\"exists\": " + exists + "}");
                logger.info("Результат сложной проверки существования функции '" + functionName + "': " + exists);

            } else if (pathInfo.equals("/complex-search")) {
                // POST /math-functions/complex-search - сложный поиск (через тело запроса)
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                double leftBoard = jsonNode.get("leftBoard").asDouble();
                double rightBoard = jsonNode.get("rightBoard").asDouble();
                int amountOfDots = jsonNode.get("amountOfDots").asInt();
                String functionName = jsonNode.get("functionName").asText();

                logger.info("POST запрос: сложный поиск функции: " + functionName);

                MathFunctions function = mathFunctionRepository.findMathFunctionComplex(
                        leftBoard, rightBoard, amountOfDots, functionName);

                if (function.getOwnerId() != currentUser.getUserId() && !currentUser.getRole().equals("admin")){
                    logger.severe("Математические функции не найдена: ");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Функция не найдена\"}");
                    return;
                }

                String json = mapper.writeValueAsString(function);
                response.getWriter().write(json);
                logger.info("Успешно выполнен сложный поиск функции");

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Математические функции не найдены: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Функции не найдены\"}");
        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в GET запросе: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат числа\"}");
        } catch (Exception e) {
            logger.severe("Ошибка при выполнении GET запроса математических функций: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                // POST /math-functions - создание математической функции
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                String functionName = jsonNode.get("function_name").asText();
                int amountOfDots = jsonNode.get("amount_of_dots").asInt();
                double leftBorder = jsonNode.get("left_border").asDouble();
                double rightBorder = jsonNode.get("right_border").asDouble();
                int ownerId = jsonNode.get("owner_id").asInt();
                String functionType = jsonNode.get("function_type").asText();

                if(ownerId!=currentUser.getUserId()) {
                    logger.severe("Вы пытаетесь создать ф-цию другому пользователю: ");
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    response.getWriter().write("{\"error\": \"Ошибка доступа\"}");
                    return;
                }

                logger.info("POST запрос: создание математической функции: " + functionName + " для пользователя ID: " + ownerId);

                mathFunctionRepository.createMathFunction(
                        functionName, amountOfDots, leftBorder, rightBorder, ownerId, functionType
                );

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"status\": \"Математическая функция успешно создана\"}");
                logger.info("Успешно создана математическая функция: " + functionName);

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Функция не найдена при сложном поиске: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Функция не найдена\"}");
        } catch (Exception e) {
            logger.severe("Ошибка создания математической функции: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный запрос: " + e.getMessage() + "\"}");
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
                response.getWriter().write("{\"error\": \"Укажите ID функции для обновления\"}");
                return;
            }

            if (pathInfo.startsWith("/function/")) {
                // PUT /math-functions/function/{functionId} - обновление имени функции
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && pathParts[2].matches("\\d+")) {
                    int functionId = Integer.parseInt(pathParts[2]);

                    String requestBody = request.getReader().lines().reduce("", String::concat);
                    String newName = mapper.readTree(requestBody).get("function_name").asText();

                    logger.info("PUT запрос: обновление имени функции ID: " + functionId + " на: " + newName);
                    MathFunctionsDTO n = mathFunctionRepository.findMathFunctionByFunctionId(functionId);

                    if(n.getOwnerId()!=currentUser.getUserId()){
                        logger.severe("Вы пытаетесь изменить ф-цию другому пользователю: ");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Ошибка доступа\"}");
                        return;
                    }

                    mathFunctionRepository.updateFunctionNameByFunctionId(newName, functionId);
                    response.getWriter().write("{\"status\": \"Имя функции успешно обновлено\"}");
                    logger.info("Успешно обновлено имя функции ID: " + functionId);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат ID функции\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Ресурс не найден\"}");
            }

        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в PUT запросе: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (Exception e) {
            logger.severe("Ошибка обновления математической функции: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный запрос: " + e.getMessage() + "\"}");
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

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // DELETE /math-functions - удалить все функции

                // Проверяем авторизацию
                if (!AuthorizationService.hasAdminAccess(currentUser, "GET", request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Insufficient permissions\"}");
                    return;
                }

                logger.warning("DELETE запрос: удаление всех математических функций");
                mathFunctionRepository.deleteAllFunctions();
                response.getWriter().write("{\"status\": \"Все математические функции успешно удалены\"}");
                logger.warning("Успешно удалены все математические функции");

            } else if (pathInfo.startsWith("/function/")) {
                // DELETE /math-functions/function/{functionId} - удалить функцию по ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && pathParts[2].matches("\\d+")) {
                    int functionId = Integer.parseInt(pathParts[2]);

                    MathFunctionsDTO n = mathFunctionRepository.findMathFunctionByFunctionId(functionId);

                    if(n.getOwnerId()!=currentUser.getUserId()){
                        logger.severe("Вы пытаетесь удалить ф-цию другому пользователю: ");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Ошибка доступа\"}");
                        return;
                    }

                    logger.info("DELETE запрос: удаление математической функции с ID: " + functionId);

                    mathFunctionRepository.deleteMathFunctionByFunctionId(functionId);
                    response.getWriter().write("{\"status\": \"Математическая функция успешно удалена\"}");
                    logger.info("Успешно удалена математическая функция ID: " + functionId);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат ID функции\"}");
                }

            } else if (pathInfo.startsWith("/user/")) {
                // DELETE /math-functions/user/{userId} - удалить функции по ID пользователя
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && pathParts[2].matches("\\d+")) {
                    int userId = Integer.parseInt(pathParts[2]);

                    if(userId!=currentUser.getUserId()){
                        logger.severe("Вы пытаетесь удалить ф-ции другому пользователю: ");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Ошибка доступа\"}");
                        return;
                    }

                    logger.info("DELETE запрос: удаление математических функций для пользователя ID: " + userId);

                    mathFunctionRepository.deleteMathFunctionsByUserId(userId);
                    response.getWriter().write("{\"status\": \"Математические функции пользователя успешно удалены\"}");
                    logger.info("Успешно удалены математические функции для пользователя ID: " + userId);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат ID пользователя\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат запроса для удаления\"}");
            }

        } catch (NumberFormatException e) {
            logger.severe("Ошибка формата числа в DELETE запросе: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (Exception e) {
            logger.severe("Ошибка удаления математических функций: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }
}