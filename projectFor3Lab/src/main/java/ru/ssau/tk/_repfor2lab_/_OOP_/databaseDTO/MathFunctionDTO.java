package ru.ssau.tk._repfor2lab_._OOP_.databaseDTO;

public class MathFunctionDTO {
    private Long functionId;
    private String functionName;
    private Integer amountOfDots;
    private Double leftBorder;
    private Double rightBorder;
    private Long ownerId;
    private String functionType;

    // Конструкторы
    public MathFunctionDTO() {}

    public MathFunctionDTO(Long functionId, String functionName, Integer amountOfDots,
                           Double leftBorder, Double rightBorder, Long ownerId, String functionType) {
        this.functionId = functionId;
        this.functionName = functionName;
        this.amountOfDots = amountOfDots;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.ownerId = ownerId;
        this.functionType = functionType;
    }

    // Геттеры и сеттеры
    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }

    public Integer getAmountOfDots() { return amountOfDots; }
    public void setAmountOfDots(Integer amountOfDots) { this.amountOfDots = amountOfDots; }

    public Double getLeftBorder() { return leftBorder; }
    public void setLeftBorder(Double leftBorder) { this.leftBorder = leftBorder; }

    public Double getRightBorder() { return rightBorder; }
    public void setRightBorder(Double rightBorder) { this.rightBorder = rightBorder; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getFunctionType() { return functionType; }
    public void setFunctionType(String functionType) { this.functionType = functionType; }
}
