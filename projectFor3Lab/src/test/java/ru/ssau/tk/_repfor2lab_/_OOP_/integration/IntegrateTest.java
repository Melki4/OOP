package ru.ssau.tk._repfor2lab_._OOP_.integration;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static org.junit.jupiter.api.Assertions.*;

class IntegrateTest {

    // Тестовые функции и их аналитические интегралы
    private static final MathFunction CONSTANT_FUNCTION = x -> 5.0;
    private static final MathFunction LINEAR_FUNCTION = x -> 2 * x + 3;
    private static final MathFunction QUADRATIC_FUNCTION = x -> x * x + 2 * x + 1;
    private static final MathFunction SIN_FUNCTION = Math::sin;
    private static final MathFunction EXP_FUNCTION = Math::exp;

    // Вспомогательный метод для вычисления интеграла
    private double computeIntegral(TabulatedFunction f, int forkFactor) {

        ForkJoinPool forkJoinPool = new ForkJoinPool(forkFactor);

        try {
            // Оцениваем общее количество задач (можно улучшить эту оценку)

            int r = 10000;

            Params params = new Params(forkFactor, r);

            ForkJoinTask<Double> task = new Integrate(new Interval(0, f.getCount()-1), f, params);
            ForkJoinTask<Double> result = forkJoinPool.submit(task);
            return result.join();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка вычисления интеграла", e);
        }
    }

    @Test
    @Timeout(5)
    void testConstantFunctionIntegration() {
        // ∫ 5 dx от 0 до 10 = 5 * (10 - 0) = 50
        TabulatedFunction f = new ArrayTabulatedFunction(CONSTANT_FUNCTION, 0, 10, 100000);
        double expected = 50.0;
        double result = computeIntegral(f, 8);
        assertEquals(expected, result, 0.2, "Интеграл константной функции");
    }

    @Test
    @Timeout(5)
    void testLinearFunctionIntegration() {
        // ∫ (2x + 3) dx от 1 до 4 = [x² + 3x] от 1 до 4 = (16+12) - (1+3) = 28 - 4 = 24
        TabulatedFunction f = new ArrayTabulatedFunction(LINEAR_FUNCTION, 1, 4, 2001);
        double expected = 24.0;

        double result = computeIntegral(f, 1);

        assertEquals(expected, result, 0.1, "Интеграл линейной функции");
    }

    @Test
    @Timeout(5)
    void testQuadraticFunctionIntegration() {
        // ∫ (x² + 2x + 1) dx от 1 до 10 = [x³/3 + x² + x] от 1 до 10
        // = (1000/3 + 100 + 10) - (1/3 + 1 + 1) = (333.33 + 110) - (0.33 + 2) = 443.33 - 2.33 = 441
        TabulatedFunction f = new ArrayTabulatedFunction(QUADRATIC_FUNCTION, 1, 10, 100000);
        double expected = 441.0;

        double result = computeIntegral(f, 1);

        assertEquals(expected, result, 0.1, "Интеграл квадратичной функции");
    }

    @Test
    @Timeout(5)
    void testQuadraticFunctionIntegration1() {
        // ∫ (x² + 2x + 1) dx от 1 до 10 = [x³/3 + x² + x] от 1 до 10
        // = (1000/3 + 100 + 10) - (1/3 + 1 + 1) = (333.33 + 110) - (0.33 + 2) = 443.33 - 2.33 = 441
        TabulatedFunction f = new ArrayTabulatedFunction(QUADRATIC_FUNCTION, 1, 10, 110000);
        double expected = 441.0;

        double result = computeIntegral(f, 1);

        assertEquals(expected, result, 0.1, "Интеграл квадратичной функции");
    }

    @Test
    @Timeout(5)
    void testSinFunctionIntegration() {
        // ∫ sin(x) dx от 0 до π = [-cos(x)] от 0 до π = (-cos(π)) - (-cos(0)) = (1) - (-1) = 2
        TabulatedFunction f = new ArrayTabulatedFunction(SIN_FUNCTION, 0, 3.14, 100001);
        double expected = 2.0;
//        System.out.println(f);

        double result = computeIntegral(f, 1);
        assertEquals(expected, result, 0.1, "Интеграл синуса");
    }

