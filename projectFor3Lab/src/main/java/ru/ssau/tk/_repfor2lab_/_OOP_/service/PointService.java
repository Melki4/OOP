package ru.ssau.tk._repfor2lab_._OOP_.service;

import ru.ssau.tk._repfor2lab_._OOP_.Dao.JdbcPointRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.List;

public class PointService {
    private JdbcPointRepository pointRepository;

    {
        pointRepository = new JdbcPointRepository();
    }

    public List<PointsDTO> findPointsByFunctionId(int function_id){
        return pointRepository.findPointsByFunctionIdAsDTO(function_id);
    }

    public List<PointsDTO> findPointsByFunctionIdSorted(int function_id){
        return pointRepository.findPointsByFunctionIdSortedAsDTO(function_id);
    }

    public void createPoint(double x_value, double y_value, int function_id){
        pointRepository.createPoint(x_value, y_value, function_id);
    }

    public void createManyPoints(List<Point> points, int function_id){
        pointRepository.addManyPoints(points, function_id);
    }

    public void updateXValue(int functionId, double oldValue, double newValue){
        pointRepository.updateXValueByFunctionIdAndOldX(oldValue, functionId, newValue);
    }

    public void updateYValue(int functionId, double oldValue, double newValue){
        pointRepository.updateYValueByFunctionIdAndOldY(oldValue, functionId, newValue);
    }

    public boolean deleteAllPoints(){
        return pointRepository.deleteAllPoints();
    }

    public boolean deletePointsByFunctionId(int function_id){
        return pointRepository.deletePointsByFunctionId(function_id);
    }
}
