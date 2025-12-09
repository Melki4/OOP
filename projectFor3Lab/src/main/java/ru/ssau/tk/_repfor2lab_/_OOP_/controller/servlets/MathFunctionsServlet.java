package ru.ssau.tk._repfor2lab_._OOP_.controller.servlets;

import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DaoException;
import ru.ssau.tk._repfor2lab_._OOP_.model.basicAUTH.AuthorizationService;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.model.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

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
import ru.ssau.tk._repfor2lab_._OOP_.model.service.MathFunctionService;

@WebServlet("/math-functions/*")
public class MathFunctionsServlet extends HttpServlet {
    private MathFunctionService mathFunctionService;
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(MathFunctionsServlet.class.getName());

    private static final Pattern ID_PATH_PATTERN =
            Pattern.compile("^/(get-by-user-id|create|update|delete-by-function-id|delete-by-user-id)/(\\d+)$");
    // ^/ - начало pathInfo
    // (get-by-user-id|create|update|delete-by-function-id|delete-by-user-id) - операция с ID
    // / - разделитель
    // (\\d+) - ID (только цифры)

    private static final Pattern FUNCTION_NAME_PATH_PATTERN =
            Pattern.compile("^/get-by-function-name/([a-zA-Z0-9()\\[\\]{}*+\\-^/xX\\s]+)$");
    // ^/get-by-function-name/ - конкретный префикс
    // ([a-zA-Z0-9()\[\]{}*+\-^/xX\s]+) - имя функции: буквы, цифры, скобки, математические операторы, пробелы
    // Пример: cos(x), sin(x), x^2+3*x-5

    @Override
    public void init() {
        mathFunctionService = new MathFunctionService();
        this.mapper = new ObjectMapper();
        logger.info("Сервлет MathFunctionsServlet успешно инициализирован");
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
                response.getWriter().write("{\"error\": \"Укажите параметры поиска\"}");
            } else if (pathInfo.startsWith("/get-by-user-id/")) {

                String path = request.getPathInfo();

                Matcher matcher = ID_PATH_PATTERN.matcher(path);

                Integer id;
                if (matcher.matches()) {
                    id = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if (!AuthorizationService.canAccessById(currentUser, id)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Доступ к данным пользователя запрещен\"}");
                    return;
                }

                List<MathFunctionsDTO> functions = mathFunctionService.findMathFunctionsByUserId(id);
                String json = mapper.writeValueAsString(functions);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + functions.size() + " функций для пользователя ID: " + id);
            } else if (pathInfo.startsWith("/get-by-function-name/")) {

                String path = request.getPathInfo();

                Matcher matcher = FUNCTION_NAME_PATH_PATTERN.matcher(path);

                String name;
                if (matcher.matches()) {
                    name = matcher.group(1);
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                logger.info("GET запрос: поиск математических функций с именем: " + name);

                List<MathFunctionsDTO> functions;

                functions = mathFunctionService.findMathFunctionsByName(name, currentUser.getUserId());

                String json = mapper.writeValueAsString(functions);
                response.getWriter().write(json);
                logger.info("Успешно возвращено " + functions.size() + " функций с именем: " + name);
            } else if (pathInfo.startsWith("/get-by-function-id/")) {

                String path = request.getPathInfo();

                Matcher matcher = ID_PATH_PATTERN.matcher(path);

                int function_id;
                if (matcher.matches()) {
                    function_id= Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                MathFunctionsDTO function;

                function = mathFunctionService.findMathFunctionByFunctionId(function_id);

                String json = mapper.writeValueAsString(function);
                response.getWriter().write(json);
                logger.info("Успешно возвращена функций с айди: " + function_id);
            } else if (pathInfo.equals("/get-complex-by-name")) {

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

        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
        }catch (DataDoesNotExistException e) {
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

        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        try {
            if (pathInfo.startsWith("/create/")) {

                String path = request.getPathInfo();

                Matcher matcher = ID_PATH_PATTERN.matcher(path);

                Integer ownerId;
                if (matcher.matches()) {
                    ownerId = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                String requestBody = request.getReader().lines().reduce("", String::concat);
                var jsonNode = mapper.readTree(requestBody);

                String functionName = jsonNode.get("function_name").asText();
                int amountOfDots = jsonNode.get("amount_of_dots").asInt();
                double leftBorder = jsonNode.get("left_border").asDouble();
                double rightBorder = jsonNode.get("right_border").asDouble();
                String functionType = jsonNode.get("function_type").asText();

                if(!ownerId.equals(currentUser.getUserId())) {
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

        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
        }
        catch (DataDoesNotExistException e) {
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

        Users currentUser = (Users) request.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Требуется аутентификация\"}");
            return;
        }

        try {
            if (pathInfo.startsWith("/update/")) {

                String path = request.getPathInfo();

                Matcher matcher = ID_PATH_PATTERN.matcher(path);

                int functionId;
                if (matcher.matches()) {
                    functionId = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }


                String requestBody = request.getReader().lines().reduce("", String::concat);
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

        } catch (DataDoesNotExistException e) {
            logger.severe("Функция не найдена при расширенном поиске: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Функция не найдена\"}");
        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
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

            } else if (pathInfo.startsWith("/delete-by-function-id/")) {

                String path = request.getPathInfo();

                Matcher matcher = ID_PATH_PATTERN.matcher(path);

                int functionId;
                if (matcher.matches()) {
                    functionId = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

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

            } else if (pathInfo.startsWith("/delete-by-user-id/")) {

                String path = request.getPathInfo();

                Matcher matcher = ID_PATH_PATTERN.matcher(path);

                Integer userId;
                if (matcher.matches()) {
                    userId = Integer.parseInt(matcher.group(2)); // 123
                } else{
                    response.getWriter().write("{\"error\": \"Некорректный запрос\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if(!userId.equals(currentUser.getUserId())){
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

        } catch (DaoException e) {
            logger.severe("Произошла ошибка на стороне дао: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\": \"Произошла ошибка на стороне дао\"}");
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