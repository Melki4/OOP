package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.repositories;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.Points;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.List;

public interface PointRepository {
    void createTable();

    List<Points> findPointsByFunctionId(int id);
    List<Points> findPointsByFunctionIdSorted(int id);

    void updateXValueByFunctionIdAndOldX(Double x_value_old, int id, Double x_value_new);
    void updateYValueByFunctionIdAndOldY(Double y_value_old, int id, Double y_value_new);

    void deleteAllPoints();
    void deletePointsByFunctionId(int functionId);

    void createPoint(Double x_value, Double y_value, int id);

    void addManyPoints(List<Point> points, int function_id);
}
