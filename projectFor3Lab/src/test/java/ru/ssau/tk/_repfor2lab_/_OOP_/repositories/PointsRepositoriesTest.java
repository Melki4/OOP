package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk._repfor2lab_._OOP_.Application;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Points;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@Transactional
@Rollback
class PointsRepositoriesTest {

    @Autowired
    private PointsRepositories pointsRepository;

    @Autowired
    private MathFunctionsRepositories mathFunctionsRepository;

    @Autowired
    private UsersRepositories usersRepository;

    private Users testUser;
    private MathFunctions testFunction1;
    private MathFunctions testFunction2;
    private Points testPoint1;
    private Points testPoint2;
    private Points testPoint3;
    private Points testPoint4;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        pointsRepository.deleteAll();
        mathFunctionsRepository.deleteAll();
        usersRepository.deleteAll();

        // Создание тестового пользователя
        testUser = new Users();
        testUser.setLogin("test_user");
        testUser.setPassword("password123");
        testUser.setFactoryType("ARRAY");
        testUser.setRole("USER");
        testUser = usersRepository.save(testUser);

        // Создание тестовых математических функций
        testFunction1 = new MathFunctions();
        testFunction1.setNameOfFunction("sin(x)");
        testFunction1.setLeftBoarder(0.0);
        testFunction1.setRightBoarder(6.28);
        testFunction1.setAmountOfDots(100L);
        testFunction1.setUsers(testUser);
        testFunction1 = mathFunctionsRepository.save(testFunction1);

        testFunction2 = new MathFunctions();
        testFunction2.setNameOfFunction("cos(x)");
        testFunction2.setLeftBoarder(-3.14);
        testFunction2.setRightBoarder(3.14);
        testFunction2.setAmountOfDots(50L);
        testFunction2.setUsers(testUser);
        testFunction2 = mathFunctionsRepository.save(testFunction2);

        // Создание тестовых точек
        testPoint1 = new Points();
        testPoint1.setxValue(0.0);
        testPoint1.setyValue(0.0);
        testPoint1.setMathFunctions(testFunction1);

        testPoint2 = new Points();
        testPoint2.setxValue(1.57);
        testPoint2.setyValue(1.0);
        testPoint2.setMathFunctions(testFunction1);

        testPoint3 = new Points();
        testPoint3.setxValue(3.14);
        testPoint3.setyValue(0.0);
        testPoint3.setMathFunctions(testFunction1);

        testPoint4 = new Points();
        testPoint4.setxValue(0.0);
        testPoint4.setyValue(1.0);
        testPoint4.setMathFunctions(testFunction2);

