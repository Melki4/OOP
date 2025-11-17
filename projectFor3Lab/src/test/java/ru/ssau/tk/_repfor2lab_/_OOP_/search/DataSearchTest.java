package ru.ssau.tk._repfor2lab_._OOP_.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk._repfor2lab_._OOP_.config.TestConfig;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Points;
import ru.ssau.tk._repfor2lab_._OOP_.entities.SimpleFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestConfig.class)
@Transactional
class DataSearchTest {

    @Autowired
    private DataSearch dataSearch;

    @Autowired
    private UsersRepositories usersRepository;

    @Autowired
    private MathFunctionsRepositories mathFunctionsRepository;

    @Autowired
    private PointsRepositories pointsRepository;

    @Autowired
    private SimpleFunctionsRepositories simpleFunctionsRepository;

    private Users testUser1, testUser2;
    private MathFunctions testFunction1, testFunction2, testFunction3;
    private SimpleFunctions testSimpleFunction1, testSimpleFunction2, testSimpleFunction3;

    @BeforeEach
    void setUp() {
        pointsRepository.deleteAll();
        mathFunctionsRepository.deleteAll();
        simpleFunctionsRepository.deleteAll();
        usersRepository.deleteAll();

        testSimpleFunction1 = new SimpleFunctions();
        testSimpleFunction1.setFunctionCode("SIN");
        testSimpleFunction1.setLocalName("Синус");
        simpleFunctionsRepository.save(testSimpleFunction1);

        testSimpleFunction2 = new SimpleFunctions();
        testSimpleFunction2.setFunctionCode("COS");
        testSimpleFunction2.setLocalName("Косинус");
        simpleFunctionsRepository.save(testSimpleFunction2);

        testSimpleFunction3 = new SimpleFunctions();
        testSimpleFunction3.setFunctionCode("QUAD");
        testSimpleFunction3.setLocalName("Квадратичная");
        simpleFunctionsRepository.save(testSimpleFunction3);

        testUser1 = new Users();
        testUser1.setLogin("user1");
        testUser1.setPassword("pass1");
        testUser1.setRole("USER");
        testUser1.setFactoryType("Array");
        testUser1 = usersRepository.save(testUser1);

        testUser2 = new Users();
        testUser2.setLogin("user2");
        testUser2.setPassword("pass2");
        testUser2.setRole("ADMIN");
        testUser2.setFactoryType("LinkedList");
        testUser2 = usersRepository.save(testUser2);

        testFunction1 = new MathFunctions();
        testFunction1.setNameOfFunction("sin(x)");
        testFunction1.setAmountOfDots(100L);
        testFunction1.setLeftBoarder(0.0);
        testFunction1.setRightBoarder(10.0);
        testFunction1.setUsers(testUser1);
        testFunction1.setSimpleFunctions(testSimpleFunction1); // Уникальная связь
        testFunction1 = mathFunctionsRepository.save(testFunction1);

        testFunction2 = new MathFunctions();
        testFunction2.setNameOfFunction("cos(x)");
        testFunction2.setAmountOfDots(50L);
        testFunction2.setLeftBoarder(-5.0);
        testFunction2.setRightBoarder(5.0);
        testFunction2.setUsers(testUser1);
        testFunction2.setSimpleFunctions(testSimpleFunction2); // Уникальная связь
        testFunction2 = mathFunctionsRepository.save(testFunction2);

        testFunction3 = new MathFunctions();
        testFunction3.setNameOfFunction("x^2");
        testFunction3.setAmountOfDots(200L);
        testFunction3.setLeftBoarder(-10.0);
        testFunction3.setRightBoarder(10.0);
        testFunction3.setUsers(testUser2);
        testFunction3.setSimpleFunctions(testSimpleFunction3); // Уникальная связь
        testFunction3 = mathFunctionsRepository.save(testFunction3);

        Points point1 = new Points();
        point1.setxValue(1.0);
        point1.setyValue(0.84);
        point1.setMathFunctions(testFunction1);
        pointsRepository.save(point1);

        Points point2 = new Points();
        point2.setxValue(2.0);
        point2.setyValue(0.91);
        point2.setMathFunctions(testFunction1);
        pointsRepository.save(point2);

        Points point3 = new Points();
        point3.setxValue(0.5);
        point3.setyValue(0.88);
        point3.setMathFunctions(testFunction2);
        pointsRepository.save(point3);
    }

    @Test
    void testDataSearchBeanExists() {
        assertNotNull(dataSearch, "DataSearch bean должен быть создан");
    }

    @Test
    void testSearchUsersWithSortingByNameAscending() {
        List<Object> results = dataSearch.searchWithSorting(
                "USER", "ALL", null, "login", true);

        assertEquals(2, results.size(), "Должно быть 2 пользователя");

        Users firstUser = (Users) results.get(0);
        Users secondUser = (Users) results.get(1);

        assertEquals("user1", firstUser.getLogin());
        assertEquals("user2", secondUser.getLogin());
        assertTrue(firstUser.getLogin().compareTo(secondUser.getLogin()) < 0);
    }

    @Test
    void testSearchUsersWithSortingByNameDescending() {
        List<Object> results = dataSearch.searchWithSorting(
                "USER", "ALL", null, "login", false);

        assertEquals(2, results.size());

        Users firstUser = (Users) results.get(0);
        Users secondUser = (Users) results.get(1);

        assertEquals("user2", firstUser.getLogin());
        assertEquals("user1", secondUser.getLogin());
        assertTrue(firstUser.getLogin().compareTo(secondUser.getLogin()) > 0);
    }

