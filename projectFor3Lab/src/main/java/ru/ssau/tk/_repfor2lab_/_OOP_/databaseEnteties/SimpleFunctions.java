package ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties;

public class SimpleFunctions {
    private String localName;

    // Конструкторы
    public SimpleFunctions() {}

    public SimpleFunctions(String localName) {
        this.localName = localName;
    }

    // Геттеры и сеттеры
    public String getLocalName() { return localName; }
    public void setLocalName(String localName) { this.localName = localName; }
}
