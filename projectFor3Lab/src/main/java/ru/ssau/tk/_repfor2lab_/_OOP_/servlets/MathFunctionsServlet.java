package ru.ssau.tk._repfor2lab_._OOP_.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

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
import ru.ssau.tk._repfor2lab_._OOP_.service.MathFunctionService;

@WebServlet("/math-functions/*")
public class MathFunctionsServlet extends HttpServlet {
//    private JdbcMathFunctionRepository mathFunctionRepository;
    private MathFunctionService mathFunctionService;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(MathFunctionsServlet.class.getName());

    @Override
    public void init() {
//        this.mathFunctionRepository = new JdbcMathFunctionRepository();
        mathFunctionService = new MathFunctionService();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет MathFunctionsServlet успешно инициализирован");
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
                response.getWriter().write("{\"error\": \"Укажите параметры поиска\"}");
            } else if (pathInfo.startsWith("/get-by-user-id")) {
                Map<String, String[]> parameters = request.getParameterMap();
                Integer id;
                if(parameters.size() > 1) throw new RuntimeException("Слишком много параметров в запросе");

                if (parameters.containsKey("user-id")){
                    id = Integer.parseInt(parameters.get("user-id")[0]);

                    if (!AuthorizationService.canAccessById(currentUser, id)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                        return;
                    }

                    List<MathFunctionsDTO> functions = mathFunctionService.findMathFunctionsByUserId(id);
                    String json = mapper.writeValueAsString(functions);
                    response.getWriter().write(json);
                    logger.info("Успешно возвращено " + functions.size() + " функций для пользователя ID: " + id);

                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат ID пользователя\"}");
                }
            } else if (pathInfo.startsWith("/get-by-function-name")) {
                Map<String, String[]> parameters = request.getParameterMap();

                if(parameters.size() > 1) throw new RuntimeException("Слишком много параметров в запросе");

                if (parameters.containsKey("name")){
                    String name = parameters.get("name")[0];
                    logger.info("GET запрос: поиск математических функций с именем: " + name);

                    List<MathFunctionsDTO> functions;

                    functions = mathFunctionService.findMathFunctionsByName(name, currentUser.getUserId());

                    String json = mapper.writeValueAsString(functions);
                    response.getWriter().write(json);
                    logger.info("Успешно возвращено " + functions.size() + " функций с именем: " + name);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Неверный формат имени функции\"}");
                }

            } else if (pathInfo.startsWith("/get-complex-by-name")) {

                double leftBoard = Double.parseDouble(request.getParameter("leftBoard"));
                double rightBoard = Double.parseDouble(request.getParameter("rightBoard"));
                int amountOfDots = Integer.parseInt(request.getParameter("amountOfDots"));
                String functionName = request.getParameter("function-name");

                logger.info("GET запрос: расширенный поиск функции с параметрами: name=" + functionName +
                        ", leftBoard=" + leftBoard + ", rightBoard=" + rightBoard +
                        ", dots=" + amountOfDots);

                MathFunctionsDTO function = mathFunctionService.findMathFunctionComplex(
                        leftBoard, rightBoard, amountOfDots, functionName);

                String json = mapper.writeValueAsString(function);
                response.getWriter().write(json);
                logger.info("Успешно выполнен расширенный поиск функции");

            }  else if (pathInfo.equals("/check-complex")) {
                double leftBoard = Double.parseDouble(request.getParameter("leftBoard"));
                double rightBoard = Double.parseDouble(request.getParameter("rightBoard"));
                int amountOfDots = Integer.parseInt(request.getParameter("amountOfDots"));
                String functionName = request.getParameter("function-name");

                logger.info("POST запрос: расширенная проверка существования функции: " + functionName);

                boolean exists = mathFunctionService.existsMathFunctionComplex(
                        leftBoard, rightBoard, amountOfDots, functionName);

                response.getWriter().write("{\"exists\": " + exists + "}");
                logger.info("Результат расширенной проверки существования функции '" + functionName + "': " + exists);
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

        // Проверка аутентификации
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        try {
            if (pathInfo.equals("/create")) {
                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                String functionName = jsonNode.get("function_name").asText();
                int amountOfDots = jsonNode.get("amount_of_dots").asInt();
                double leftBorder = jsonNode.get("left_border").asDouble();
                double rightBorder = jsonNode.get("right_border").asDouble();
                int ownerId = jsonNode.get("owner_id").asInt();
                String functionType = jsonNode.get("function_type").asText();

                if(ownerId != currentUser.getUserId()) {
                    logger.severe("Попытка создания функции для другого пользователя");
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    response.getWriter().write("{\"error\": \"Ошибка доступа\"}");
                    return;
                }

                logger.info("POST запрос: создание математической функции: " + functionName + " для пользователя ID: " + ownerId);

                if (mathFunctionService.existsMathFunctionComplex(leftBorder, rightBorder, amountOfDots, functionName)){
                    logger.info("Функция уже существует: ");
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write("{\"error\": \"Функция уже существует\"}");
                    return;
                }

                mathFunctionService.createMathFunction(
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
            logger.severe("Функция не найдена при расширенном поиске: " + e.getMessage());
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

        // Проверка аутентификации
        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        try {
            if (pathInfo.startsWith("/update")) {
                String requestBody = request.getReader().lines().reduce("", String::concat);
                int functionId = mapper.readTree(requestBody).get("function-id").asInt();
                String newName = mapper.readTree(requestBody).get("function-name").asText();

                logger.info("PUT запрос: обновление имени функции ID: " + functionId + " на: " + newName);
                MathFunctionsDTO n = mathFunctionService.findMathFunctionByFunctionId(functionId);

                if(!Objects.equals(n.getOwnerId(), currentUser.getUserId())){
                    logger.severe("Попытка изменения функции другого пользователя");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Ошибка доступа\"}");
                    return;
                }

                mathFunctionService.updateFunctionName(newName, functionId);
                response.getWriter().write("{\"status\": \"Имя функции успешно обновлено\"}");
                logger.info("Успешно обновлено имя функции ID: " + functionId);

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

        // Проверка аутентификации
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

                logger.warning("DELETE запрос: удаление всех математических функций");
                mathFunctionService.deleteAllFunctions();
                response.getWriter().write("{\"status\": \"Все математические функции успешно удалены\"}");
                logger.warning("Успешно удалены все математические функции");

            } else if (pathInfo.startsWith("/delete-function-by-function-id")) {
                String requestBody = request.getReader().lines().reduce("", String::concat);
                int functionId = mapper.readTree(requestBody).get("function-id").asInt();

                MathFunctionsDTO n = mathFunctionService.findMathFunctionByFunctionId(functionId);

                if(!Objects.equals(n.getOwnerId(), currentUser.getUserId())){
                    logger.severe("Попытка удаления функции другого пользователя");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Ошибка доступа\"}");
                    return;
                }

                logger.info("DELETE запрос: удаление математической функции с ID: " + functionId);

                mathFunctionService.deleteMathFunctionByFunctionId(functionId);
                response.getWriter().write("{\"status\": \"Математическая функция успешно удалена\"}");
                logger.info("Успешно удалена математическая функция ID: " + functionId);

            } else if (pathInfo.startsWith("/delete-function-by-user-id")) {

                String requestBody = request.getReader().lines().reduce("", String::concat);
                int userId = mapper.readTree(requestBody).get("user-id").asInt();

                if(userId != currentUser.getUserId()){
                    logger.severe("Попытка удаления функций другого пользователя");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Ошибка доступа\"}");
                    return;
                }

                logger.info("DELETE запрос: удаление математических функций для пользователя ID: " + userId);

                mathFunctionService.deleteMathFunctionsByUserId(userId);
                response.getWriter().write("{\"status\": \"Математические функции пользователя успешно удалены\"}");
                logger.info("Успешно удалены математические функции для пользователя ID: " + userId);

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неверный формат запроса для удаления\"}");
            }

        } catch (DataDoesNotExistException e) {
            logger.severe("Математическая ф-ция не найдена " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Математическая ф-ция не найдена\"}");
        }
        catch (NumberFormatException e) {
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