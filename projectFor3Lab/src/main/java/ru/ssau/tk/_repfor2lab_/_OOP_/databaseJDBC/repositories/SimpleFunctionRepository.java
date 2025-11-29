package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.repositories;

import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.SimpleFunctions;

import java.util.List;

public interface SimpleFunctionRepository {
    void createTable();

    List<SimpleFunctions> findAllSimpleFunctions();
    List<SimpleFunctions> findAllSimpleFunctionsSortedByLocalName();

    void updateSimpleFunctionName(String oldName, String newName);

    void deleteSimpleFunctionByName(String localName);
    void deleteAllFunctions();

    void createSimpleFunction(String localName);

    boolean existSimpleFunction(String localName);
}
