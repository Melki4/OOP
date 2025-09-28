package ru.ssau.tk._repfor2lab_._OOP_.functions;

abstract class AbstractTabulatedFunction implements TabulatedFunction, Insertable {

    protected int count = 0;// отвечает за количество строк в таблице и должно быть возвращено соответствующим методом

    abstract protected int floorIndexOfX(double x);/*Метод поиска индекса x, который, в отличие от обычного indexOfX(),
    не должен возвращать -1 (если x не найден), а должен вернуть индекс максимального значения x, которое меньше заданного x.*/

    abstract protected double extrapolateLeft(double x);//Метод экстраполяции слева:
    abstract protected double extrapolateRight(double x);//Метод экстраполяции справа:
    abstract protected double interpolate(double x, int floorIndex);//Метод интерполяции с указанием индекса интервала:

    double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return leftY+(rightY-leftY)/(rightX-leftX)*(x-leftX);
    }

    @Override
    public double apply(double x) {
        if (x < leftBound()) return extrapolateLeft(x);

        else if (x > leftBound()) return extrapolateRight(x);

        else if (indexOfX(x) != -1) return getY(indexOfX(x));

        else return interpolate(x, floorIndexOfX(x));
    }
}
