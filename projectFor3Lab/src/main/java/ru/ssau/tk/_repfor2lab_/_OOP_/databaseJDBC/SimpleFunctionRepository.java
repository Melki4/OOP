package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunction;

import java.util.List;

public interface SimpleFunctionRepository {
    void createTable();

    String selectSimpleFunctionNameByFunctionCode(String code);

    List<SimpleFunction> selectAllSimpleFunctions();
    List<SimpleFunction> selectAllSimpleFunctionsSortedByLocalName();
    SimpleFunction selectSimpleFunctionByFunctionCode(String code);

    void updateLocalNameByFunctionCode(String localName, String code);

    void deleteSimpleFunctionByFunctionCode(String code);
    void deleteAllFunctions();

    void addSimpleFunction(String functionCode, String localName);

    boolean existsSimpleFunction(String code);
}
