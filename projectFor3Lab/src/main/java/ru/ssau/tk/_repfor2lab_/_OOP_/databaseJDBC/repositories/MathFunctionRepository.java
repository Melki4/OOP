package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.repositories;

import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.MathFunctions;
import java.util.List;

public interface MathFunctionRepository {
    void createTable();

    List<MathFunctions> findMathFunctionsByUserId(int id);
    List<MathFunctions> findMathFunctionsByName(String name);

    MathFunctions findMathFunctionComplex(double leftBoard, double rightBoard, int amountOfDots,
                                          String functionName);
    Integer findMathFunctionIdComplex(double leftBoard, double rightBoard, int amountOfDots,
                                      String functionName);

    void updateFunctionNameByFunctionId(String name, int id);

    void deleteMathFunctionByFunctionId(int id);
    void deleteAllFunctions();
    void deleteMathFunctionsByUserId(int id);

    void createMathFunction(String function_name, int amount_of_dots, double left_boarder,
                                double right_boarder, int owner_id, String function_type);

    boolean existsFunctionComplex(double leftBoard, double rightBoard, int amountOfDots,
                                  String functionName);
}
