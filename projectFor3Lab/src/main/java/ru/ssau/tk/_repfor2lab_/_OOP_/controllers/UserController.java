package ru.ssau.tk._repfor2lab_._OOP_.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ru.ssau.tk._repfor2lab_._OOP_.DTO.CreateUserRequest;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.UsersRepositories;
import ru.ssau.tk._repfor2lab_._OOP_.DTO.UserDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UsersRepositories usersRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("Получение всех пользователей");
        List<UserDTO> users = usersRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/sorted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsersSorted() {
        logger.info("Получение всех пользователей, отсортированных по логину");
        List<UserDTO> users = usersRepository.findAllByOrderByLoginAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/id/login")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Integer> selectIdByLogin(@RequestBody Map<String, String> body) {
        String login = body.get("value");
        if (login == null || login.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentLogin = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin && !currentLogin.equals(login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Получение айди по логину");
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found: " + login));

        return ResponseEntity.ok(user.getUserID().intValue());
    }

    @GetMapping("/by-login/{login}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByLoginPath(@PathVariable String login) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentLogin = auth.getName();

        if (!"ADMIN".equals(auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .findFirst().orElse("USER"))) {
            if (!currentLogin.equals(login)) {
                return ResponseEntity.status(403).build();
            }
        }

        logger.info("Получение пользователя по логину: {}", login);
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(toDto(user));
    }

    @PostMapping("/auth/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        logger.info("Создание пользователя с логином: {}", request.getLogin());

        String role = "USER";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (isAdmin && "ADMIN".equals(request.getRole())) {
            role = "ADMIN";
        }
        Users user = new Users(request.getLogin(), request.getPassword(), role);
        user.setFactoryType(request.getFactoryType());

        Users saved = usersRepository.save(user);
        logger.info("Пользователь ID {} создан с ролью {}", saved.getUserID(), role);
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping("/check/id/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        logger.info("Проверка существования пользователя по айди");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin) {
            Users currentUser = usersRepository.findByLogin(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            if (!currentUser.getUserID().equals(id)) {
                return ResponseEntity.ok(false);
            }
        }
        boolean exists = usersRepository.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check/login/{login}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> existsByLogin(@PathVariable String login) {
        logger.info("Проверка существования пользователя по логину");
        if (login == null || login.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentLogin = auth.getName();

        String role = auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");

        if (!"ADMIN".equals(role) && !currentLogin.equals(login)) {
            return ResponseEntity.ok(false);
        }

        boolean exists = usersRepository.existsByLogin(login);
        return ResponseEntity.ok(exists);
    }

    @PutMapping("/{id}/factoryType")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<String> updateFactoryType(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newValue = body.get("value");
        logger.info("Запрос на обновление factoryType");
        if (newValue == null || newValue.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFactoryType(newValue);
        usersRepository.save(user);

        logger.info("factoryType обновлен");
        return ResponseEntity.ok("{\"status\": \"Пользователь успешно обновлен\"}");
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newValue = body.get("value");
        logger.info("Запрос на обновление пароля");
        if (newValue == null || newValue.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setPassword(newValue);
        usersRepository.save(user);
        logger.info("Пароль обновлен");
        return ResponseEntity.ok("{\"status\": \"Пользователь успешно обновлен\"}");
    }

    @PutMapping("/{id}/login")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<String> updateLogin(@PathVariable Long id, @RequestBody Map<String, String> body) {
        logger.info("Запрос на обновление логина");
        String newValue = body.get("value");
        if (newValue == null || newValue.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setLogin(newValue);
        usersRepository.save(user);
        logger.info("Логин обновлен");
        return ResponseEntity.ok("{\"status\": \"Пользователь успешно обновлен\"}");
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        logger.info("Запрос на обновление роли");
        String newValue = body.get("value");
        if (newValue == null || !("USER".equals(newValue) || "ADMIN".equals(newValue))) {
            return ResponseEntity.badRequest().build();
        }
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setRole(newValue);
        usersRepository.save(user);
        logger.info("Роль обновлена");
        return ResponseEntity.ok("{\"status\": \"Пользователь успешно обновлен\"}");
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAll() {
        logger.info("Запрос на удаление всех пользователей");
        usersRepository.deleteAll();
        return ResponseEntity.ok("{\"status\": \"Все пользователи успешно удалены\"}");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        logger.info("Запрос на удаление пользователя");
        usersRepository.deleteById(id);
        return ResponseEntity.ok("{\"status\": \"Пользователь успешно удален\"}");
    }

    private UserDTO toDto(Users user) {
        UserDTO dto = new UserDTO();
        dto.setUserID(user.getUserID());
        dto.setLogin(user.getLogin());
        dto.setRole(user.getRole());
        dto.setFactoryType(user.getFactoryType());
        return dto;
    }
}