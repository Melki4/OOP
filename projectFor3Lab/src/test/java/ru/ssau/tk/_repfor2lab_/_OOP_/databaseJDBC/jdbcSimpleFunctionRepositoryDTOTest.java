package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunctionDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcSimpleFunctionRepositoryDTOTest {

    @AfterEach
    void tearDown() {
        // Очищаем тестовые данные
        var s = new JdbcSimpleFunctionRepository();
        s.deleteAllFunctions();
    }

    @Test
    void selectAllSimpleFunctionsAsDTO(){
        JdbcSimpleFunctionRepository JdbcSimpleFunctionRepository = new JdbcSimpleFunctionRepository();
        JdbcSimpleFunctionRepository.createTable();

        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_LOG", "Логарифм");
        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_EXP", "Экспонента");
        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_SQRT", "Квадратный корень");

        List<String> allFunctions = JdbcSimpleFunctionRepository.selectAllSimpleFunctions();
        List<SimpleFunctionDTO> simpleFunctionDTOS = JdbcSimpleFunctionRepository.selectAllSimpleFunctionsAsDTO();

        for (int i=0;i<allFunctions.size();++i){
            String[] boof = allFunctions.get(i).split(" ");
            assertEquals(Integer.parseInt(boof[0]), simpleFunctionDTOS.get(i).getFunctionId());
            assertEquals(boof[1], simpleFunctionDTOS.get(i).getFunctionCode());
            if (boof.length < 4){
                assertEquals(boof[2], simpleFunctionDTOS.get(i).getLocalName());
            }
            else {
                assertEquals(boof[2] + " " + boof[3] , simpleFunctionDTOS.get(i).getLocalName());
            }
        }
    }

    @Test
    void selectSimpleFunctionByFunctionCodeAsDTO(){
        JdbcSimpleFunctionRepository JdbcSimpleFunctionRepository = new JdbcSimpleFunctionRepository();
        JdbcSimpleFunctionRepository.createTable();

        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_LOG", "Логарифм");
        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_EXP", "Экспонента");
        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_SQRT", "Квадратный_корень");
        JdbcSimpleFunctionRepository.addSimpleFunction("TESTSQRT", "Квадратный корень");

        String simpleFunction = JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_EXP");
        SimpleFunctionDTO simpleFunctionDTO = JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCodeAsDTO("TEST_EXP");

        String[] boof = simpleFunction.split(" ");
        assertEquals(Integer.parseInt(boof[0]), simpleFunctionDTO.getFunctionId());
        assertEquals(boof[1], simpleFunctionDTO.getFunctionCode());
        assertEquals(boof[2], simpleFunctionDTO.getLocalName());

        SimpleFunctionDTO simpleFunctionDTO1 = JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCodeAsDTO("TESTSQRT");

        assertEquals(JdbcSimpleFunctionRepository.selectSimpleFunctionNameByFunctionCode("TESTSQRT"), simpleFunctionDTO1.getLocalName());
    }
}