package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.Users;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcUserRepositoryTest {

    static List<Users> array = new ArrayList<>();
    static List<String> arrayNames = new ArrayList<>();

    @BeforeAll
    static void arrayCreation(){
        arrayNames.add("vovapain");
        arrayNames.add("yojo");
        arrayNames.add("kartavaya");
        arrayNames.add("owlneverdie");
        arrayNames.add("ni4r");
        arrayNames.add("wesslo");

        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.createUser("array", arrayNames.get(0), "vivatVOVA", "user");
        userRepository.createUser("list", arrayNames.get(1), "vivatyojo", "admin");
        userRepository.createUser("array", arrayNames.get(2), "vivatVOVA", "user");
        userRepository.createUser("array", arrayNames.get(3), "vivatVOVA", "user");
        userRepository.createUser("array", arrayNames.get(4), "vivatni4r", "admin");
        userRepository.createUser("list", arrayNames.get(5), "vivatwesslo", "admin");

        array = userRepository.findAllUsers();
    }

    @AfterAll
    static void cleaning(){
        JdbcUserRepository d = new JdbcUserRepository();
        d.deleteAllUsers();
    }

    @Order(1)
    @Test
    void findAllUsersSortedByLogin() {
        arrayNames.sort(Comparator.naturalOrder());

        JdbcUserRepository userRepository = new JdbcUserRepository();
        List<Users> list = userRepository.findAllUsersSortedByLogin();

        for (int i =0; i<list.size(); ++i){
            assertEquals(arrayNames.get(i), list.get(i).getLogin());
        }
    }

    @Order(2)
    @Test
    void selectIdByLogin() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        assertEquals(array.get(2).getUserId(), userRepository.selectIdByLogin("kartavaya"));
    }

    @Order(3)
    @Test
    void updateFactoryTypeById() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.updateFactoryTypeById("list", userRepository.selectIdByLogin("vovapain"));
    }

    @Order(4)
    @Test
    void updatePasswordById() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.updatePasswordById("pupupu", userRepository.selectIdByLogin("vovapain"));
    }

    @Order(5)
    @Test
    void updateLoginById() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.updateLoginById("painvova", userRepository.selectIdByLogin("vovapain"));
    }

    @Order(6)
    @Test
    void updateRoleById() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.updateRoleById("admin", userRepository.selectIdByLogin("painvova"));
    }

    @Order(7)
    @Test
    void existsUserById() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.existsUserById(userRepository.selectIdByLogin("painvova"));
    }

    @Order(8)
    @Test
    void existsUserByLogin() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.existsUserByLogin("painvova");
    }

    @Order(9)
    @Test
    void deleteUserById() {
        JdbcUserRepository userRepository = new JdbcUserRepository();

        List<Users> list = userRepository.findAllUsersSortedByLogin();
        Users pain = list.get(3);
        assertEquals("painvova", pain.getLogin());
        assertEquals("pupupu", pain.getPassword());
        assertEquals("list", pain.getFactoryType());
        assertEquals("admin", pain.getRole());

        userRepository.deleteUserById(userRepository.selectIdByLogin("painvova"));
    }
}