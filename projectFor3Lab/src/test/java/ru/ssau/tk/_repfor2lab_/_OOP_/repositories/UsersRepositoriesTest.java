package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk._repfor2lab_._OOP_.config.AppConfig;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
@Transactional
class UsersRepositoriesTest {

    @Autowired
    private UsersRepositories usersRepository;

    private Users testUser1;
    private Users testUser2;
    private Users testUser3;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        usersRepository.deleteAll();

        // Создание тестовых пользователей
        testUser1 = new Users();
        testUser1.setLogin("john_doe");
        testUser1.setPassword("password123");
        testUser1.setFactoryType("ARRAY");
        testUser1.setRole("USER");

        testUser2 = new Users();
        testUser2.setLogin("jane_smith");
        testUser2.setPassword("password456");
        testUser2.setFactoryType("LINKED_LIST");
        testUser2.setRole("ADMIN");

        testUser3 = new Users();
        testUser3.setLogin("bob_johnson");
        testUser3.setPassword("password789");
        testUser3.setFactoryType("ARRAY");
        testUser3.setRole("USER");

        // Сохранение тестовых данных
        usersRepository.save(testUser1);
        usersRepository.save(testUser2);
        usersRepository.save(testUser3);
    }

    @Test
    void testSaveUser() {
        Users newUser = new Users();
        newUser.setLogin("new_user");
        newUser.setPassword("new_password");
        newUser.setFactoryType("TREE");
        newUser.setRole("ADMIN");

        Users savedUser = usersRepository.save(newUser);

        assertNotNull(savedUser.getUserID());
        assertEquals("new_user", savedUser.getLogin());
        assertEquals("ADMIN", savedUser.getRole());
        assertEquals("TREE", savedUser.getFactoryType());
    }

    @Test
    void testFindById() {
        // Получаем ID сохраненного пользователя
        Long userId = testUser1.getUserID();

        Optional<Users> foundUser = usersRepository.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals("john_doe", foundUser.get().getLogin());
        assertEquals("USER", foundUser.get().getRole());
    }

    @Test
    void testFindByLogin() {
        Optional<Users> foundUser = usersRepository.findByLogin("jane_smith");

        assertTrue(foundUser.isPresent());
        assertEquals("ADMIN", foundUser.get().getRole());
        assertEquals("LINKED_LIST", foundUser.get().getFactoryType());
    }

    @Test
    void testFindByLogin_NotFound() {
        Optional<Users> foundUser = usersRepository.findByLogin("non_existent_user");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testExistsByLogin() {
        boolean exists = usersRepository.existsByLogin("john_doe");
        boolean notExists = usersRepository.existsByLogin("non_existent_user");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testDeleteByLogin() {
        // Проверяем, что пользователь существует
        assertTrue(usersRepository.existsByLogin("bob_johnson"));

        // Удаляем по логину
        usersRepository.deleteByLogin("bob_johnson");

        // Проверяем, что пользователь удален
        assertFalse(usersRepository.existsByLogin("bob_johnson"));

        // Проверяем, что другие пользователи остались
        assertTrue(usersRepository.existsByLogin("john_doe"));
        assertTrue(usersRepository.existsByLogin("jane_smith"));
    }

    @Test
    void testFindAllUsers() {
        List<Users> allUsers = usersRepository.findAll();

        assertEquals(3, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(user -> "john_doe".equals(user.getLogin())));
        assertTrue(allUsers.stream().anyMatch(user -> "jane_smith".equals(user.getLogin())));
        assertTrue(allUsers.stream().anyMatch(user -> "bob_johnson".equals(user.getLogin())));
    }

    @Test
    void testUpdateUser() {
        // Находим пользователя
        Users user = usersRepository.findByLogin("john_doe").get();

        // Обновляем данные
        user.setRole("ADMIN");
        user.setFactoryType("HASH_TABLE");
        user.setPassword("updated_password");

        Users updatedUser = usersRepository.save(user);

        // Проверяем обновленные данные
        assertEquals("ADMIN", updatedUser.getRole());
        assertEquals("HASH_TABLE", updatedUser.getFactoryType());
        assertEquals("updated_password", updatedUser.getPassword());
        assertEquals("john_doe", updatedUser.getLogin());

        // Проверяем, что данные сохранились в базе
        Users persistedUser = usersRepository.findByLogin("john_doe").get();
        assertEquals("ADMIN", persistedUser.getRole());
    }

    @Test
    void testCountUsers() {
        long count = usersRepository.count();
        assertEquals(3, count);

        // Добавляем нового пользователя и проверяем счетчик
        Users newUser = new Users();
        newUser.setLogin("alice_cooper");
        newUser.setPassword("password000");
        newUser.setFactoryType("GRAPH");
        newUser.setRole("USER");

        usersRepository.save(newUser);

        long newCount = usersRepository.count();
        assertEquals(4, newCount);
    }

    @Test
    void testDeleteById() {
        Long userId = testUser1.getUserID();

        // Проверяем, что пользователь существует
        assertTrue(usersRepository.findById(userId).isPresent());

        // Удаляем по ID
        usersRepository.deleteById(userId);

        // Проверяем, что пользователь удален
        assertFalse(usersRepository.findById(userId).isPresent());
    }

    @Test
    void testSaveAndFlush() {
        Users newUser = new Users();
        newUser.setLogin("flush_user");
        newUser.setPassword("flush_pass");
        newUser.setFactoryType("STACK");
        newUser.setRole("USER");

        Users savedUser = usersRepository.saveAndFlush(newUser);

        assertNotNull(savedUser.getUserID());
        assertEquals("flush_user", savedUser.getLogin());

        // Немедленно проверяем, что пользователь доступен
        Optional<Users> foundUser = usersRepository.findByLogin("flush_user");
        assertTrue(foundUser.isPresent());
    }

    @Test
    void testFindAllById() {
        Long id1 = testUser1.getUserID();
        Long id2 = testUser2.getUserID();

        List<Users> users = usersRepository.findAllById(List.of(id1, id2));

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.getUserID().equals(id1)));
        assertTrue(users.stream().anyMatch(user -> user.getUserID().equals(id2)));
    }
}