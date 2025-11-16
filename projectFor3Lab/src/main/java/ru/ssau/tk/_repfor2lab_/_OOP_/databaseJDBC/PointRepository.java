package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.PointDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.List;

public interface PointRepository {
    void createTable();
    List<String> selectAllPoints();
    List<String> selectPointsByFunctionId(int id);
    String selectPointByPointId(int id);
    List<String> selectAllPointsSorted();
    List<String> selectPointsByFunctionIdSorted(int id);
    List<PointDTO> selectAllPointsAsDTO();
    List<PointDTO> selectPointsByFunctionIdAsDTO(int id);
    PointDTO selectPointByPointIdAsDTO(int id);
    List<PointDTO> selectAllPointsSortedAsDTO();
    List<PointDTO> selectPointsByFunctionIdSortedAsDTO(int id);
    int selectPointIdByXValueAndFunctionId(double x, int function_id);
    void updateXValueById(Double x_value, int id);
    void updateYValueById(Double y_value, int id);
    void deletePointById(int id);
    void deleteAllPoints();
    void addPoint(Double x_value, Double y_value, int id);
    void bulkInsertPointsDirect(List<Point> points, int function_id);
}
