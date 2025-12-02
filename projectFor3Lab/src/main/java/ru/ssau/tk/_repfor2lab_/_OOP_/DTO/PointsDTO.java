package ru.ssau.tk._repfor2lab_._OOP_.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PointsDTO {
    private Long pointsID;
    @JsonProperty("x")
    private Double xValue;

    @JsonProperty("y")
    private Double yValue;
    private Long functionID;

    public PointsDTO(){}

    public PointsDTO(Long pointsID, Double xValue, Double yValue, Long functionID) {
        this.pointsID = pointsID;
        this.xValue = xValue;
        this.yValue = yValue;
        this.functionID = functionID;
    }

    public Long getPointsID() {
        return pointsID;
    }

    public void setPointsID(Long pointsID) {
        this.pointsID = pointsID;
    }

    public Double getxValue() {
        return xValue;
    }

    public void setxValue(Double xValue) {
        this.xValue = xValue;
    }

    public Double getyValue() {
        return yValue;
    }

    public void setyValue(Double yValue) {
        this.yValue = yValue;
    }

    public Long getFunctionID() {
        return functionID;
    }

    public void setFunctionID(Long functionID) {
        this.functionID = functionID;
    }
}
