package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class Point {

    public double x;
    public double y;

    public Point() {
        x = 0;
        y = 0;
    }
    public Point(double x, double y){
        this.x=x;
        this.y=y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
}
