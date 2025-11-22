package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunctions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcSimpleFunctionRepositoryTest {

    static List<String> array = new ArrayList<>();

    @BeforeAll
    static void arrayCreation(){
        array.add("Квадратичная функция");
        array.add("Постоянная единичная");
        array.add("Сложная");
        array.add("Табулированная");

        JdbcSimpleFunctionRepository simpleFunctionRepository= new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.createTable();

        for (String s : array) {
            simpleFunctionRepository.createSimpleFunction(s);
        }
    }

    @AfterAll
    static void cleaning(){
        JdbcSimpleFunctionRepository userRepository = new JdbcSimpleFunctionRepository();
        userRepository.deleteAllFunctions();
    }

    @Order(1)
    @Test
    void findAllSimpleFunctions() {
        JdbcSimpleFunctionRepository simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        List<SimpleFunctions> list = simpleFunctionRepository.findAllSimpleFunctionsSortedByLocalName();
        for (int i =0; i< list.size(); ++i){
            assertEquals(array.get(i), list.get(i).getLocalName());
        }
    }

    @Order(2)
    @Test
    void findAllSimpleFunctionsSortedByLocalName() {
        array.sort(Comparator.naturalOrder());
        JdbcSimpleFunctionRepository simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        List<SimpleFunctions> list = simpleFunctionRepository.findAllSimpleFunctions();
        for (int i =0; i< list.size(); ++i){
            assertEquals(array.get(i), list.get(i).getLocalName());
        }
    }

    @Order(3)
    @Test
    void updateSimpleFunctionName() {
        JdbcSimpleFunctionRepository simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.updateSimpleFunctionName("Постоянная единичная","Постоянная восьмеричная");
    }

    @Order(4)
    @Test
    void deleteSimpleFunctionByName() {
        JdbcSimpleFunctionRepository simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.deleteSimpleFunctionByName("Табулированная");
        List<SimpleFunctions> list = simpleFunctionRepository.findAllSimpleFunctions();
        assertEquals(3, list.size());
    }

    @Order(5)
    @Test
    void existSimpleFunction() {
        JdbcSimpleFunctionRepository simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.existSimpleFunction("Постоянная восьмеричная");
    }
}