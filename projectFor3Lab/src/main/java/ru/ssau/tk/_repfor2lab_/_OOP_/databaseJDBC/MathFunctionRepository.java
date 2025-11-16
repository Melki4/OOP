package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctionDTO;
import java.util.List;

public interface MathFunctionRepository {
    void createTable();
    List<String> selectAllMathFunctions();
    List<String> selectMathFunctionsByUserId(int id);
    String selectMathFunctionsByName(String name);
    List<MathFunctionDTO> selectAllMathFunctionsAsDTO();
    List<MathFunctionDTO> selectMathFunctionsByUserIdAsDTO(int id);
    MathFunctionDTO selectMathFunctionsByNameAsDTO(String name);
    void updateFunctionNameByFunctionId(String name, int id);
    void deleteMathFunctionByFunctionId(int id);
    void deleteAllFunctions();
    void addMathFunction(String function_name, int amount_of_dots, double left_boarder,
                                double right_boarder, int owner_id, String function_type);
    boolean existsFunction(String name);
}
