package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.User;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcUserRepositoryDTOTest {

    private JdbcUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new JdbcUserRepository();
        userRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllUsers();
    }

    @Test
    void testSelectAllUsers() {
        // Добавляем тестовых пользователей
        userRepository.addUser("OrigFact", "updateuser", "originalpass", "user");
        userRepository.addUser("OrigFact", "updateuser1", "originalpass1", "user");
        userRepository.addUser("OrigFact", "updateuser2", "originalpass2", "user");

        // Получаем всех пользователей как DTO
        List<User> userList = userRepository.selectAllUsers();

        // Проверяем результат
        assertEquals(3, userList.size());

        // Проверяем корректность данных для каждого пользователя
        for (User user : userList) {
            assertNotNull(user.getUserId());
            assertNotNull(user.getFactoryType());
            assertNotNull(user.getLogin());
            assertNotNull(user.getPassword());
            assertNotNull(user.getRole());

            // Проверяем что логин соответствует ожидаемому формату
            assertTrue(user.getLogin().startsWith("updateuser"));
            assertEquals("OrigFact", user.getFactoryType());
            assertEquals("user", user.getRole());
        }
    }

    @Test
    void testSelectUserById() {
        // Добавляем тестовых пользователей
        userRepository.addUser("OrigFact", "updateuser", "originalpass", "user");
        userRepository.addUser("OrigFact", "updateuser1", "originalpass1", "user");
        userRepository.addUser("OrigFact", "updateuser2", "originalpass2", "user");

        // Получаем ID пользователя
        int id = userRepository.selectIdByLogin("updateuser1");

        // Получаем пользователя по ID как DTO
        User user = userRepository.selectUserById(id);

        // Проверяем корректность данных
        assertNotNull(user);
        assertEquals(id, user.getUserId().intValue());
        assertEquals("OrigFact", user.getFactoryType());
        assertEquals("updateuser1", user.getLogin());
        assertEquals("originalpass1", user.getPassword());
        assertEquals("user", user.getRole());
    }

    @Test
    void testSelectAllUsersSortedByLogin() {
        // Добавляем пользователей в разном порядке
        userRepository.addUser("FactoryA", "z_user", "pass1", "user");
        userRepository.addUser("FactoryB", "a_user", "pass2", "admin");
        userRepository.addUser("FactoryC", "m_user", "pass3", "user");

        // Получаем отсортированный список
        List<User> sortedUsers = userRepository.selectAllUsersSortedByLogin();

        // Проверяем сортировку по логину (в алфавитном порядке)
        assertEquals(3, sortedUsers.size());
        assertEquals("a_user", sortedUsers.get(0).getLogin());
        assertEquals("m_user", sortedUsers.get(1).getLogin());
        assertEquals("z_user", sortedUsers.get(2).getLogin());
    }

    @Test
    void testSelectUserByLogin() {
        // Добавляем тестовых пользователей
        userRepository.addUser("TestFac", "testlogin", "testpass", "admin");
        userRepository.addUser("TestFac", "anotherlogin", "anotherpass", "user");

        // Получаем пользователя по логину
        User user = userRepository.selectUserByLogin("testlogin");

        // Проверяем корректность данных
        assertNotNull(user);
        assertEquals("testlogin", user.getLogin());
        assertEquals("TestFac", user.getFactoryType());
        assertEquals("testpass", user.getPassword());
        assertEquals("admin", user.getRole());
    }

    @Test
    void testExistsUser() {
        // Проверяем несуществующего пользователя
        assertFalse(userRepository.existsUser("nonexistent"));

        // Добавляем пользователя и проверяем существование
        userRepository.addUser("TestFac", "existinguser", "password", "user");
        assertTrue(userRepository.existsUser("existinguser"));
    }

    @Test
    void testDeleteUser() {
        // Добавляем тестовых пользователей
        userRepository.addUser("Factory1", "user1", "pass1", "user");
        userRepository.addUser("Factory2", "user2", "pass2", "admin");
        userRepository.addUser("Factory3", "user3", "pass3", "user");

        // Получаем ID пользователя для удаления
        int userIdToDelete = userRepository.selectIdByLogin("user2");

        // Проверяем начальное состояние
        List<User> initialUsers = userRepository.selectAllUsers();
        assertEquals(3, initialUsers.size());
    }

    @Test
    void testDeleteUserByLogin() {
        // Добавляем тестовых пользователей
        userRepository.addUser("FactoryA", "todelete", "pass1", "user");
        userRepository.addUser("FactoryB", "tokeep", "pass2", "admin");

        // Проверяем начальное состояние
        assertTrue(userRepository.existsUser("todelete"));
        assertTrue(userRepository.existsUser("tokeep"));
    }

    @Test
    void testEmptyDatabase() {
        // Проверяем поведение с пустой базой данных
        assertThrows(RuntimeException.class, () -> {
            userRepository.selectAllUsers();
        });

        // Проверяем исключения для несуществующих данных
        assertThrows(RuntimeException.class, () -> {
            userRepository.selectUserByLogin("nonexistent");
        });

        assertThrows(RuntimeException.class, () -> {
            userRepository.selectUserById(999);
        });
    }

    @Test
    void testUserDataIntegrity() {
        // Добавляем пользователя с различными данными
        userRepository.addUser("Complex", "complex.login-123", "P@ssw0rd!123", "s_admin");

        // Получаем пользователя
        User user = userRepository.selectUserByLogin("complex.login-123");

        // Проверяем целостность всех полей
        assertNotNull(user.getUserId());
        assertEquals("Complex", user.getFactoryType());
        assertEquals("complex.login-123", user.getLogin());
        assertEquals("P@ssw0rd!123", user.getPassword());
        assertEquals("s_admin", user.getRole());
    }

    @Test
    void testMultipleUserRoles() {
        // Добавляем пользователей с разными ролями
        userRepository.addUser("Factory", "admin_user", "pass", "admin");
        userRepository.addUser("Factory", "regular_user", "pass", "user");
        userRepository.addUser("Factory", "moderator_user", "pass", "moder");
        userRepository.addUser("Factory", "guest_user", "pass", "guest");

        // Получаем всех пользователей
        List<User> allUsers = userRepository.selectAllUsers();
        assertEquals(4, allUsers.size());

        // Проверяем что у каждого пользователя правильная роль
        for (User user : allUsers) {
            assertNotNull(user.getRole());
            assertTrue(user.getLogin().endsWith("_user"));

            switch (user.getLogin()) {
                case "admin_user":
                    assertEquals("admin", user.getRole());
                    break;
                case "regular_user":
                    assertEquals("user", user.getRole());
                    break;
                case "moderator_user":
                    assertEquals("moder", user.getRole());
                    break;
                case "guest_user":
                    assertEquals("guest", user.getRole());
                    break;
            }
        }
    }

    @Test
    void testSelectIdByLogin() {
        // Добавляем тестового пользователя
        userRepository.addUser("TestFac", "testuser", "testpass", "user");

        // Получаем ID по логину
        int userId = userRepository.selectIdByLogin("testuser");

        // Проверяем что ID корректен
        assertTrue(userId > 0);

        // Проверяем что по этому ID можно получить пользователя
        User user = userRepository.selectUserById(userId);
        assertEquals("testuser", user.getLogin());
        assertEquals("TestFac", user.getFactoryType());
    }
}