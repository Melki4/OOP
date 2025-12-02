package ru.ssau.tk._repfor2lab_._OOP_.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ru.ssau.tk._repfor2lab_._OOP_.DTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.MathFunctionsRepositories;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.UsersRepositories;
import ru.ssau.tk._repfor2lab_._OOP_.service.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/math-functions")
public class MathFunctionsController {

    private static final Logger logger = LoggerFactory.getLogger(MathFunctionsController.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MathFunctionsRepositories mathFunctionsRepository;

    @Autowired
    private UsersRepositories usersRepository;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<List<MathFunctionsDTO>> findMathFunctionsByUserId(@PathVariable Long userId) {
        logger.info("Получение функций пользователя с ID: {}", userId);
        List<MathFunctionsDTO> functions = mathFunctionsRepository.findByUsersUserID(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/name/{functionName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MathFunctionsDTO>> findMathFunctionsByName(@PathVariable String functionName) {
        logger.info("Поиск функций по имени: {}", functionName);
        Optional<MathFunctions> functions = mathFunctionsRepository.findByNameOfFunction(functionName);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentRole = auth.getAuthorities().stream()
                .findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("USER");
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = userDetails.getUserId();

        List<MathFunctionsDTO> result = functions.stream()
                .filter(f -> "ADMIN".equals(currentRole) || f.getUsers().getUserID().equals(currentUserId))
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/complex/find")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MathFunctionsDTO> findFunctionComplex(
            @RequestParam Double leftBoard,
            @RequestParam Double rightBoard,
            @RequestParam Long amountOfDots,
            @RequestParam String functionName) {
        logger.info("Запрос на поиск по параметрам");

        MathFunctions function = mathFunctionsRepository
                .findByLeftBoarderGreaterThanEqualAndRightBoarderLessThanEqualAndAmountOfDotsAndNameOfFunction(
                        leftBoard, rightBoard, amountOfDots, functionName)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Function not found"));

        checkAccess(function);

        return ResponseEntity.ok(toDto(function));
    }

    @GetMapping("/complex/id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> findFunctionIdComplex(
            @RequestParam Double leftBoard,
            @RequestParam Double rightBoard,
            @RequestParam Long amountOfDots,
            @RequestParam String functionName) {
        logger.info("Запрос на получение ID функции по параметрам");

        MathFunctions function = mathFunctionsRepository
                .findByLeftBoarderGreaterThanEqualAndRightBoarderLessThanEqualAndAmountOfDotsAndNameOfFunction(
                        leftBoard, rightBoard, amountOfDots, functionName)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Function not found"));

        checkAccess(function);

        return ResponseEntity.ok("{\"functionId\": " + function.getMathFunctionsID() + "}");
    }

    @GetMapping("/check-complex")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> existsFunctionComplex(@RequestBody JsonNode body) {
        double leftBoard = body.get("leftBoard").asDouble();
        double rightBoard = body.get("rightBoard").asDouble();
        long amountOfDots = body.get("amountOfDots").asLong();
        String functionName = body.get("functionName").asText();
        logger.info("Запрос на проверку существования функции по параметрам");

        List<MathFunctions> functions = mathFunctionsRepository
                .findByLeftBoarderGreaterThanEqualAndRightBoarderLessThanEqualAndAmountOfDotsAndNameOfFunction(
                        leftBoard, rightBoard, amountOfDots, functionName);

        boolean exists = false;
        if (!functions.isEmpty()) {
            MathFunctions function = functions.get(0);
            try {
                checkAccess(function);
                exists = true;
            } catch (Exception e) {
                exists = false;
            }
        }

        return ResponseEntity.ok("{\"exists\": " + exists + "}");
    }

    @GetMapping("/complex-search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MathFunctionsDTO> findFunctionComplexPost(@RequestBody JsonNode body) {
        double leftBoard = body.get("leftBoard").asDouble();
        double rightBoard = body.get("rightBoard").asDouble();
        long amountOfDots = body.get("amountOfDots").asLong();
        String functionName = body.get("functionName").asText();
        logger.info("Запрос на получение функции по параметрам");

        MathFunctions function = mathFunctionsRepository
                .findByLeftBoarderGreaterThanEqualAndRightBoarderLessThanEqualAndAmountOfDotsAndNameOfFunction(
                        leftBoard, rightBoard, amountOfDots, functionName)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Function not found"));

        checkAccess(function);

        return ResponseEntity.ok(toDto(function));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createMathFunction(@RequestBody JsonNode body) {
        String functionName = body.get("function_name").asText();
        long amountOfDots = body.get("amount_of_dots").asLong();
        double leftBorder = body.get("left_border").asDouble();
        double rightBorder = body.get("right_border").asDouble();
        long ownerId = body.get("owner_id").asLong();
        String functionType = body.get("function_type").asText();
        logger.info("Запрос на создание функции");

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = userDetails.getUserId();
        if (ownerId != currentUserId) {
            return ResponseEntity.status(406).body("{\"error\": \"Ошибка доступа\"}");
        }

        MathFunctions function = new MathFunctions();
        function.setNameOfFunction(functionName);
        function.setAmountOfDots(amountOfDots);
        function.setLeftBoarder(leftBorder);
        function.setRightBoarder(rightBorder);
        function.setUsers(new Users());
        function.getUsers().setUserID(ownerId);

        function.setSimpleFunctions(new ru.ssau.tk._repfor2lab_._OOP_.entities.SimpleFunctions());
        function.getSimpleFunctions().setLocalName(functionType);

        mathFunctionsRepository.save(function);

        return ResponseEntity.status(201).body("{\"status\": \"Математическая функция успешно создана\"}");
    }

    @PutMapping("/function/{functionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateFunctionName(
            @PathVariable Long functionId,
            @RequestBody JsonNode body) {
        logger.info("Запрос на обновление functionName");

        MathFunctions function = mathFunctionsRepository.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Function not found"));

        checkAccess(function);

        String newName = body.get("function_name").asText();
        function.setNameOfFunction(newName);
        mathFunctionsRepository.save(function);

        return ResponseEntity.ok("{\"status\": \"Имя функции успешно обновлено\"}");
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllFunctions() {
        logger.info("Запрос на удаление всех функций");
        mathFunctionsRepository.deleteAll();
        return ResponseEntity.ok("{\"status\": \"Все математические функции успешно удалены\"}");
    }

    @DeleteMapping("/function/{functionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteFunctionById(@PathVariable Long functionId) {
        logger.info("Запрос на удаление функций по functionId");
        MathFunctions function = mathFunctionsRepository.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Function not found"));
        checkAccess(function);
        mathFunctionsRepository.deleteById(functionId);
        return ResponseEntity.ok("{\"status\": \"Математическая функция успешно удалена\"}");
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<String> deleteFunctionsByUserId(@PathVariable Long userId) {
        logger.info("Запрос на удаление функций по userId");
        mathFunctionsRepository.deleteByUsersUserID(userId);
        return ResponseEntity.ok("{\"status\": \"Математические функции пользователя успешно удалены\"}");
    }

    private MathFunctionsDTO toDto(MathFunctions f) {
        MathFunctionsDTO dto = new MathFunctionsDTO();
        dto.setMathFunctionsID(f.getMathFunctionsID());
        dto.setNameOfFunction(f.getNameOfFunction());
        dto.setAmountOfDots(f.getAmountOfDots());
        dto.setLeftBoarder(f.getLeftBoarder());
        dto.setRightBoarder(f.getRightBoarder());
        dto.setFunctionType(f.getSimpleFunctions().getLocalName());
        dto.setOwnerID(f.getUsers().getUserID());
        return dto;
    }

    private void checkAccess(MathFunctions function) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        if (!"ADMIN".equals(role) && !function.getUsers().getUserID().equals(currentUserId)) {
            throw new RuntimeException("Access denied");
        }
    }
}