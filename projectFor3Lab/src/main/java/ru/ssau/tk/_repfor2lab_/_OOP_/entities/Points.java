package ru.ssau.tk._repfor2lab_._OOP_.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class Points {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pointID", nullable = false)
    private Integer pointID;

    @Column(name = "xValue", nullable = false)
    private double xValue;

    @Column(name = "yValue", nullable = false)
    private double yValue;

    @ManyToOne
    @JoinColumn(name = "functionID")
    private MathFunctions mathFunctions;

    public Points(){}
    public Points(Integer pointID, double xValue, double yValue){
        this.pointID = pointID;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public Integer getPointID() {
        return pointID;
    }

    public void setPointID(Integer pointID) {
        this.pointID = pointID;
    }

    public double getxValue() {
        return xValue;
    }

    public void setxValue(double xValue) {
        this.xValue = xValue;
    }

    public double getyValue() {
        return yValue;
    }

    public void setyValue(double yValue) {
        this.yValue = yValue;
    }

    public MathFunctions getMathFunctions() {
        return mathFunctions;
    }

    public void setMathFunctions(MathFunctions mathFunctions) {
        this.mathFunctions = mathFunctions;
    }
}
