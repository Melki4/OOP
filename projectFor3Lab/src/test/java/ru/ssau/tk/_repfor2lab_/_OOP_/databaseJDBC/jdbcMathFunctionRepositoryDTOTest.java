package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctionDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcMathFunctionRepositoryDTOTest {
    private JdbcMathFunctionRepository JdbcMathFunctionRepository;

    @BeforeEach
    void setUp() {
        JdbcMathFunctionRepository = new JdbcMathFunctionRepository();
        JdbcMathFunctionRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        var f = new JdbcMathFunctionRepository();
        f.deleteAllFunctions();
        JdbcSimpleFunctionRepository s = new JdbcSimpleFunctionRepository();
        s.deleteAllFunctions();
        JdbcUserRepository u = new JdbcUserRepository();
        u.deleteAllUsers();
    }

    @Test
    void someTest(){
        // CREATE - Добавляем несколько функций
        JdbcMathFunctionRepository.createTable();

        //INSERT
        JdbcSimpleFunctionRepository s = new JdbcSimpleFunctionRepository();
        s.addSimpleFunction("SqrFunc", "Квадратичная функция");

        JdbcUserRepository u = new JdbcUserRepository();
        u.addUser("array", "login", "hardpassword", "user");
        u.addUser("array", "login1", "hardpassword", "user");
        int id = u.selectIdByLogin("login");
        int id1 = u.selectIdByLogin("login1");

        JdbcMathFunctionRepository.addMathFunction("x^2-1", 100, -42.2,
                42.2, id, "SqrFunc");
        JdbcMathFunctionRepository.addMathFunction("x^3-1", 100, -42.2,
                42.2, id1, "SqrFunc");
        JdbcMathFunctionRepository.addMathFunction("x^4-1", 100, -42.2,
                42.2, id1, "SqrFunc");

        // READ - Получаем все функции
        List<String> allFunctions = JdbcMathFunctionRepository.selectAllMathFunctions();
        assertFalse(allFunctions.isEmpty());

        List<String> sinLocalName = JdbcMathFunctionRepository.selectMathFunctionsByUserId(id1);
        assertNotNull(sinLocalName);

        List<MathFunctionDTO> mathFunctionDTOS = JdbcMathFunctionRepository.selectAllMathFunctionsAsDTO();
        List<MathFunctionDTO> mathFunctionDTOS1 = JdbcMathFunctionRepository.selectMathFunctionsByUserIdAsDTO(id1);

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