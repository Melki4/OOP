package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class NewtonMethod implements MathFunction{

    public double[] interval;
    public MathFunction function;
    public MathFunction derivative1;
    public MathFunction derivative2;
    public double ACCURACY;

    public  NewtonMethod(double[] i, MathFunction f, MathFunction d1, MathFunction d2){
        interval = i;
        function = f;
        derivative1 = d1;
        derivative2 =d2;
    }

    public double apply(double e) {
        ACCURACY = e;
        double side = interval[0];
        while (side < interval[1]) {
            if (function.apply(side) * derivative2.apply(side) > 0.0)  break;
            side+=0.1;
        }
        if (side > interval[1]) return -1;

        double f_x = side;
        double x1 = f_x;

        do {
            f_x = x1;

            if (Math.abs(derivative1.apply(f_x)) < 1e-12) {
                return -1;
            }
            x1 = f_x - function.apply(f_x) / derivative1.apply(f_x);

        } while (Math.abs(x1 - f_x) > ACCURACY);

        return x1;
    }
}
