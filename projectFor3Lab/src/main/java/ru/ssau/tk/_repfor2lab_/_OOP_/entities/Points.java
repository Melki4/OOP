package ru.ssau.tk._repfor2lab_._OOP_.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class Points {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pointID", nullable = false)
    private Long pointID;

    @Column(name = "xValue", nullable = false)
    private Double xValue;

    @Column(name = "yValue", nullable = false)
    private Double yValue;

    @ManyToOne
    @JoinColumn(name = "functionID")
    private MathFunctions mathFunctions;

    public Points(){}
    public Points(Long pointID, Double xValue, Double yValue, MathFunctions mathFunctions){
        this.pointID = pointID;
        this.xValue = xValue;
        this.yValue = yValue;
        this.mathFunctions = mathFunctions;
    }

    public Long getPointID() {
        return pointID;
    }

    public void setPointID(Long pointID) {
        this.pointID = pointID;
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

    public MathFunctions getMathFunctions() {
        return mathFunctions;
    }

    public void setMathFunctions(MathFunctions mathFunctions) {
        this.mathFunctions = mathFunctions;
    }
}
