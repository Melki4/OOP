package ru.ssau.tk._repfor2lab_._OOP_.DTO;

import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;

public class MathFunctionsDTO {
    private Long mathFunctionsID;
    private String nameOfFunction;
    private Long amountOfDots;
    private Double leftBoarder;
    private Double rightBoarder;
    private String functionType;
    private Long ownerID;

    public MathFunctionsDTO(){}

    public MathFunctionsDTO(Long mathFunctionsID, String nameOfFunction, Long amountOfDots,
                            Double leftBoarder, Double rightBoarder, String functionType, Long ownerID) {
        this.mathFunctionsID = mathFunctionsID;
        this.nameOfFunction = nameOfFunction;
        this.amountOfDots = amountOfDots;
        this.leftBoarder = leftBoarder;
        this.rightBoarder = rightBoarder;
        this.functionType = functionType;
        this.ownerID = ownerID;
    }

    public Long getMathFunctionsID() {
        return mathFunctionsID;
    }

    public void setMathFunctionsID(Long mathFunctionsID) {
        this.mathFunctionsID = mathFunctionsID;
    }

    public String getNameOfFunction() {
        return nameOfFunction;
    }

    public void setNameOfFunction(String nameOfFunction) {
        this.nameOfFunction = nameOfFunction;
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

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }
}
