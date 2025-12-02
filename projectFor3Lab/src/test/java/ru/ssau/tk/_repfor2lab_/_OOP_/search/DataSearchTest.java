package ru.ssau.tk._repfor2lab_._OOP_.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Points;
import ru.ssau.tk._repfor2lab_._OOP_.entities.SimpleFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSearchTest {

    @Mock
    private UsersRepositories usersRepository;

    @Mock
    private MathFunctionsRepositories mathFunctionsRepository;

    @Mock
    private PointsRepositories pointsRepository;

    @Mock
    private SimpleFunctionsRepositories simpleFunctionsRepository;

    @InjectMocks
    private DataSearch dataSearch;

    private Users testUser;
    private MathFunctions testFunction;
    private Points testPoint;
    private SimpleFunctions testSimpleFunction;

    @BeforeEach
    void setUp() {
        // Создаем тестовые объекты
        testUser = new Users();
        testUser.setUserID(1L);
        testUser.setLogin("test_user");
        testUser.setPassword("password");
        testUser.setRole("USER");
        testUser.setFactoryType("factory1");

        testFunction = new MathFunctions();
        testFunction.setMathFunctionsID(1L);
        testFunction.setNameOfFunction("test_function");
        testFunction.setAmountOfDots(100L);
        testFunction.setLeftBoarder(0.0);
        testFunction.setRightBoarder(10.0);
        testFunction.setUsers(testUser);

        testPoint = new Points();
        testPoint.setPointID(1L);
        testPoint.setxValue(5.0);
        testPoint.setyValue(10.0);
        testPoint.setMathFunctions(testFunction);

        testSimpleFunction = new SimpleFunctions();
        testSimpleFunction.setLocalName("linear");
    }

    @Test
    void testFindSingleUser_UserExists() {
        when(usersRepository.findByLogin("test_user")).thenReturn(Optional.of(testUser));

        Optional<Users> result = dataSearch.findSingleUser("test_user");

        assertTrue(result.isPresent());
        assertEquals("test_user", result.get().getLogin());
        verify(usersRepository, times(1)).findByLogin("test_user");
    }

    @Test
    void testFindSingleUser_UserNotExists() {
        when(usersRepository.findByLogin("unknown_user")).thenReturn(Optional.empty());

        Optional<Users> result = dataSearch.findSingleUser("unknown_user");

        assertFalse(result.isPresent());
        verify(usersRepository, times(1)).findByLogin("unknown_user");
    }

    @Test
    void testFindSingleMathFunctionByName_FunctionExists() {
        when(mathFunctionsRepository.findByNameOfFunction("test_function"))
                .thenReturn(Optional.of(testFunction));

        Optional<MathFunctions> result = dataSearch.findSingleMathFunctionByName("test_function");

        assertTrue(result.isPresent());
        assertEquals("test_function", result.get().getNameOfFunction());
        verify(mathFunctionsRepository, times(1)).findByNameOfFunction("test_function");
    }

    @Test
    void testFindSingleMathFunctionByName_FunctionNotExists() {
        when(mathFunctionsRepository.findByNameOfFunction("unknown_function"))
                .thenReturn(Optional.empty());

        Optional<MathFunctions> result = dataSearch.findSingleMathFunctionByName("unknown_function");

        assertFalse(result.isPresent());
        verify(mathFunctionsRepository, times(1)).findByNameOfFunction("unknown_function");
    }

    @Test
    void testFindMultipleMathFunctionsByUser_WithFunctions() {
        List<MathFunctions> functions = Arrays.asList(testFunction, testFunction);
        when(mathFunctionsRepository.findByUsersUserID(1L)).thenReturn(functions);

        List<MathFunctions> result = dataSearch.findMultipleMathFunctionsByUser(1L);

        assertEquals(2, result.size());
        verify(mathFunctionsRepository, times(1)).findByUsersUserID(1L);
    }

    @Test
    void testFindMultipleMathFunctionsByUser_NoFunctions() {
        when(mathFunctionsRepository.findByUsersUserID(999L)).thenReturn(Arrays.asList());

        List<MathFunctions> result = dataSearch.findMultipleMathFunctionsByUser(999L);

        assertTrue(result.isEmpty());
        verify(mathFunctionsRepository, times(1)).findByUsersUserID(999L);
    }

    @Test
    void testFindMultiplePointsByFunction_WithPoints() {
        List<Points> points = Arrays.asList(testPoint, testPoint, testPoint);
        when(pointsRepository.findByMathFunctionsMathFunctionsID(1L)).thenReturn(points);

        List<Points> result = dataSearch.findMultiplePointsByFunction(1L);

        assertEquals(3, result.size());
        verify(pointsRepository, times(1)).findByMathFunctionsMathFunctionsID(1L);
    }

    @Test
    void testFindMultiplePointsByFunction_NoPoints() {
        when(pointsRepository.findByMathFunctionsMathFunctionsID(999L)).thenReturn(Arrays.asList());

        List<Points> result = dataSearch.findMultiplePointsByFunction(999L);

        assertTrue(result.isEmpty());
        verify(pointsRepository, times(1)).findByMathFunctionsMathFunctionsID(999L);
    }

    @Test
    void testSearchWithSorting_UserAllAscending() {
        List<Users> users = Arrays.asList(testUser, testUser);
        when(usersRepository.findAll(any(Sort.class))).thenReturn(users);

        List<Object> result = dataSearch.searchWithSorting(
                "USER", "ALL", null, "login", true);

        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof Users);
        verify(usersRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testSearchWithSorting_UserLoginContaining() {
        List<Users> users = Arrays.asList(testUser);
        when(usersRepository.findByLoginContainingIgnoreCase(eq("test"), any(Sort.class)))
                .thenReturn(users);

        List<Object> result = dataSearch.searchWithSorting(
                "USER", "LOGIN", "test", "login", true);

        assertEquals(1, result.size());
        verify(usersRepository, times(1)).findByLoginContainingIgnoreCase(eq("test"), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_UserAllDescending() {
        List<Users> users = Arrays.asList(testUser, testUser, testUser);
        when(usersRepository.findAll(any(Sort.class))).thenReturn(users);

        List<Object> result = dataSearch.searchWithSorting(
                "USER", "ALL", null, "login", false);

        assertEquals(3, result.size());
        verify(usersRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testSearchWithSorting_MathFunctionAll() {
        List<MathFunctions> functions = Arrays.asList(testFunction);
        when(mathFunctionsRepository.findAll(any(Sort.class))).thenReturn(functions);

        List<Object> result = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "ALL", null, "nameOfFunction", true);

        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof MathFunctions);
        verify(mathFunctionsRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testSearchWithSorting_MathFunctionByName() {
        List<MathFunctions> functions = Arrays.asList(testFunction);
        when(mathFunctionsRepository.findByNameOfFunctionContainingIgnoreCase(
                eq("test"), any(Sort.class))).thenReturn(functions);

        List<Object> result = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "NAME", "test", "nameOfFunction", true);

        assertEquals(1, result.size());
        verify(mathFunctionsRepository, times(1))
                .findByNameOfFunctionContainingIgnoreCase(eq("test"), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_MathFunctionByUserId() {
        List<MathFunctions> functions = Arrays.asList(testFunction, testFunction);
        when(mathFunctionsRepository.findByUsersUserID(eq(1L), any(Sort.class)))
                .thenReturn(functions);

        List<Object> result = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "USER_ID", "1", "nameOfFunction", false);

        assertEquals(2, result.size());
        verify(mathFunctionsRepository, times(1))
                .findByUsersUserID(eq(1L), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_MathFunctionByLeftBoarderRange() {
        List<MathFunctions> functions = Arrays.asList(testFunction);
        when(mathFunctionsRepository.findByLeftBoarderBetween(
                eq(0.0), eq(5.0), any(Sort.class))).thenReturn(functions);

        List<Object> result = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "LEFT_BOARDER_RANGE", "0-5", "leftBoarder", true);

        assertEquals(1, result.size());
        verify(mathFunctionsRepository, times(1))
                .findByLeftBoarderBetween(eq(0.0), eq(5.0), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_MathFunctionByRightBoarderRange() {
        List<MathFunctions> functions = Arrays.asList(testFunction);
        when(mathFunctionsRepository.findByRightBoarderBetween(
                eq(5.0), eq(15.0), any(Sort.class))).thenReturn(functions);

        List<Object> result = dataSearch.searchWithSorting(
                "MATH_FUNCTION", "RIGHT_BOARDER_RANGE", "5-15", "rightBoarder", true);

        assertEquals(1, result.size());
        verify(mathFunctionsRepository, times(1))
                .findByRightBoarderBetween(eq(5.0), eq(15.0), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_PointAll() {
        List<Points> points = Arrays.asList(testPoint, testPoint);
        when(pointsRepository.findAll(any(Sort.class))).thenReturn(points);

        List<Object> result = dataSearch.searchWithSorting(
                "POINT", "ALL", null, "xValue", true);

        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof Points);
        verify(pointsRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testSearchWithSorting_PointByFunctionId() {
        List<Points> points = Arrays.asList(testPoint);
        when(pointsRepository.findByMathFunctionsMathFunctionsID(eq(1L), any(Sort.class)))
                .thenReturn(points);

        List<Object> result = dataSearch.searchWithSorting(
                "POINT", "FUNCTION_ID", "1", "xValue", true);

        assertEquals(1, result.size());
        verify(pointsRepository, times(1))
                .findByMathFunctionsMathFunctionsID(eq(1L), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_PointByXRange() {
        List<Points> points = Arrays.asList(testPoint);
        when(pointsRepository.findByxValueBetween(eq(0.0), eq(10.0), any(Sort.class)))
                .thenReturn(points);

        List<Object> result = dataSearch.searchWithSorting(
                "POINT", "X_RANGE", "0-10", "xValue", true);

        assertEquals(1, result.size());
        verify(pointsRepository, times(1))
                .findByxValueBetween(eq(0.0), eq(10.0), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_PointByYRange() {
        List<Points> points = Arrays.asList(testPoint);
        when(pointsRepository.findByyValueBetween(eq(5.0), eq(15.0), any(Sort.class)))
                .thenReturn(points);

        List<Object> result = dataSearch.searchWithSorting(
                "POINT", "Y_RANGE", "5-15", "yValue", true);

        assertEquals(1, result.size());
        verify(pointsRepository, times(1))
                .findByyValueBetween(eq(5.0), eq(15.0), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_SimpleFunctionAll() {
        List<SimpleFunctions> functions = Arrays.asList(testSimpleFunction);
        when(simpleFunctionsRepository.findAll(any(Sort.class))).thenReturn(functions);

        List<Object> result = dataSearch.searchWithSorting(
                "SIMPLE_FUNCTION", "ALL", null, "localName", true);

        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof SimpleFunctions);
        verify(simpleFunctionsRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testSearchWithSorting_SimpleFunctionByName() {
        List<SimpleFunctions> functions = Arrays.asList(testSimpleFunction);
        when(simpleFunctionsRepository.findByLocalNameContainingIgnoreCase(
                eq("lin"), any(Sort.class))).thenReturn(functions);

        List<Object> result = dataSearch.searchWithSorting(
                "SIMPLE_FUNCTION", "NAME", "lin", "localName", true);

        assertEquals(1, result.size());
        verify(simpleFunctionsRepository, times(1))
                .findByLocalNameContainingIgnoreCase(eq("lin"), any(Sort.class));
    }

    @Test
    void testSearchWithSorting_InvalidEntity() {
        List<Object> result = dataSearch.searchWithSorting(
                "INVALID_ENTITY", "ALL", null, "id", true);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchWithSorting_InvalidFieldForUser() {
        List<Users> users = Arrays.asList(testUser);
        when(usersRepository.findAll(any(Sort.class))).thenReturn(users);

        List<Object> result = dataSearch.searchWithSorting(
                "USER", "INVALID_FIELD", "value", "login", true);

        assertEquals(1, result.size());
        verify(usersRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testSearchWithSorting_ExceptionInRepository() {
        when(usersRepository.findAll(any(Sort.class)))
                .thenThrow(new RuntimeException("Database error"));

        List<Object> result = dataSearch.searchWithSorting(
                "USER", "ALL", null, "login", true);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchWithSorting_CaseInsensitiveEntityName() {
        List<Users> users = Arrays.asList(testUser);
        when(usersRepository.findAll(any(Sort.class))).thenReturn(users);

        List<Object> result1 = dataSearch.searchWithSorting("user", "ALL", null, "login", true);
        List<Object> result2 = dataSearch.searchWithSorting("User", "ALL", null, "login", true);
        List<Object> result3 = dataSearch.searchWithSorting("USER", "ALL", null, "login", true);

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals(1, result3.size());
        verify(usersRepository, times(3)).findAll(any(Sort.class));
    }


}