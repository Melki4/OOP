package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewtonMethodTest {

    @Test
    void NewtonMTest1() {
        class CubFunc implements MathFunction {
            @Override
            public double apply(double x) {
                return -Math.pow(x, 3) - 2 * Math.pow(x, 2) - 4 * x + 10;
            }
        }
        class CubFuncDerrivative1 implements MathFunction {
            @Override
            public double apply(double x) {
                return -3 * (Math.pow(x, 2)) - 4 * x - 4;
            }
        }
        class CubFuncDerrivative2 implements MathFunction {
            @Override
            public double apply(double x) {
                return -6 * x - 4;
            }
        }

        double otrez[] = {0.0, 2.0};

        MathFunction f_f = new CubFunc();
        MathFunction s_f = new CubFuncDerrivative1();
        MathFunction ff_f = new CubFuncDerrivative2();

        var Tested_m = new NewtonMethod(otrez, f_f, s_f, ff_f);

        assertEquals(1.2442, Tested_m.apply(0.01), 0.01);
    }

    @Test
    void NewtonMTest2() {
        class Func implements MathFunction {
            @Override
            public double apply(double x) {
                return Math.pow(x, 2)-Math.sin(x);
            }
        }
        class Derrivative1 implements MathFunction {
            @Override
            public double apply(double x) {
                return 2*x-Math.cos(x);
            }
        }
        class Derrivative2 implements MathFunction {
            @Override
            public double apply(double x) {
                return 2+Math.sin(x);
            }
        }

        double otrez[] = {0.4, 2.0};

        MathFunction f_f = new Func();
        MathFunction s_f = new Derrivative1();
        MathFunction ff_f = new Derrivative2();

        var Tested_m = new NewtonMethod(otrez, f_f, s_f, ff_f);

        assertEquals(0.8767, Tested_m.apply(0.0001), 0.01);
    }
}

