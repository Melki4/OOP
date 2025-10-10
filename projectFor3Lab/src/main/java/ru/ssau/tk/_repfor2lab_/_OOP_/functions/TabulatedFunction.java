package ru.ssau.tk._repfor2lab_._OOP_.functions;

import java.util.Iterator;

public interface TabulatedFunction extends MathFunction, Iterable<Point>{
    int getCount();//Метод получения количества табулированных значений
    double getX(int index);//Метод, получающий значение аргумента x по номеру индекса
    double getY(int index);//Метод, получающий значение y по номеру индекса:
    void setY(int index, double value);//Метод, задающий значение y по номеру индекса:
    int indexOfX(double x);//если нет элемента в таблице вернуть -1
    int indexOfY(double y);//Метод, возвращающий индекс первого вхождения значения y. Если
                            // такого y в таблице нет, то необходимо вернуть -1:
    double leftBound();//Метод, возвращающий самый левый x:
    double rightBound();//Метод, возвращающий самый правый x
}
