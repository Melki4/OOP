package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class NewtonMethod implements MathFunction{

    public double startDot;//начальная точка, которую надо задать
    public MathFunction function;
    public MathFunction derivative1;
    public double ACCURACY;//точность

    public  NewtonMethod(double i, MathFunction f, MathFunction d1, MathFunction d2){
        /**
         Предполагается, что первый параметр - массив из двух элементов: начало и конец отрезка
         второй аргумент - функция(от одной переменной), третий первая производная, четвёртый - вторая производная
         * */
        startDot = i;
        function = f;
        derivative1 = d1;
    }

    public double apply(double e) {//вычислить корень с заданной точностью
        ACCURACY = e;

        double side = startDot;

        double f_x = side;
        double x1 = f_x;

        do {//применение непосредственно метода Ньютона
            f_x = x1;

            if (Math.abs(derivative1.apply(f_x)) < 1e-6) {//если производная уходит в ноль, метод не будет работать
                return -1.0;
            }

            x1 = f_x - function.apply(f_x) / derivative1.apply(f_x);

        } while (Math.abs(x1 - f_x) > ACCURACY);

        return x1;
    }
}
