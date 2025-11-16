package ru.ssau.tk._repfor2lab_._OOP_.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "simplefunctions")
public class SimpleFunctions {
    @Id
    @Column(name = "functionCode", length = 32, nullable = false)
    private String functionCode;

    @Column(name = "localName", length = 64, nullable = false)
    private String localName;

    public SimpleFunctions(){}
    public SimpleFunctions(String functionCode, String localName){
        this.functionCode = functionCode;
        this.localName = localName;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}
