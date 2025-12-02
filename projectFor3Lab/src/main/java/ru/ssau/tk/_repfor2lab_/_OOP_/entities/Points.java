package ru.ssau.tk._repfor2lab_._OOP_.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class Points {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id", nullable = false)
    private Long pointID;
    @Column(name = "x_value", nullable = false)
    private Double xValue;

    @Column(name = "y_value", nullable = false)
    private Double yValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id")
    private MathFunctions mathFunctions;

    public Points(){}
    public Points(Double xValue, Double yValue, MathFunctions mathFunctions){
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
