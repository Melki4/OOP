package ru.ssau.tk._repfor2lab_._OOP_.controllers;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
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

import ru.ssau.tk._repfor2lab_._OOP_.DTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Points;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.PointsRepositories;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.MathFunctionsRepositories;
import ru.ssau.tk._repfor2lab_._OOP_.service.CustomUserDetails;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/points")
public class PointsController {

    private static final Logger logger = LoggerFactory.getLogger(PointsController.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PointsRepositories pointsRepository;

    @Autowired
    private MathFunctionsRepositories mathFunctionsRepository;

    @GetMapping("/function/{functionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PointsDTO>> getPointsByFunctionId(@PathVariable Long functionId) {
        checkFunctionAccess(functionId);
        logger.info("Запрос на получение точек по айди функции");
        List<PointsDTO> points = pointsRepository.findByMathFunctionsMathFunctionsID(functionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(points);
    }

    @GetMapping("/function/{functionId}/sorted")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PointsDTO>> getPointsByFunctionIdSorted(@PathVariable Long functionId) {
        checkFunctionAccess(functionId);
        List<PointsDTO> points = pointsRepository.findByMathFunctionsMathFunctionsID(functionId,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "xValue"))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        logger.info("Запрос на получение отсортированных точек по айди функции");
        return ResponseEntity.ok(points);
    }

    @PostMapping("/single")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createSinglePoint(@RequestBody JsonNode body) {
        double xValue = body.get("x_value").asDouble();
        double yValue = body.get("y_value").asDouble();
        long functionId = body.get("function_id").asLong();
        logger.info("Запрос на создание одной точки");

        checkFunctionAccess(functionId);

        Points point = new Points();
        point.setxValue(xValue);
        point.setyValue(yValue);
        point.setMathFunctions(new MathFunctions());
        point.getMathFunctions().setMathFunctionsID(functionId);

        pointsRepository.save(point);

        return ResponseEntity.status(201).body("{\"status\": \"Точка успешно создана\"}");
    }

    @PostMapping("/bulk")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createBulkPoints(@RequestBody JsonNode body) {
        long functionId = body.get("function_id").asLong();
        checkFunctionAccess(functionId);
        logger.info("Запрос на создание нескольких точек");

        try {
            List<ru.ssau.tk._repfor2lab_._OOP_.DTO.PointsDTO> points =
                    mapper.readValue(
                            body.get("points").traverse(),
                            new TypeReference<List<PointsDTO>>() {}
                    );

            List<Points> entities = points.stream().map(p -> {
                Points point = new Points();
                point.setxValue(p.getxValue());
                point.setyValue(p.getyValue());
                point.setMathFunctions(new MathFunctions());
                point.getMathFunctions().setMathFunctionsID(functionId);
                return point;
            }).collect(Collectors.toList());

            pointsRepository.saveAll(entities);
            return ResponseEntity.status(201).body("{\"status\": \"Точки успешно созданы\"}");

        } catch (Exception e) {
            logger.error("Ошибка парсинга точек: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"error\": \"Неверный формат точек\"}");
        }
    }

    @PutMapping("/x")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateXValue(@RequestBody JsonNode body) {
        long functionId = body.get("id").asLong();
        double oldValue = body.get("oldValue").asDouble();
        double newValue = body.get("newValue").asDouble();
        logger.info("Запрос на обновление x_value");

        checkFunctionAccess(functionId);

        pointsRepository.findByMathFunctionsMathFunctionsIDAndXValue(functionId, oldValue)
                .ifPresent(point -> {
                    point.setxValue(newValue);
                    pointsRepository.save(point);
                });

        return ResponseEntity.ok("{\"status\": \"Значение успешно обновлено\"}");
    }

    @PutMapping("/y")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateYValue(@RequestBody JsonNode body) {
        long functionId = body.get("id").asLong();
        double oldValue = body.get("oldValue").asDouble();
        double newValue = body.get("newValue").asDouble();
        logger.info("Запрос на обновление y_value");

        checkFunctionAccess(functionId);

        pointsRepository.findByMathFunctionsMathFunctionsIDAndYValue(functionId, oldValue)
                .ifPresent(point -> {
                    point.setyValue(newValue);
                    pointsRepository.save(point);
                });

        return ResponseEntity.ok("{\"status\": \"Значение успешно обновлено\"}");
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllPoints() {
        logger.info("Запрос на удаление всех точек");
        pointsRepository.deleteAll();
        return ResponseEntity.ok("{\"status\": \"Все точки успешно удалены\"}");
    }

    @DeleteMapping("/function/{functionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deletePointsByFunctionId(@PathVariable Long functionId) {
        logger.info("Запрос на удаление точек по айди функции");
        checkFunctionAccess(functionId);
        pointsRepository.deleteByMathFunctionsMathFunctionsID(functionId);
        return ResponseEntity.ok("{\"status\": \"Точки для функции успешно удалены\"}");
    }

    private PointsDTO toDto(Points point) {
        PointsDTO dto = new PointsDTO();
        dto.setPointsID(point.getPointID());
        dto.setxValue(point.getxValue());
        dto.setyValue(point.getyValue());
        dto.setFunctionID(point.getMathFunctions().getMathFunctionsID());
        return dto;
    }

    private void checkFunctionAccess(Long functionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getUserId(); // ← безопасно

        MathFunctions function = mathFunctionsRepository.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Function not found"));

        if (!"ADMIN".equals(role) && !function.getUsers().getUserID().equals(currentUserId)) {
            throw new RuntimeException("Access denied");
        }
    }
}