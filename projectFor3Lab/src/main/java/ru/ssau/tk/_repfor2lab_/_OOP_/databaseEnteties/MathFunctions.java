package ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties;

public class MathFunctions {
    private Integer functionId;
    private String functionName;
    private Integer amountOfDots;
    private Double leftBorder;
    private Double rightBorder;
    private Integer ownerId;
    private String functionType;

    // Конструкторы
    public MathFunctions() {}

    public MathFunctions(Integer functionId, String functionName, Integer amountOfDots,
                         Double leftBorder, Double rightBorder, Integer ownerId, String functionType) {
        this.functionId = functionId;
        this.functionName = functionName;
        this.amountOfDots = amountOfDots;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.ownerId = ownerId;
        this.functionType = functionType;
    }

    // Геттеры и сеттеры
    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }

    public Integer getAmountOfDots() { return amountOfDots; }
    public void setAmountOfDots(Integer amountOfDots) { this.amountOfDots = amountOfDots; }

    public Double getLeftBorder() { return leftBorder; }
    public void setLeftBorder(Double leftBorder) { this.leftBorder = leftBorder; }

    public Double getRightBorder() { return rightBorder; }
    public void setRightBorder(Double rightBorder) { this.rightBorder = rightBorder; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public String getFunctionType() { return functionType; }
    public void setFunctionType(String functionType) { this.functionType = functionType; }
}
