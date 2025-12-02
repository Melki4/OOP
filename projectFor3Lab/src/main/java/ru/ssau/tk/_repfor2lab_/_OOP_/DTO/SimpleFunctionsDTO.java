package ru.ssau.tk._repfor2lab_._OOP_.DTO;

public class SimpleFunctionsDTO {
    private String localName;
    public SimpleFunctionsDTO(){}
    public SimpleFunctionsDTO(String localName){
        this.localName = localName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}
