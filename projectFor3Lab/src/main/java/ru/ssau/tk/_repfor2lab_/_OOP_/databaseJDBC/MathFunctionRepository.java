package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctions;
import java.util.List;

public interface MathFunctionRepository {
    void createTable();

    List<MathFunctions> selectAllMathFunctions();
    List<MathFunctions> selectAllMathFunctionsSortedByUserLogins();
    List<MathFunctions> selectMathFunctionsByUserId(int id);
    MathFunctions selectMathFunctionsByName(String name);

    void updateFunctionNameByFunctionId(String name, int id);

    void deleteMathFunctionByFunctionId(int id);
    void deleteAllFunctions();

    void addMathFunction(String function_name, int amount_of_dots, double left_boarder,
                                double right_boarder, int owner_id, String function_type);

    boolean existsFunction(String name);
}
