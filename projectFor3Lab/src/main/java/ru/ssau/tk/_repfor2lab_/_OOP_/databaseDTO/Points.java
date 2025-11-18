package ru.ssau.tk._repfor2lab_._OOP_.databaseDTO;

public class Points {
    private Long pointId;
    private Double xValue;
    private Double yValue;
    private Long functionId;

    // Конструкторы
    public Points() {}

    public Points(Long pointId, Double xValue, Double yValue, Long functionId) {
        this.pointId = pointId;
        this.xValue = xValue;
        this.yValue = yValue;
        this.functionId = functionId;
    }

    // Геттеры и сеттеры
    public Long getPointId() { return pointId; }
    public void setPointId(Long pointId) { this.pointId = pointId; }

    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }

    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }

    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }
}
