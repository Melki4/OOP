package ru.ssau.tk._repfor2lab_._OOP_.DTO;

import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;

public class MathFunctionsDTO {
    private Long FunctionID;
    private String functionName;
    private Long amountOfDots;
    private Double leftBoarder;
    private Double rightBoarder;
    private String functionType;
    private Long ownerID;

    public MathFunctionsDTO(){}

    public MathFunctionsDTO(Long FunctionID, String functionName, Long amountOfDots,
                            Double leftBoarder, Double rightBoarder, String functionType, Long ownerID) {
        this.FunctionID = FunctionID;
        this.functionName = functionName;
        this.amountOfDots = amountOfDots;
        this.leftBoarder = leftBoarder;
        this.rightBoarder = rightBoarder;
        this.functionType = functionType;
        this.ownerID = ownerID;
    }

    public Long getFunctionID() {
        return FunctionID;
    }

    public void setFunctionID(Long mathFunctionsID) {
        this.FunctionID = mathFunctionsID;
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

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }
}
