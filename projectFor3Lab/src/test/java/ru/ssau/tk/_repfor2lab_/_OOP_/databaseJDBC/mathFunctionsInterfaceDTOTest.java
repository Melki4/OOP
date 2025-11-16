package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctionDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class mathFunctionsInterfaceDTOTest {
    private mathFunctionsInterface mathFunctionsInterface;

    @BeforeEach
    void setUp() {
        mathFunctionsInterface = new mathFunctionsInterface();
        mathFunctionsInterface.createTable();
    }

    @AfterEach
    void tearDown() {
        var f = new mathFunctionsInterface();
        f.deleteAllFunctions();
        simpleFunctionInterface s = new simpleFunctionInterface();
        s.deleteAllFunctions();
        userInterface u = new userInterface();
        u.deleteAllUsers();
    }

    @Test
    void someTest(){
        // CREATE - Добавляем несколько функций
        mathFunctionsInterface.createTable();

        //INSERT
        simpleFunctionInterface s = new simpleFunctionInterface();
        s.addSimpleFunction("SqrFunc", "Квадратичная функция");

        userInterface u = new userInterface();
        u.addUser("array", "login", "hardpassword", "user");
        u.addUser("array", "login1", "hardpassword", "user");
        int id = u.selectIdByLogin("login");
        int id1 = u.selectIdByLogin("login1");

        mathFunctionsInterface.addMathFunction("x^2-1", 100, -42.2,
                42.2, id, "SqrFunc");
        mathFunctionsInterface.addMathFunction("x^3-1", 100, -42.2,
                42.2, id1, "SqrFunc");
        mathFunctionsInterface.addMathFunction("x^4-1", 100, -42.2,
                42.2, id1, "SqrFunc");

        // READ - Получаем все функции
        List<String> allFunctions = mathFunctionsInterface.selectAllMathFunctions();
        assertFalse(allFunctions.isEmpty());

        List<String> sinLocalName = mathFunctionsInterface.selectMathFunctionsByUserId(id1);
        assertNotNull(sinLocalName);

        List<MathFunctionDTO> mathFunctionDTOS = mathFunctionsInterface.selectAllMathFunctionsAsDTO();
        List<MathFunctionDTO> mathFunctionDTOS1 = mathFunctionsInterface.selectMathFunctionsByUserIdAsDTO(id1);

        for (int i = 0; i<allFunctions.size();++i){
            String[] boof = allFunctions.get(i).split(" ");
            assertEquals(Long.parseLong(boof[0]), mathFunctionDTOS.get(i).getFunctionId());
            assertEquals(boof[1], mathFunctionDTOS.get(i).getFunctionName());
            assertEquals(Integer.parseInt(boof[2]), mathFunctionDTOS.get(i).getAmountOfDots());
            assertEquals(Double.parseDouble(boof[3]), mathFunctionDTOS.get(i).getLeftBorder());
            assertEquals(Double.parseDouble(boof[4]), mathFunctionDTOS.get(i).getRightBorder());
            assertEquals(Integer.parseInt(boof[5]), mathFunctionDTOS.get(i).getOwnerId());
            assertEquals(boof[6], mathFunctionDTOS.get(i).getFunctionType());
        }

        for (int i = 0; i<sinLocalName.size();++i){
            String[] boof = sinLocalName.get(i).split(" ");
            assertEquals(Long.parseLong(boof[0]), mathFunctionDTOS1.get(i).getFunctionId());
            assertEquals(boof[1], mathFunctionDTOS1.get(i).getFunctionName());
            assertEquals(Integer.parseInt(boof[2]), mathFunctionDTOS1.get(i).getAmountOfDots());
            assertEquals(Double.parseDouble(boof[3]), mathFunctionDTOS1.get(i).getLeftBorder());
            assertEquals(Double.parseDouble(boof[4]), mathFunctionDTOS1.get(i).getRightBorder());
            assertEquals(Integer.parseInt(boof[5]), mathFunctionDTOS1.get(i).getOwnerId());
            assertEquals(boof[6], mathFunctionDTOS1.get(i).getFunctionType());
        }
    }
}