    @Test
    void testSearchUsersWithFilterAndSorting() {
        List<Object> results = dataSearch.searchWithSorting(
                "USER", "LOGIN", "user1", "login", true);

        assertEquals(1, results.size());

        Users user = (Users) results.get(0);
        assertEquals("user1", user.getLogin());
        assertEquals("USER", user.getRole());
    }

    @Test
    void testSearchMathFunctionsWithSortingByNameAscending() {
        List<Object> results = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "ALL", null, "nameOfFunction", true);

        assertEquals(3, results.size());

        MathFunctions first = (MathFunctions) results.get(0);
        MathFunctions last = (MathFunctions) results.get(2);

        assertEquals("cos(x)", first.getNameOfFunction());
        assertEquals("x^2", last.getNameOfFunction());
        assertTrue(first.getNameOfFunction().compareTo(last.getNameOfFunction()) < 0);
    }

    @Test
    void testSearchMathFunctionsWithSortingByNameDescending() {
        List<Object> results = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "ALL", null, "nameOfFunction", false);

        assertEquals(3, results.size());

        MathFunctions first = (MathFunctions) results.get(0);
        MathFunctions last = (MathFunctions) results.get(2);

        assertEquals("x^2", first.getNameOfFunction());
        assertEquals("cos(x)", last.getNameOfFunction());
        assertTrue(first.getNameOfFunction().compareTo(last.getNameOfFunction()) > 0);
    }

    @Test
    void testSearchMathFunctionsWithSortingByLeftBoarderAscending() {
        List<Object> results = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "ALL", null, "leftBoarder", true);

        assertEquals(3, results.size());

        MathFunctions first = (MathFunctions) results.get(0);
        MathFunctions last = (MathFunctions) results.get(2);

        assertEquals(-10.0, first.getLeftBoarder(), 0.001);
        assertEquals(0.0, last.getLeftBoarder(), 0.001);
        assertTrue(first.getLeftBoarder() <= last.getLeftBoarder());
    }

    @Test
    void testSearchMathFunctionsByUserWithSorting() {
        List<Object> results = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "USER_ID", testUser1.getUserID().toString(), "nameOfFunction", true);

        assertEquals(2, results.size(), "У пользователя 1 должно быть 2 функции");

        MathFunctions first = (MathFunctions) results.get(0);
        MathFunctions second = (MathFunctions) results.get(1);

        assertEquals("cos(x)", first.getNameOfFunction());
        assertEquals("sin(x)", second.getNameOfFunction());
        assertEquals(testUser1.getUserID(), first.getUsers().getUserID());
    }

    @Test
    void testSearchPointsWithSortingByXValueAscending() {
        List<Object> results = dataSearch.searchWithSorting(
                "POINT", "ALL", null, "xValue", true);

        assertEquals(3, results.size());

        Points first = (Points) results.get(0);
        Points last = (Points) results.get(2);

        assertEquals(0.5, first.getxValue(), 0.001);
        assertEquals(2.0, last.getxValue(), 0.001);
        assertTrue(first.getxValue() <= last.getxValue());
    }

    @Test
    void testSearchPointsWithSortingByYValueDescending() {
        List<Object> results = dataSearch.searchWithSorting(
                "POINT", "ALL", null, "yValue", false);

        assertEquals(3, results.size());

        Points first = (Points) results.get(0);
        Points last = (Points) results.get(2);

        assertEquals(0.91, first.getyValue(), 0.001);
        assertEquals(0.84, last.getyValue(), 0.001);
        assertTrue(first.getyValue() >= last.getyValue());
    }

    @Test
    void testSearchPointsByFunctionWithSorting() {
        List<Object> results = dataSearch.searchWithSorting(
                "POINT", "FUNCTION_ID", testFunction1.getMathFunctionsID().toString(), "xValue", true);

        assertEquals(2, results.size(), "У функции 1 должно быть 2 точки");

        Points first = (Points) results.get(0);
        Points second = (Points) results.get(1);

        assertEquals(1.0, first.getxValue(), 0.001);
        assertEquals(2.0, second.getxValue(), 0.001);
        assertEquals(testFunction1.getMathFunctionsID(), first.getMathFunctions().getMathFunctionsID());
    }

    @Test
    void testSearchSimpleFunctionsWithSortingByNameAscending() {
        List<Object> results = dataSearch.searchWithSorting(
                "SIMPLE_FUNCTION", "ALL", null, "localName", true);

        assertEquals(3, results.size());

        SimpleFunctions first = (SimpleFunctions) results.get(0);
        SimpleFunctions last = (SimpleFunctions) results.get(2);

        assertEquals("Квадратичная", first.getLocalName());
        assertEquals("Синус", last.getLocalName());
        assertTrue(first.getLocalName().compareTo(last.getLocalName()) < 0);
    }

    @Test
    void testSearchWithInvalidEntity() {
        List<Object> results = dataSearch.searchWithSorting(
                "INVALID_ENTITY", "ALL", null, "id", true);

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Для несуществующей сущности должен вернуться пустой список");
    }

    @Test
    void testSearchWithInvalidField() {
        List<Object> results = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "INVALID_FIELD", "value", "nameOfFunction", true);

        assertNotNull(results);
        assertEquals(3, results.size());
    }
}