        // Сохранение тестовых данных
        pointsRepository.save(testPoint1);
        pointsRepository.save(testPoint2);
        pointsRepository.save(testPoint3);
        pointsRepository.save(testPoint4);
    }

    @Test
    void testSavePoint() {
        Points newPoint = new Points();
        newPoint.setxValue(2.0);
        newPoint.setyValue(-1.0);
        newPoint.setMathFunctions(testFunction1);

        Points savedPoint = pointsRepository.save(newPoint);

        assertNotNull(savedPoint.getPointID());
        assertEquals(2.0, savedPoint.getxValue());
        assertEquals(-1.0, savedPoint.getyValue());
        assertEquals(testFunction1.getMathFunctionsID(), savedPoint.getMathFunctions().getMathFunctionsID());
    }

    @Test
    void testFindById() {
        Long pointId = testPoint1.getPointID();

        Optional<Points> foundPoint = pointsRepository.findById(pointId);

        assertTrue(foundPoint.isPresent());
        assertEquals(0.0, foundPoint.get().getxValue());
        assertEquals(0.0, foundPoint.get().getyValue());
        assertEquals(testFunction1.getMathFunctionsID(), foundPoint.get().getMathFunctions().getMathFunctionsID());
    }

    @Test
    void testFindByMathFunctionsMathFunctionsID() {
        Long functionId = testFunction1.getMathFunctionsID();

        List<Points> functionPoints = pointsRepository.findByMathFunctionsMathFunctionsID(functionId);

        assertEquals(3, functionPoints.size());
        assertTrue(functionPoints.stream().anyMatch(point -> point.getxValue().equals(0.0)));
        assertTrue(functionPoints.stream().anyMatch(point -> point.getxValue().equals(1.57)));
        assertTrue(functionPoints.stream().anyMatch(point -> point.getxValue().equals(3.14)));
    }

    @Test
    void testExistsByMathFunctionsMathFunctionsID() {
        Long functionId1 = testFunction1.getMathFunctionsID();
        Long functionId2 = testFunction2.getMathFunctionsID();
        Long nonExistentFunctionId = 999L;

        boolean exists1 = pointsRepository.existsByMathFunctionsMathFunctionsID(functionId1);
        boolean exists2 = pointsRepository.existsByMathFunctionsMathFunctionsID(functionId2);
        boolean notExists = pointsRepository.existsByMathFunctionsMathFunctionsID(nonExistentFunctionId);

        assertTrue(exists1);
        assertTrue(exists2);
        assertFalse(notExists);
    }

    @Test
    void testFindByxValue() {
        List<Points> points = pointsRepository.findByxValue(0.0);

        assertEquals(2, points.size()); // Две точки с x=0.0 из разных функций
        assertTrue(points.stream().allMatch(point -> point.getxValue().equals(0.0)));
    }

    @Test
    void testExistsByxValue() {
        // Используем Double.valueOf() вместо примитива double
        boolean exists = pointsRepository.existsByxValue(Double.valueOf(1.57));
        boolean notExists = pointsRepository.existsByxValue(Double.valueOf(999.0));

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByyValue() {
        List<Points> points = pointsRepository.findByyValue(1.0);

        assertEquals(2, points.size()); // Две точки с y=1.0 из разных функций
        assertTrue(points.stream().allMatch(point -> point.getyValue().equals(1.0)));
    }

    @Test
    void testExistsByyValue() {
        // Используем Double.valueOf() вместо примитива double
        boolean exists = pointsRepository.existsByyValue(Double.valueOf(0.0));
        boolean notExists = pointsRepository.existsByyValue(Double.valueOf(999.0));

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByxValueBetween() {
        List<Points> points = pointsRepository.findByxValueBetween(1.0, 2.0);

        assertEquals(1, points.size()); // Только точка с x=1.57
        assertEquals(1.57, points.get(0).getxValue());
    }

    @Test
    void testFindByyValueBetween() {
        List<Points> points = pointsRepository.findByyValueBetween(0.5, 1.5);

        assertEquals(2, points.size()); // Две точки с y=1.0
        assertTrue(points.stream().allMatch(point -> point.getyValue().equals(1.0)));
    }

    @Test
    void testDeleteByMathFunctionsMathFunctionsID() {
        Long functionId = testFunction1.getMathFunctionsID();

        // Проверяем, что точки существуют
        assertTrue(pointsRepository.existsByMathFunctionsMathFunctionsID(functionId));
        assertEquals(3, pointsRepository.findByMathFunctionsMathFunctionsID(functionId).size());

        // Удаляем все точки функции
        pointsRepository.deleteByMathFunctionsMathFunctionsID(functionId);

        // Проверяем, что точки удалены
        assertFalse(pointsRepository.existsByMathFunctionsMathFunctionsID(functionId));
        assertEquals(0, pointsRepository.findByMathFunctionsMathFunctionsID(functionId).size());

        // Проверяем, что точки другой функции остались
        assertTrue(pointsRepository.existsByMathFunctionsMathFunctionsID(testFunction2.getMathFunctionsID()));
    }

    @Test
    void testFindAllPoints() {
        List<Points> allPoints = pointsRepository.findAll();

        assertEquals(4, allPoints.size());
        assertTrue(allPoints.stream().anyMatch(point -> point.getxValue().equals(0.0) && point.getyValue().equals(0.0)));
        assertTrue(allPoints.stream().anyMatch(point -> point.getxValue().equals(1.57) && point.getyValue().equals(1.0)));
        assertTrue(allPoints.stream().anyMatch(point -> point.getxValue().equals(3.14) && point.getyValue().equals(0.0)));
        assertTrue(allPoints.stream().anyMatch(point -> point.getxValue().equals(0.0) && point.getyValue().equals(1.0)));
    }

    @Test
    void testUpdatePoint() {
        // Находим точку
        Points point = pointsRepository.findByxValue(1.57).get(0);

        // Обновляем данные
        point.setxValue(2.0);
        point.setyValue(-1.0);

        Points updatedPoint = pointsRepository.save(point);

        // Проверяем обновленные данные
        assertEquals(2.0, updatedPoint.getxValue());
        assertEquals(-1.0, updatedPoint.getyValue());

        // Проверяем, что данные сохранились в базе
        Points persistedPoint = pointsRepository.findById(point.getPointID()).get();
        assertEquals(2.0, persistedPoint.getxValue());
        assertEquals(-1.0, persistedPoint.getyValue());
    }

    @Test
    void testCountPoints() {
        long count = pointsRepository.count();
        assertEquals(4, count);

        // Добавляем новую точку и проверяем счетчик
        Points newPoint = new Points();
        newPoint.setxValue(5.0);
        newPoint.setyValue(2.0);
        newPoint.setMathFunctions(testFunction1);

        pointsRepository.save(newPoint);

        long newCount = pointsRepository.count();
        assertEquals(5, newCount);
    }

    @Test
    void testDeleteById() {
        Long pointId = testPoint1.getPointID();

        // Проверяем, что точка существует
        assertTrue(pointsRepository.findById(pointId).isPresent());

        // Удаляем по ID
        pointsRepository.deleteById(pointId);

        // Проверяем, что точка удалена
        assertFalse(pointsRepository.findById(pointId).isPresent());
    }

    @Test
    void testSaveAndFlush() {
        Points newPoint = new Points();
        newPoint.setxValue(10.0);
        newPoint.setyValue(20.0);
        newPoint.setMathFunctions(testFunction2);

        Points savedPoint = pointsRepository.saveAndFlush(newPoint);

        assertNotNull(savedPoint.getPointID());
        assertEquals(10.0, savedPoint.getxValue());

        // Немедленно проверяем, что точка доступна
        List<Points> foundPoints = pointsRepository.findByxValue(10.0);
        assertFalse(foundPoints.isEmpty());
        assertEquals(10.0, foundPoints.get(0).getxValue());
    }

    @Test
    void testFindAllById() {
        Long id1 = testPoint1.getPointID();
        Long id2 = testPoint2.getPointID();

        List<Points> points = pointsRepository.findAllById(List.of(id1, id2));

        assertEquals(2, points.size());
        assertTrue(points.stream().anyMatch(point -> point.getPointID().equals(id1)));
        assertTrue(points.stream().anyMatch(point -> point.getPointID().equals(id2)));
    }

    @Test
    void testFindByMathFunctionsMathFunctionsID_NoPoints() {
        // Создаем новую функцию без точек
        MathFunctions newFunction = new MathFunctions();
        newFunction.setNameOfFunction("new_function");
        newFunction.setLeftBoarder(0.0);
        newFunction.setRightBoarder(10.0);
        newFunction.setAmountOfDots(10L);
        newFunction.setUsers(testUser);
        newFunction = mathFunctionsRepository.save(newFunction);

        List<Points> points = pointsRepository.findByMathFunctionsMathFunctionsID(newFunction.getMathFunctionsID());

        assertTrue(points.isEmpty());
    }

    @Test
    void testComplexQueryCombinations() {
        // Тестируем комбинацию нескольких условий - находим точки с x между 0 и 2
        List<Points> points = pointsRepository.findByxValueBetween(0.0, 2.0);

        assertEquals(3, points.size()); // x=0.0 (2 точки) и x=1.57

        // Проверяем, что найденные точки имеют правильные x значения
        for (Points point : points) {
            assertTrue(point.getxValue() >= 0.0 && point.getxValue() <= 2.0);
        }
    }

    @Test
    void testMultiplePointsSameCoordinates() {
        // Добавляем еще одну точку с такими же координатами как testPoint1
        Points duplicatePoint = new Points();
        duplicatePoint.setxValue(0.0);
        duplicatePoint.setyValue(0.0);
        duplicatePoint.setMathFunctions(testFunction2); // Но для другой функции

        pointsRepository.save(duplicatePoint);

        // Проверяем, что теперь есть 3 точки с x=0.0
        List<Points> pointsWithXZero = pointsRepository.findByxValue(0.0);
        assertEquals(3, pointsWithXZero.size());

        // Проверяем, что есть 3 точки с y=0.0
        List<Points> pointsWithYZero = pointsRepository.findByyValue(0.0);
        assertEquals(3, pointsWithYZero.size());
    }
}