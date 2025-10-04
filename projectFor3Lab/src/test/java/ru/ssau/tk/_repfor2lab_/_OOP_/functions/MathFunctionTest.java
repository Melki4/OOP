package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MathFunctionTest {
    @Test
    void MathFTest(){
        MathFunction f = new IdentityFunction();
        MathFunction g = new SqrFunction();
        MathFunction h = new IdentityFunction();
        var composite = f.andThen(g).andThen(h);
        assertEquals(169.0, composite.apply(13.0));
        assertEquals(256.0, new SqrFunction().andThen(g).andThen(new CompositeFunction(new IdentityFunction(), new SqrFunction())).apply(2));
        assertEquals(625.0, new SqrFunction().andThen(new SqrFunction().andThen(new ConstantFunction(5.0))).apply(3));
        assertEquals(6.0, new IdentityFunction().andThen(new ConstantFunction(6.0)).apply(-45.0));
    }
    @Test
    void testAndThenChain() {
        MathFunction f = x -> x + 1;  // f(x) = x + 1 лямбда выражение
        MathFunction g = x -> x * 2;  // g(x) = 2x
        MathFunction h = x -> x * x;  // h(x) = x²

        MathFunction composite = f.andThen(g).andThen(h);

        // f(g(h(2))) = f(g(4)) = f(8) = 9
        assertEquals(9.0, composite.apply(2.0), 1e-9);

        // Проверим промежуточные шаги
        MathFunction fg = f.andThen(g); // f(g(x)) = (2x) + 1
        assertEquals(5.0, fg.apply(2.0), 1e-9); // 2*2 + 1 = 5

        MathFunction fgh = fg.andThen(h); // f(g(h(x)))
        assertEquals(9.0, fgh.apply(2.0), 1e-9); // как выше

        /**
         MathFunction f = new MathFunction() { // анонимный класс
            @Override
            public int execute(int x) {
            return x + 1;
            }
        };
         */
    }
}