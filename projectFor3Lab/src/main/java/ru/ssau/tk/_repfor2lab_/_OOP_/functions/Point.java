package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class Point {

    public double x;
    public double y;

    // Конструктор по умолчанию (обязателен для Jackson)
    public Point() {
        x = 0;
        y = 0;
    }

    public Point(double x, double y){
        this.x=x;
        this.y=y;
    }
}
