package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunctionDTO;

import java.util.List;

public interface SimpleFunctionRepository {
    void createTable();

    List<String> selectAllSimpleFunctions();
    List<String> selectAllSimpleFunctionsSortedByLocalName();
    String selectSimpleFunctionByFunctionCode(String code);
    String selectSimpleFunctionNameByFunctionCode(String code);

    List<SimpleFunctionDTO> selectAllSimpleFunctionsAsDTO();
    List<SimpleFunctionDTO> selectAllSimpleFunctionsSortedByLocalNameAsDTO();
    SimpleFunctionDTO selectSimpleFunctionByFunctionCodeAsDTO(String code);

    void updateLocalNameByFunctionCode(String localName, String code);

    void deleteSimpleFunctionByFunctionCode(String code);
    void deleteAllFunctions();

    void addSimpleFunction(String functionCode, String localName);

    boolean existsSimpleFunction(String code);
}
