package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class NewtonMethod implements MathFunction{

    public double[] interval;
    public MathFunction function;
    public MathFunction derivative1;
    public MathFunction derivative2;
    public double ACCURACY;//точность

    public  NewtonMethod(double[] i, MathFunction f, MathFunction d1, MathFunction d2){
        /**
         Предполагается, что первый параметр - массив из двух элементов: начало и конец отрезка
         второй аргумент - функция(от одной переменной), третий первая производная, четвёртый - вторая производная
         * */
        interval = i;
        function = f;
        derivative1 = d1;
        derivative2 =d2;
    }

    public double apply(double e) {//вычислить самый левый корень с заданной точностью
        ACCURACY = e;

        double side = interval[0];;
        double step = interval[1]-interval[0];

        for (int i=0;i<5;++i) {//поиск начальной точки
            side = interval[0];
            while (side<interval[1] && function.apply(side) * derivative2.apply(side) < 0.0) side+=step;
            if (side<interval[1] && function.apply(side) * derivative2.apply(side) > 0.0) break;
            step/=10;
        }

        if (side > interval[1]) return -1;//если не смогли найти начальную точку, удовлетворяющую условиям - выходим

        double f_x = side;
        double x1 = f_x;

        do {//применение непосредственно метода Ньютона
            f_x = x1;

            if (Math.abs(derivative1.apply(f_x)) < 1e-12) {//если производная уходит в ноль, метод не будет работать
                return -1;
            }

            x1 = f_x - function.apply(f_x) / derivative1.apply(f_x);

        } while (Math.abs(x1 - f_x) > ACCURACY);

        return x1;
    }
}
