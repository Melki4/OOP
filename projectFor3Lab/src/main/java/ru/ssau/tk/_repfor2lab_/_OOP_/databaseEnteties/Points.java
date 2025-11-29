package ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties;

public class Points {
    private Integer pointId;
    private Double xValue;
    private Double yValue;
    private Integer functionId;

    // Конструкторы
    public Points() {}

    public Points(Integer pointId, Double xValue, Double yValue, Integer functionId) {
        this.pointId = pointId;
        this.xValue = xValue;
        this.yValue = yValue;
        this.functionId = functionId;
    }

    // Геттеры и сеттеры
    public Integer getPointId() { return pointId; }
    public void setPointId(Integer pointId) { this.pointId = pointId; }

    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }

    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }

    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }
}
