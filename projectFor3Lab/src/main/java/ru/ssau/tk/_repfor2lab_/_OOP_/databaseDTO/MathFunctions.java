package ru.ssau.tk._repfor2lab_._OOP_.databaseDTO;

public class MathFunctions {
    private Long functionId;
    private String functionName;
    private Long amountOfDots;
    private Double leftBorder;
    private Double rightBorder;
    private Long ownerId;
    private String ownerLogin;
    private String functionType;
    private String functionTypeLocalName;

    // Конструкторы
    public MathFunctions() {}

    public MathFunctions(Long functionId, String functionName, Long amountOfDots,
                         Double leftBorder, Double rightBorder, Long ownerId, String ownerLogin,
                         String functionType, String functionTypeLocalName) {
        this.functionId = functionId;
        this.functionName = functionName;
        this.amountOfDots = amountOfDots;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.ownerId = ownerId;
        this.ownerLogin = ownerLogin;
        this.functionType = functionType;
        this.functionTypeLocalName = functionTypeLocalName;
    }

    // Геттеры и сеттеры
    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }

    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }

    public Long getAmountOfDots() { return amountOfDots; }
    public void setAmountOfDots(Long amountOfDots) { this.amountOfDots = amountOfDots; }

    public Double getLeftBorder() { return leftBorder; }
    public void setLeftBorder(Double leftBorder) { this.leftBorder = leftBorder; }

    public Double getRightBorder() { return rightBorder; }
    public void setRightBorder(Double rightBorder) { this.rightBorder = rightBorder; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerLogin() { return ownerLogin; }
    public void setOwnerLogin(String ownerLogin) { this.ownerLogin = ownerLogin; }

    public String getFunctionType() { return functionType; }
    public void setFunctionType(String functionType) { this.functionType = functionType; }

    public String getFunctionTypeLocalName() { return functionType; }
    public void setFunctionTypeLocalName(String functionTypeLocalName) { this.functionTypeLocalName = functionTypeLocalName; }
}
