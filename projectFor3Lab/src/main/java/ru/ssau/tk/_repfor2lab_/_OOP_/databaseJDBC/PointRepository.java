package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.Points;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.List;

public interface PointRepository {
    void createTable();

    List<Points> selectAllPoints();
    List<Points> selectPointsByFunctionId(int id);
    Points selectPointByPointId(int id);
    List<Points> selectAllPointsSorted();
    List<Points> selectPointsByFunctionIdSorted(int id);

    int selectPointIdByXValueAndFunctionId(double x, int function_id);

    void updateXValueById(Double x_value, int id);
    void updateYValueById(Double y_value, int id);

    void deletePointById(int id);
    void deleteAllPoints();

    void addPoint(Double x_value, Double y_value, int id);

    void bulkInsertPointsDirect(List<Point> points, int function_id);
}
