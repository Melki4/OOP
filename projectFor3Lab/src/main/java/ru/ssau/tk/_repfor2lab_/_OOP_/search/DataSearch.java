package ru.ssau.tk._repfor2lab_._OOP_.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Points;
import ru.ssau.tk._repfor2lab_._OOP_.entities.SimpleFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.*;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class DataSearch {
    private static final Logger logger = LoggerFactory.getLogger(DataSearch.class);
    @Autowired
    private UsersRepositories usersRepository;
    @Autowired
    private MathFunctionsRepositories mathFunctionsRepository;
    @Autowired
    private PointsRepositories pointsRepository;
    @Autowired
    private SimpleFunctionsRepositories simpleFunctionsRepository;

    public Optional<Users> findSingleUser(String login) {
        logger.info("Одиночный поиск пользователя по логину: {}", login);
        Optional<Users> user = usersRepository.findByLogin(login);
        if (user.isPresent()) {
            logger.info(" Пользователь '{}' найден", login);
        } else {
            logger.warn("Пользователь '{}' не найден", login);
        }
        return user;
    }

    public Optional<MathFunctions> findSingleMathFunction(Long functionId) {
        logger.info("Одиночный поиск математической функции по ID: {}", functionId);

        Optional<MathFunctions> function = mathFunctionsRepository.findByMathFunctionsID(functionId);
        if (function.isPresent()) {
            logger.info("Математическая функция '{}' найдена", function.get().getNameOfFunction());
        } else {
            logger.warn("Математическая функция с ID {} не найдена", functionId);
        }
        return function;
    }

    public Optional<MathFunctions> findSingleMathFunctionByName(String name) {
        logger.info("Одиночный поиск математической функции по имени: {}", name);
        Optional<MathFunctions> function = mathFunctionsRepository.findByNameOfFunction(name);
        if (function.isPresent()) {
            logger.info("Математическая функция '{}' найдена", name);
        } else {
            logger.warn("Математическая функция '{}' не найдена", name);
        }
        return function;
    }

    public List<MathFunctions> findMultipleMathFunctionsByUser(Long userId) {
        logger.info("Множественный поиск математических функций пользователя ID: {}", userId);
        List<MathFunctions> functions = mathFunctionsRepository.findByUsersUserID(userId);
        logger.info("Найдено {} математических функций для пользователя ID: {}",
                functions.size(), userId);
        return functions;
    }

    public List<Points> findMultiplePointsByFunction(Long functionId) {
        logger.info("Множественный поиск точек функции ID: {}", functionId);
        List<Points> points = pointsRepository.findByMathFunctionsMathFunctionsID(functionId);
        logger.info("Найдено {} точек для функции ID: {}", points.size(), functionId);
        return points;
    }

    public List<Object> searchWithSorting(String entity, String field, String value,
                                          String sortBy, boolean ascending) {
        logger.info("Поиск с сортировкой: сущность={}, поле={}, значение={}, сортировка по={}, порядок={}",
                entity, field, value, sortBy, ascending ? "ASC" : "DESC");

        List<Object> results = new ArrayList<>();
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        try {
            switch (entity.toUpperCase()) {
                case "USER":
                    results.addAll(searchUsersWithSort(field, value, sort));
                    break;
                case "MATH_FUNCTION":
                    results.addAll(searchMathFunctionsWithSort(field, value, sort));
                    break;
                case "POINT":
                    results.addAll(searchPointsWithSort(field, value, sort));
                    break;
                case "SIMPLE_FUNCTION":
                    results.addAll(searchSimpleFunctionsWithSort(field, value, sort));
                    break;
            }
        } catch (Exception e) {
            logger.error("Ошибка при поиске с сортировкой", e);
        }

        logger.info("Найдено результатов: {}", results.size());
        return results;
    }

    private List<Users> searchUsersWithSort(String field, String value, Sort sort) {
        switch (field.toUpperCase()) {
            case "LOGIN":
                return usersRepository.findByLoginContainingIgnoreCase(value, sort);
            case "ALL":
                return usersRepository.findAll(sort);
            default:
                return usersRepository.findAll(sort);
        }
    }

    private List<MathFunctions> searchMathFunctionsWithSort(String field, String value, Sort sort) {
        switch (field.toUpperCase()) {
            case "NAME":
                return mathFunctionsRepository.findByNameOfFunctionContainingIgnoreCase(value, sort);
            case "USER_ID":
                Long userId = Long.parseLong(value);
                return mathFunctionsRepository.findByUsersUserID(userId, sort);
            case "LEFT_BOARDER_RANGE":
                String[] range = value.split("-");
                Double minLeft = Double.parseDouble(range[0]);
                Double maxLeft = Double.parseDouble(range[1]);
                return mathFunctionsRepository.findByLeftBoarderBetween(minLeft, maxLeft, sort);
            case "RIGHT_BOARDER_RANGE":
                String[] range2 = value.split("-");
                Double minRight = Double.parseDouble(range2[0]);
                Double maxRight = Double.parseDouble(range2[1]);
                return mathFunctionsRepository.findByRightBoarderBetween(minRight, maxRight, sort);
            case "ALL":
                return mathFunctionsRepository.findAll(sort);
            default:
                return mathFunctionsRepository.findAll(sort);
        }
    }

    private List<Points> searchPointsWithSort(String field, String value, Sort sort) {
        switch (field.toUpperCase()) {
            case "FUNCTION_ID":
                Long functionId = Long.parseLong(value);
                return pointsRepository.findByMathFunctionsMathFunctionsID(functionId, sort);
            case "X_RANGE":
                String[] range = value.split("-");
                Double minX = Double.parseDouble(range[0]);
                Double maxX = Double.parseDouble(range[1]);
                return pointsRepository.findByxValueBetween(minX, maxX, sort);
            case "Y_RANGE":
                String[] range2 = value.split("-");
                Double minY = Double.parseDouble(range2[0]);
                Double maxY = Double.parseDouble(range2[1]);
                return pointsRepository.findByyValueBetween(minY, maxY, sort);
            case "ALL":
                return pointsRepository.findAll(sort);
            default:
                return pointsRepository.findAll(sort);
        }
    }
    private List<SimpleFunctions> searchSimpleFunctionsWithSort(String field, String value, Sort sort) {
        switch (field.toUpperCase()) {
            case "NAME":
                return simpleFunctionsRepository.findByLocalNameContainingIgnoreCase(value, sort);
            case "ALL":
                return simpleFunctionsRepository.findAll(sort);
            default:
                return simpleFunctionsRepository.findAll(sort);
        }
    }
}