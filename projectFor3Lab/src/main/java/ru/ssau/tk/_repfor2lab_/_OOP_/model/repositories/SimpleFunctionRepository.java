package ru.ssau.tk._repfor2lab_._OOP_.model.repositories;

import ru.ssau.tk._repfor2lab_._OOP_.model.databaseEnteties.SimpleFunctions;

import java.util.List;

public interface SimpleFunctionRepository {
    void createTable();

    List<SimpleFunctions> findAllSimpleFunctions();
    List<SimpleFunctions> findAllSimpleFunctionsSortedByLocalName();

    void updateSimpleFunctionName(String oldName, String newName);

    boolean deleteSimpleFunctionByName(String localName);
    boolean deleteAllFunctions();

    SimpleFunctions createSimpleFunction(String localName);

    boolean existSimpleFunction(String localName);
}
