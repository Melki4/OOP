package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcUserRepositoryTest1 {

    static List<Users> array = new ArrayList<>();
    static List<String> arrayNames = new ArrayList<>();
    static JdbcUserRepository repository;

    @BeforeAll
    static void setUp() {
        repository = new JdbcUserRepository();
        repository.createTable();

        arrayNames.add("vovapain");
        arrayNames.add("yojo");
        arrayNames.add("kartavaya");
        arrayNames.add("owlneverdie");
        arrayNames.add("ni4r");
        arrayNames.add("wesslo");
        arrayNames.add("a_username"); // Для проверки сортировки
        arrayNames.add("z_username"); // Для проверки сортировки

        // Создаем пользователей с проверкой на дубликаты
        for (String login : arrayNames) {
            if (!repository.existsUserByLogin(login)) {
                String role = login.contains("admin") ? "admin" : "user";
                String factoryType = login.length() % 2 == 0 ? "array" : "list";
                repository.createUser(factoryType, login, "defaultPass", role);
            }
        }

        array = repository.findAllUsers();
    }

    @AfterAll
    static void tearDown() {
        repository.deleteAllUsers();
    }

    @Order(1)
    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        List<Users> result = repository.findAllUsers();

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(arrayNames.size(), result.size(), "Количество пользователей должно совпадать");

        List<String> resultLogins = result.stream()
                .map(Users::getLogin)
                .collect(Collectors.toList());

        assertTrue(resultLogins.containsAll(arrayNames), "Все исходные логины должны присутствовать в результате");

        // Проверяем, что у всех пользователей заполнены обязательные поля
        for (Users user : result) {
            assertTrue(user.getUserId() > 0, "ID пользователя должен быть положительным");
            assertNotNull(user.getLogin(), "Логин не должен быть null");
            assertNotNull(user.getPassword(), "Пароль не должен быть null");
            assertNotNull(user.getFactoryType(), "Тип фабрики не должен быть null");
            assertNotNull(user.getRole(), "Роль не должна быть null");
        }
    }

    @Order(2)
    @Test
    void findAllUsersSortedByLogin_ShouldReturnSortedList() {
        List<String> expectedSortedLogins = new ArrayList<>(arrayNames);
        expectedSortedLogins.sort(Comparator.naturalOrder());

        List<Users> result = repository.findAllUsersSortedByLogin();

        assertEquals(expectedSortedLogins.size(), result.size(), "Количество пользователей должно совпадать");

        for (int i = 0; i < expectedSortedLogins.size(); i++) {
            assertEquals(expectedSortedLogins.get(i), result.get(i).getLogin(),
                    "Пользователи должны быть отсортированы по логину в лексикографическом порядке");
        }

        // Проверяем, что список действительно отсортирован
        List<String> resultLogins = result.stream()
                .map(Users::getLogin)
                .collect(Collectors.toList());
        List<String> manuallySorted = resultLogins.stream()
                .sorted()
                .collect(Collectors.toList());
        assertEquals(manuallySorted, resultLogins, "Список должен быть отсортирован по логину");
    }

    @Order(4)
    @Test
    void selectIdByLogin_WithNonExistentLogin_ShouldThrowException() {
        String nonExistentLogin = "nonexistentuser123";

        assertFalse(repository.existsUserByLogin(nonExistentLogin),
                "Пользователь не должен существовать");

        assertThrows(DataDoesNotExistException.class, () -> repository.selectIdByLogin(nonExistentLogin),
                "Должно бросаться исключение для несуществующего логина");
    }

    @Order(5)
    @Test
    void updateFactoryTypeById_ShouldUpdateSuccessfully() {
        int userId = repository.selectIdByLogin("vovapain");
        String newFactoryType = "list";

        repository.updateFactoryTypeById(newFactoryType, userId);

        // Проверяем обновление через поиск всех пользователей
        List<Users> allUsers = repository.findAllUsers();
        Users updatedUser = allUsers.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser, "Пользователь должен существовать после обновления");
        assertEquals(newFactoryType, updatedUser.getFactoryType(),
                "Тип фабрики должен быть обновлен");
    }

    @Order(6)
    @Test
    void updatePasswordById_ShouldUpdateSuccessfully() {
        int userId = repository.selectIdByLogin("vovapain");
        String newPassword = "pupupu";

        repository.updatePasswordById(newPassword, userId);

        // Проверяем, что другие поля не изменились
        List<Users> allUsers = repository.findAllUsers();
        Users updatedUser = allUsers.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser);
        assertEquals(newPassword, updatedUser.getPassword(), "Пароль должен быть обновлен");
        assertEquals("vovapain", updatedUser.getLogin(), "Логин не должен измениться");
    }

    @Order(7)
    @Test
    void updateLoginById_ShouldUpdateSuccessfully() {
        int userId = repository.selectIdByLogin("vovapain");
        String newLogin = "painvova";

        repository.updateLoginById(newLogin, userId);

        assertFalse(repository.existsUserByLogin("vovapain"), "Старый логин не должен существовать");
        assertTrue(repository.existsUserByLogin(newLogin), "Новый логин должен существовать");

        // Проверяем, что ID остался прежним
        assertEquals(userId, repository.selectIdByLogin(newLogin),
                "ID пользователя должен остаться прежним после смены логина");
    }

    @Order(8)
    @Test
    void updateRoleById_ShouldUpdateSuccessfully() {
        int userId = repository.selectIdByLogin("painvova");
        String newRole = "admin";

        repository.updateRoleById(newRole, userId);

        List<Users> allUsers = repository.findAllUsers();
        Users updatedUser = allUsers.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser);
        assertEquals(newRole, updatedUser.getRole(), "Роль должна быть обновлена");
    }

    @Order(9)
    @Test
    void existsUserById_ShouldReturnCorrectBoolean() {
        int existingUserId = repository.selectIdByLogin("painvova");
        int nonExistentUserId = 999999;

        assertTrue(repository.existsUserById(existingUserId),
                "Существующий пользователь должен возвращать true");
        assertFalse(repository.existsUserById(nonExistentUserId),
                "Несуществующий пользователь должен возвращать false");
    }

    @Order(10)
    @Test
    void existsUserByLogin_ShouldReturnCorrectBoolean() {
        assertTrue(repository.existsUserByLogin("painvova"),
                "Существующий логин должен возвращать true");
        assertFalse(repository.existsUserByLogin("nonexistentlogin"),
                "Несуществующий логин должен возвращать false");

        // Граничные случаи
        assertFalse(repository.existsUserByLogin(""), "Пустой логин должен возвращать false");
    }

    @Order(11)
    @Test
    void deleteUserById_ShouldRemoveUser() {

        List<Users> array1 = new JdbcUserRepository().findAllUsers();
        String loginToDelete = "painvova";
        int userId = repository.selectIdByLogin(loginToDelete);

        assertTrue(repository.existsUserById(userId), "Пользователь должен существовать перед удалением");

        repository.deleteUserById(userId);

        assertFalse(repository.existsUserById(userId), "Пользователь не должен существовать после удаления");
        assertFalse(repository.existsUserByLogin(loginToDelete), "Логин не должен существовать после удаления");

        // Проверяем, что другие пользователи не затронуты
        List<Users> remainingUsers = repository.findAllUsers();
        assertEquals(arrayNames.size() - 1, remainingUsers.size(),
                "Количество пользователей должно уменьшиться на 1");
    }

    @Order(12)
    @Test
    void deleteUserById_WithNonExistentId_ShouldNotAffectData() {
        int nonExistentId = 999999;

        assertFalse(repository.existsUserById(nonExistentId));

        assertDoesNotThrow(() -> repository.deleteUserById(nonExistentId));

        List<Users> allUsers = repository.findAllUsers();
        assertEquals(arrayNames.size()-1, allUsers.size(),
                "Количество пользователей не должно измениться(лишь учтём, что удалили человека" +
                        "в прошлом тесте)");
    }

    @Order(13)
    @Test
    void integrationTest_ComplexUserLifecycle() {
        // Комплексный сценарий: создание → обновление нескольких полей → удаление
        String newLogin = "newtestuser";
        String password = "testpass123";
        String factoryType = "array";
        String role = "user";

        // Создаем нового пользователя
        repository.createUser(factoryType, newLogin, password, role);
        assertTrue(repository.existsUserByLogin(newLogin));

        int userId = repository.selectIdByLogin(newLogin);

        // Обновляем несколько полей
        repository.updatePasswordById("newpassword456", userId);
        repository.updateRoleById("admin", userId);
        repository.updateFactoryTypeById("list", userId);

        // Проверяем обновления
        List<Users> allUsers = repository.findAllUsers();
        Users updatedUser = allUsers.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser);
        assertEquals("newpassword456", updatedUser.getPassword());
        assertEquals("admin", updatedUser.getRole());
        assertEquals("list", updatedUser.getFactoryType());

        // Удаляем пользователя
        repository.deleteUserById(userId);
        assertFalse(repository.existsUserByLogin(newLogin));
    }

    @Order(15)
    @Test
    void findAllUsers_WithEmptyTable_ShouldThrowException() {
        repository.deleteAllUsers();

        assertThrows(DataDoesNotExistException.class, () -> repository.findAllUsers(),
                "Пустая таблица должна бросать DataDoesNotExistException");

        assertThrows(DataDoesNotExistException.class, () -> repository.findAllUsersSortedByLogin(),
                "Пустая таблица должна бросать DataDoesNotExistException при сортированном запросе");
    }

    @Order(14)
    @Test
    void createUser_WithDuplicateLogin_ShouldHandleAppropriately() {
        String duplicateLogin = "kartavaya";

        assertTrue(repository.existsUserByLogin(duplicateLogin));

        // Попытка создать пользователя с существующим логином
        // Поведение зависит от реализации - может бросить исключение или проигнорировать
       assertThrows(RuntimeException.class, () ->
                repository.createUser("array", duplicateLogin, "newpass", "user"));

        // Проверяем, что пользователь все еще существует
        assertTrue(repository.existsUserByLogin(duplicateLogin));
    }
}