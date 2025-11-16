package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunctionDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class simpleFunctionInterfaceDTOTest {

    @AfterEach
    void tearDown() {
        // Очищаем тестовые данные
        var s = new simpleFunctionInterface();
        s.deleteAllFunctions();
    }

    @Test
    void selectAllSimpleFunctionsAsDTO(){
        simpleFunctionInterface simpleFunctionInterface = new simpleFunctionInterface();
        simpleFunctionInterface.createTable();

        simpleFunctionInterface.addSimpleFunction("TEST_LOG", "Логарифм");
        simpleFunctionInterface.addSimpleFunction("TEST_EXP", "Экспонента");
        simpleFunctionInterface.addSimpleFunction("TEST_SQRT", "Квадратный корень");

        List<String> allFunctions = simpleFunctionInterface.selectAllSimpleFunctions();
        List<SimpleFunctionDTO> simpleFunctionDTOS = simpleFunctionInterface.selectAllSimpleFunctionsAsDTO();

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
        simpleFunctionInterface simpleFunctionInterface = new simpleFunctionInterface();
        simpleFunctionInterface.createTable();

        simpleFunctionInterface.addSimpleFunction("TEST_LOG", "Логарифм");
        simpleFunctionInterface.addSimpleFunction("TEST_EXP", "Экспонента");
        simpleFunctionInterface.addSimpleFunction("TEST_SQRT", "Квадратный_корень");
        simpleFunctionInterface.addSimpleFunction("TESTSQRT", "Квадратный корень");

        String simpleFunction = simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_EXP");
        SimpleFunctionDTO simpleFunctionDTO = simpleFunctionInterface.selectSimpleFunctionByFunctionCodeAsDTO("TEST_EXP");

        String[] boof = simpleFunction.split(" ");
        assertEquals(Integer.parseInt(boof[0]), simpleFunctionDTO.getFunctionId());
        assertEquals(boof[1], simpleFunctionDTO.getFunctionCode());
        assertEquals(boof[2], simpleFunctionDTO.getLocalName());

        SimpleFunctionDTO simpleFunctionDTO1 = simpleFunctionInterface.selectSimpleFunctionByFunctionCodeAsDTO("TESTSQRT");

        assertEquals(simpleFunctionInterface.selectSimpleFunctionNameByFunctionCode("TESTSQRT"), simpleFunctionDTO1.getLocalName());
    }
}