package ru.ssau.tk._repfor2lab_._OOP_.entities;


import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name = "math_functions")
public class MathFunctions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "math_function_id", nullable = false)
    private Long mathFunctionsID;

    @Column(name = "function_name", length = 128, nullable = false)
    private String functionName;

    @Column(name = "amount_of_dots", nullable = false)
    private Long amountOfDots;

    @Column(name = "left_boarder", nullable = false)
    private Double leftBoarder;

    @Column(name = "right_boarder", nullable = false)
    private Double rightBoarder;

    @Column(name = "function_type", length = 64, nullable = false)
    private String functionType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Users users;

    @OneToMany(mappedBy = "mathFunctions")
    private List<Points> points;

    public MathFunctions(){}

    public MathFunctions(Long mathFunctionsID, String nameOfFunction, Long amountOfDots,
                         Double leftBoarder, Double rightBoarder, Users users){
        this.mathFunctionsID = mathFunctionsID;
        this.functionName = nameOfFunction;
        this.amountOfDots = amountOfDots;
        this.leftBoarder = leftBoarder;
        this.rightBoarder = rightBoarder;
        this.users = users;
    }

    public Long getMathFunctionsID() {
        return mathFunctionsID;
    }

    public void setMathFunctionsID(Long mathFunctionsID) {
        this.mathFunctionsID = mathFunctionsID;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Long getAmountOfDots() {
        return amountOfDots;
    }

    public void setAmountOfDots(Long amountOfDots) {
        this.amountOfDots = amountOfDots;
    }

    public Double getLeftBoarder() {
        return leftBoarder;
    }

    public void setLeftBoarder(Double leftBoarder) {
        this.leftBoarder = leftBoarder;
    }

    public Double getRightBoarder() {
        return rightBoarder;
    }

    public void setRightBoarder(Double rightBoarder) {
        this.rightBoarder = rightBoarder;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public List<Points> getPoints() {
        return points;
    }

    public void setPoints(List<Points> points) {
        this.points = points;
    }
}
