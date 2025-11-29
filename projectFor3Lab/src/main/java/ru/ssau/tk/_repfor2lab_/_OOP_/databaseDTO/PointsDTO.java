package ru.ssau.tk._repfor2lab_._OOP_.databaseDTO;

public class PointsDTO {
    private Double xValue;
    private Double yValue;
    private Integer functionId;

    // Конструкторы
    public PointsDTO() {}

    public PointsDTO(Double xValue, Double yValue, Integer functionId) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.functionId = functionId;
    }

    // Геттеры и сеттеры

    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }

    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }

    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }
}
