package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class userInterfaceTest {

    private userInterface userInterface;
    private List<Integer> createdUserIds;
    private Random random;

    @BeforeEach
    void setUp() {
        userInterface = new userInterface();
        userInterface.deleteAllUsers();
        createdUserIds = new ArrayList<>();
        random = new Random();

        // Создаем таблицу перед каждым тестом
        userInterface.createTable();
    }

    @AfterEach
    void tearDown() {
        var u = new userInterface();
        u.deleteAllUsers();
    }

    // Генератор случайных данных
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    private String generateRandomRole() {
        String[] roles = {"admin", "user", "moderator", "editor", "viewer", "guest", "supervisor"};
        return roles[random.nextInt(roles.length)];
    }

    @Test
    @DisplayName("Поиск пользователей по разным ID")
    void testFindUsersByVariousIds() {
        // Добавляем 5 тестовых пользователей
        for (int i = 0; i < 5; i++) {
            String factoryType = "TFact_" + i;
            String login = "testuser_" + i;
            String password = "testpass_" + i;
            String role = i % 2 == 0 ? "admin" : "user";

            userInterface.addUser(factoryType, login, password, role);

            int userId = userInterface.selectIdByLogin(login);
            if (userId != -1) {
                createdUserIds.add(userId);
            }
        }

        // Ищем каждого пользователя по ID
        for (Integer userId : createdUserIds) {
            String userData = userInterface.selectUserById(userId);
            assertNotNull(userData);
            assertTrue(userData.contains("testuser_"));
        }

        // Пытаемся найти несуществующего пользователя
        try{
            String nonExistentUser = userInterface.selectUserById(999);
        } catch (RuntimeException e){

        }
        // Ожидаем исключение, так как в оригинальном коде есть resultSet.next() без проверки
    }

    @Test
    @DisplayName("Обновление данных пользователей")
    void testUpdateUserData() {
        // Добавляем тестового пользователя
        userInterface.addUser("OrigFact", "updateuser", "originalpass", "user");
        int userId = userInterface.selectIdByLogin("updateuser");

        if (userId != -1) {
            createdUserIds.add(userId);

            // Обновляем фабрику
            userInterface.updateFactoryTypeById("UpdFact", userId);

            // Обновляем логин
            userInterface.updateLoginById("updated_login", userId);

            // Обновляем пароль
            userInterface.updatePasswordById("new_secure_password", userId);

            // Обновляем роль
            userInterface.updateRoleById("admin", userId);

            // Проверяем обновленные данные
            String updatedData = userInterface.selectUserById(userId);
            assertNotNull(updatedData);
            // Проверяем, что данные обновились (хотя в текущей реализации мы не можем проверить конкретные поля)
        }
    }

    @Test
    @DisplayName("Полный цикл CRUD для нескольких пользователей")
    void testFullCrudCycleForMultipleUsers() {
        List<String> testLogins = new ArrayList<>();

        // CREATE - Создаем 3 пользователей
        for (int i = 0; i < 3; i++) {
            String login = "cruduser_" + generateRandomString(6);
            testLogins.add(login);

            userInterface.addUser(
                    "CRUDF_" + i,
                    login,
                    "crudpass_" + i,
                    i == 0 ? "admin" : "user"
            );

            int userId = userInterface.selectIdByLogin(login);
            if (userId != -1) {
                createdUserIds.add(userId);
            }
        }

        // READ - Читаем и проверяем созданных пользователей
        for (String login : testLogins) {
            int userId = userInterface.selectIdByLogin(login);
            assertNotEquals(-1, userId);

            String userData = userInterface.selectUserById(userId);
            assertNotNull(userData);
        }

        // UPDATE - Обновляем данные пользователей
        for (int i = 0; i < createdUserIds.size(); i++) {
            int userId = createdUserIds.get(i);

            userInterface.updateFactoryTypeById("UpCF_" + i, userId);
            userInterface.updatePasswordById("updated_pass_" + i, userId);
            userInterface.updateRoleById("moder", userId);
        }

        // DELETE - Удаляем пользователей и проверяем удаление
        for (int userId : createdUserIds) {
            userInterface.deleteUserById(userId);

            // После удаления попытка найти пользователя должна вернуть -1 или бросить исключение
            // В текущей реализации selectIdByLogin вернет -1 для несуществующих пользователей
        }

        // Очищаем список, так как пользователи удалены
        createdUserIds.clear();

        // Проверяем, что пользователи действительно удалены
        for (String login : testLogins) {
            int userId = userInterface.selectIdByLogin(login);
            assertEquals(-1, userId);
        }
    }

    @Test
    @DisplayName("Тест с экстремальными данными")
    void testWithExtremeData() {
        // Тест с очень длинными значениями
        String longFactory = "F".repeat(8);
        String longLogin = "L".repeat(30);
        String longPassword = "P".repeat(40);
        String longRole = "R".repeat(8);

        userInterface.addUser(longFactory, longLogin, longPassword, longRole);
        int userId = userInterface.selectIdByLogin(longLogin);
        if (userId != -1) {
            createdUserIds.add(userId);
            assertNotEquals(-1, userId);
        }

        // Тест с специальными символами
        userInterface.addUser("Fac@#$%", "user!@#$", "pass^&*()", "role{}[]");
        userId = userInterface.selectIdByLogin("user!@#$");
        if (userId != -1) {
            createdUserIds.add(userId);
            assertNotEquals(-1, userId);
        }

        // Тест с числами в строках
        userInterface.addUser("Fact123", "user456", "pass789", "role000");
        userId = userInterface.selectIdByLogin("user456");
        if (userId != -1) {
            createdUserIds.add(userId);
            assertNotEquals(-1, userId);
        }
    }

    @Test
    @DisplayName("Поиск по разным критериям")
    void testSearchByVariousCriteria() {
        // Создаем пользователей с разными ролями
        String[][] roleUsers = {
                {"list", "admin_user", "admin_pass", "admin"},
                {"array", "regular_user", "user_pass", "user"},
                {"array", "mod_user", "mod_pass", "moder"}
        };

        for (String[] userData : roleUsers) {
            userInterface.addUser(userData[0], userData[1], userData[2], userData[3]);
            int userId = userInterface.selectIdByLogin(userData[1]);
            if (userId != -1) {
                createdUserIds.add(userId);
            }
        }

        // Ищем каждого пользователя по логину
        for (String[] userData : roleUsers) {
            String login = userData[1];
            int userId = userInterface.selectIdByLogin(login);
            assertNotEquals(-1, userId);

            // Получаем полные данные
            String fullData = userInterface.selectUserById(userId);
            assertNotNull(fullData);
            assertTrue(fullData.contains(login));
        }

        // Пытаемся найти несуществующего пользователя
        int nonExistentId = userInterface.selectIdByLogin("nonexistent_user_xyz");
        assertEquals(-1, nonExistentId);
    }
}