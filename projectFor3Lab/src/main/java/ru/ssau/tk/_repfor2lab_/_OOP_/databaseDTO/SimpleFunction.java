package ru.ssau.tk._repfor2lab_._OOP_.databaseDTO;

public class SimpleFunction {
    private Long functionId;
    private String functionCode;
    private String localName;

    // Конструкторы
    public SimpleFunction() {}

    public SimpleFunction(Long functionId, String functionCode, String localName) {
        this.functionId = functionId;
        this.functionCode = functionCode;
        this.localName = localName;
    }

    // Геттеры и сеттеры
    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }

    public String getFunctionCode() { return functionCode; }
    public void setFunctionCode(String functionCode) { this.functionCode = functionCode; }

    public String getLocalName() { return localName; }
    public void setLocalName(String localName) { this.localName = localName; }
}
