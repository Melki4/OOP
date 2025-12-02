package ru.ssau.tk._repfor2lab_._OOP_.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.ssau.tk._repfor2lab_._OOP_.DTO.SimpleFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.entities.SimpleFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.SimpleFunctionsRepositories;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/simple-functions")
public class SimpleFunctionController {

    private static final Logger logger = LoggerFactory.getLogger(SimpleFunctionController.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SimpleFunctionsRepositories simpleFunctionsRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SimpleFunctionsDTO>> findAllSimpleFunctions() {
        logger.info("Получение всех простых функций");
        List<SimpleFunctionsDTO> functions = simpleFunctionsRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/sorted")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SimpleFunctionsDTO>> findAllSorted() {
        logger.info("Получение отсортированных простых функций");
        List<SimpleFunctionsDTO> functions = simpleFunctionsRepository.findAllByOrderByLocalNameAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/check/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> existsByName(@PathVariable String name) {
        logger.info("Проверка существования функции: {}", name);
        boolean exists = simpleFunctionsRepository.existsByLocalName(name);
        return ResponseEntity.ok("{\"exists\": " + exists + "}");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createFunction(@RequestBody JsonNode body) {
        String localName = body.get("value").asText();
        logger.info("Создание простой функции: {}", localName);

        SimpleFunctions function = new SimpleFunctions();
        function.setLocalName(localName);
        simpleFunctionsRepository.save(function);

        return ResponseEntity.status(201).body("{\"status\": \"Простая функция успешно создана\"}");
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateSimpleFunctionName(
            @RequestBody JsonNode body) {
        String oldName = body.get("oldName").asText();
        String newName = body.get("newName").asText();

        if (!simpleFunctionsRepository.existsByLocalName(oldName)) {
            return ResponseEntity.notFound().build();
        }
        simpleFunctionsRepository.deleteByLocalName(oldName);

        SimpleFunctions newFunction = new SimpleFunctions();
        newFunction.setLocalName(newName);
        simpleFunctionsRepository.save(newFunction);

        logger.info("Функция '{}' переименована в '{}'", oldName, newName);
        return ResponseEntity.ok("{\"status\": \"Имя простой функции успешно обновлено\"}");
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAll() {
        logger.warn("Удаление всех простых функций");
        simpleFunctionsRepository.deleteAll();
        return ResponseEntity.ok("{\"status\": \"Все простые функции успешно удалены\"}");
    }

    @DeleteMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteByName(@PathVariable String name) {
        logger.info("Удаление функции: {}", name);
        if (!simpleFunctionsRepository.existsByLocalName(name)) {
            return ResponseEntity.notFound().build();
        }
        simpleFunctionsRepository.deleteByLocalName(name);
        return ResponseEntity.ok("{\"status\": \"Простая функция успешно удалена\"}");
    }

    private SimpleFunctionsDTO toDto(SimpleFunctions f) {
        SimpleFunctionsDTO dto = new SimpleFunctionsDTO();
        dto.setLocalName(f.getLocalName());
        return dto;
    }
}