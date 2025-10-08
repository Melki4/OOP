package ru.ssau.tk._repfor2lab_._OOP_.functions;

import ru.ssau.tk._repfor2lab_._OOP_.exceptions.InterpolationException;

public class MockTabulatedFunction extends AbstractTabulatedFunction{

    private double x0 = 0.1;
    private double x1 = 0.5;
    private double y0 = 0.01;
    private double y1 = 0.25;

    {
        count = 2;
    }

    public MockTabulatedFunction(){
        x0 = 0.1;
        x1 = 0.5;
        y0 = 0.01;
        y1 = 0.25;
    }

    public MockTabulatedFunction(double x0, double x1, double y0, double y1){
        setDigits(x0, x1, y0, y1);
    }

    public void setDigits(double x0, double x1, double y0, double y1){
        this.x0=x0;
        this.y0=y0;
        this.x1=x1;
        this.y1=y1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x > x1) return 1;
        else return 0;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return (y0*(x1-x)-y1*(x0-x))/(x1-x0);
    }

    @Override
    protected double extrapolateRight(double x) {
        return y0 + (x - x0)/(x1-x0)*(y1-y0);
    }

    @Override
    protected double interpolate(double x, int floorIndex) throws InterpolationException {
        if (floorIndex >2 || floorIndex<0) throw new InterpolationException();
        return y0+(y1-y0)/(x1-x0)*(x-x0);
    }


    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getX(int index) throws RuntimeException{
        if (index>2||index<0) throw new RuntimeException();
        if (index == 0) return x0;
        else return x1;
    }

    @Override
    public double getY(int index) throws RuntimeException {
        if (index > 2 || index < 0) throw new RuntimeException();
        if (index == 0) return y0;
        else return y1;
    }

    @Override
    public void setY(int index, double value) throws RuntimeException {
        if (index > 2 || index < 0) throw new RuntimeException();
        //А как????? =)
    }

    @Override
    public int indexOfX(double x) {
        if (x == x0) return 0;
        else if (x == x1) return 1;
        else return -1;
    }

    @Override
    public int indexOfY(double y) {
        if (y == y0) return 0;
        else if (y == y1) return 1;
        else return -1;
    }

    @Override
    public double leftBound() {
        return x0;
    }

    @Override
    public double rightBound() {
        return x1;
    }
}