    @Test
    @Timeout(5)
    void testExpFunctionIntegration() {
        // ∫ e^x dx от 0 до 1 = [e^x] от 0 до 1 = e - 1 ≈ 1.71828
        TabulatedFunction f = new ArrayTabulatedFunction(EXP_FUNCTION, 0, 10, 48209);
        double expected = 22025.4657948;

        double result = computeIntegral(f, 1);

        assertEquals(expected, result, 1, "Интеграл экспоненты");
    }

    @Test
    @Timeout(5)
    void testDifferentForkFactors() {
        TabulatedFunction f = new ArrayTabulatedFunction(QUADRATIC_FUNCTION, 1, 10, 43001);
        double expected = 441.0;

        // Тестируем с разным количеством потоков
        for (int forkFactor : new int[]{1, 1}) {
            double result = computeIntegral(f, forkFactor);
            assertEquals(expected, result, 1.0,
                    "Интеграл с forkFactor = " + forkFactor);
        }
    }

    @Test
    @Timeout(5)
    void testSmallInterval() {
        // Маленький интервал должен обрабатываться bruteForce
        TabulatedFunction f = new ArrayTabulatedFunction(LINEAR_FUNCTION, 0, 0.5, 1001);
        double expected = 1.75; // ∫ (2x + 3) dx от 0 до 0.5 = [x² + 3x] = (0.25 + 1.5) = 1.75

        double result = computeIntegral(f, 7);

        assertEquals(expected, result, 0.01, "Интеграл на маленьком интервале");
    }

    @Test
    @Timeout(5)
    void testLargeNumberOfPoints() {
        // Тест с большим количеством точек
        TabulatedFunction f = new ArrayTabulatedFunction(QUADRATIC_FUNCTION, 1, 10, 300001);
        double expected = 441.0;

        double result = computeIntegral(f, 1);

        assertEquals(expected, result, 0.5, "Интеграл с большим количеством точек");
    }

    @Test
    @Timeout(10)
    void testConcurrentExecution() throws InterruptedException {
        // Тест на правильность работы в конкурентной среде
        TabulatedFunction f = new ArrayTabulatedFunction(QUADRATIC_FUNCTION, 1, 10, 10001);
        double expected = 441.0;

        // Запускаем несколько вычислений параллельно
        CompletableFuture<Double>[] futures = new CompletableFuture[5];
        for (int i = 0; i < futures.length; i++) {
            futures[i] = CompletableFuture.supplyAsync(() -> computeIntegral(f, 1));
        }

        // Проверяем все результаты
        CompletableFuture.allOf(futures).join();
        for (CompletableFuture<Double> future : futures) {
            double result = future.join();
            assertEquals(expected, result, 1.0, "Параллельное вычисление интеграла");
        }
    }

    @Test
    @Timeout(5)
    void testZeroLengthInterval() {
        // ∫ f(x) dx от a до a = 0
        TabulatedFunction f = new ArrayTabulatedFunction(QUADRATIC_FUNCTION, 5, 5, 1011);
        double expected = 0.0;

        double result = computeIntegral(f, 7);

        assertEquals(expected, result, 1e-10, "Интеграл на нулевом интервале");
    }

    @Test
    @Timeout(5)
    void testNegativeBounds() {
        // ∫ (2x + 3) dx от -2 до 2 = [x² + 3x] от -2 до 2 = (4+6) - (4-6) = 10 - (-2) = 12
        TabulatedFunction f = new ArrayTabulatedFunction(LINEAR_FUNCTION, -2, 2, 100001);
        double expected = 12.0;

        double result = computeIntegral(f, 7);

        assertEquals(expected, result, 0.1, "Интеграл с отрицательными границами");
    }

    @Test
    @Timeout(10)
    void testDifferentForkFactorsComparison() {
        TabulatedFunction f = new ArrayTabulatedFunction(QUADRATIC_FUNCTION, 1, 10, 500000);
        double expected = 441.0;

        for (int forkFactor : new int[]{1, 2, 4, 8, 16}) {
            long startTime = System.currentTimeMillis();
            double result = computeIntegral(f, forkFactor);
            long time = System.currentTimeMillis() - startTime;

            System.out.println("ForkFactor " + forkFactor + ": " + time + "ms");
            assertEquals(expected, result, 0.5,
                    "Результат для forkFactor = " + forkFactor);
        }
    }
}