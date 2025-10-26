package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {

    SynchronizedTabulatedFunction list;
    SynchronizedTabulatedFunction array;

    @BeforeEach
    void create(){

        double[] xValues = {1, 2, 3, 4, 5, 6, 7,8, 9, 10};
        double[] yValues = {1.2, 1.3, 1.4, 1.4, 1.6, 1.8, 1.5, 1.5, 1.6, 1.7};

        MathFunction func = (double x) -> x*x -2*x+1;

        LinkedListTabulatedFunction listTabulatedFunction = new LinkedListTabulatedFunction(xValues, yValues);
        ArrayTabulatedFunction arrayTabulatedFunction = new ArrayTabulatedFunction(func, 1, 10, 20);

        list = new SynchronizedTabulatedFunction(listTabulatedFunction);
        array = new SynchronizedTabulatedFunction(arrayTabulatedFunction);
    }

    @Test
    void getCount() {
        assertEquals(10, list.getCount());
        assertEquals(20, array.getCount());
    }

    @Test
    void getX() {
        assertEquals(3.0, list.getX(2));
        assertEquals(10.0, array.getX(19));
    }

    @Test
    void getY() {
        assertEquals(1.4, list.getY(2));
        assertEquals(81.0, array.getY(19));
    }

    @Test
    void setY() {
        list.setY(2, 101.4);
        array.setY(19, 13.4);
        assertEquals(101.4, list.getY(2));
        assertEquals(13.4, array.getY(19));
    }

    @Test
    void indexOfX() {
        assertEquals(-1, array.indexOfX(101));
        assertEquals(-1, list.indexOfX(101));
        assertEquals(0, list.indexOfX(1.0));
        assertEquals(0, array.indexOfX(1.0));
    }

    @Test
    void indexOfY() {
        assertEquals(-1, array.indexOfY(101));
        assertEquals(-1, list.indexOfY(101));
        assertEquals(2, list.indexOfY(1.4));
        assertEquals(19, array.indexOfY(81.0));
    }

    @Test
    void leftBound() {
        assertEquals(1.0, list.leftBound());
        assertEquals(1.0, array.leftBound());
    }

    @Test
    void rightBound() {
        assertEquals(10.0, list.rightBound());
        assertEquals(10.0, array.rightBound());
    }

    @Test
    void iterator() {
        synchronized (list){
            Iterator<Point> iterator = list.iterator();
            int i =0;
            Point boof;
            while(iterator.hasNext()){
                boof = iterator.next();
                assertEquals(list.getX(i), boof.x);
                assertEquals(list.getY(i), boof.y);
                i++;
            }
        }

        synchronized (array){
            Iterator<Point> iterator = array.iterator();
            int i = 0;
            Point boof;
            while(iterator.hasNext()){
                boof = iterator.next();
                assertEquals(array.getX(i), boof.x);
                assertEquals(array.getY(i), boof.y);
                i++;
            }
        }
    }
    @Test
    void testIteratorBasicFunctionality() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        assertTrue(iterator.hasNext());

        Point point1 = iterator.next();
        assertEquals(1.0, point1.x, 0.0001);
        assertEquals(1.0, point1.y, 0.0001);

        Point point2 = iterator.next();
        assertEquals(2.0, point2.x, 0.0001);
        assertEquals(4.0, point2.y, 0.0001);

        Point point3 = iterator.next();
        assertEquals(3.0, point3.x, 0.0001);
        assertEquals(9.0, point3.y, 0.0001);

        Point point4 = iterator.next();
        assertEquals(4.0, point4.x, 0.0001);
        assertEquals(16.0, point4.y, 0.0001);

        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorWithWhileLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[index], point.x, 0.0001);
            assertEquals(yValues[index], point.y, 0.0001);
            index++;
        }

        assertEquals(3, index);
    }

    @Test
    void testIteratorWithForEachLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        int index = 0;
        for (Point point : syncFunction) {
            assertEquals(xValues[index], point.x, 0.0001);
            assertEquals(yValues[index], point.y, 0.0001);
            index++;
        }

        assertEquals(3, index);
    }

    @Test
    void testIteratorThrowsNoSuchElementException() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        iterator.next();
        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorIsIndependentCopy() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        syncFunction.setY(1, 25.0);

        Point point1 = iterator.next();
        assertEquals(1.0, point1.x, 0.0001);
        assertEquals(1.0, point1.y, 0.0001);

        Point point2 = iterator.next();
        assertEquals(2.0, point2.x, 0.0001);
        assertEquals(4.0, point2.y, 0.0001);

        Point point3 = iterator.next();
        assertEquals(3.0, point3.x, 0.0001);
        assertEquals(9.0, point3.y, 0.0001);
    }

    @Test
    void testMultipleIteratorsAreIndependent() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator1 = syncFunction.iterator();
        Iterator<Point> iterator2 = syncFunction.iterator();

        Point point1FromIterator1 = iterator1.next();
        Point point1FromIterator2 = iterator2.next();

        assertEquals(point1FromIterator1.x, point1FromIterator2.x, 0.0001);
        assertEquals(point1FromIterator1.y, point1FromIterator2.y, 0.0001);

        Point point2FromIterator1 = iterator1.next();
        assertEquals(2.0, point2FromIterator1.x, 0.0001);

        Point point2FromIterator2 = iterator2.next();
        assertEquals(2.0, point2FromIterator2.x, 0.0001);
    }

    @Test
    void testIteratorConsistencyDuringModifications() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        Point point1 = iterator.next();
        assertEquals(1.0, point1.x, 0.0001);
        assertEquals(1.0, point1.y, 0.0001);

        syncFunction.setY(2, 25.0);
        syncFunction.setY(0, 0.5);

        Point point2 = iterator.next();
        assertEquals(2.0, point2.x, 0.0001);
        assertEquals(4.0, point2.y, 0.0001);

        Point point3 = iterator.next();
        assertEquals(3.0, point3.x, 0.0001);
        assertEquals(9.0, point3.y, 0.0001);

        Point point4 = iterator.next();
        assertEquals(4.0, point4.x, 0.0001);
        assertEquals(16.0, point4.y, 0.0001);
    }

    @Test
    void testMultipleIteratorsWithConcurrentModifications() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator1 = syncFunction.iterator();

        syncFunction.setY(1, 25.0);

        Iterator<Point> iterator2 = syncFunction.iterator();

        Point point1FromIterator1 = iterator1.next();
        assertEquals(1.0, point1FromIterator1.y, 0.0001);

        Point point2FromIterator1 = iterator1.next();
        assertEquals(4.0, point2FromIterator1.y, 0.0001);

        Point point1FromIterator2 = iterator2.next();
        assertEquals(1.0, point1FromIterator2.y, 0.0001);

        Point point2FromIterator2 = iterator2.next();
        assertEquals(25.0, point2FromIterator2.y, 0.0001);
    }

    @Test
    void testIteratorWithLargeDataset() {
        int size = 100;
        double[] xValues = new double[size];
        double[] yValues = new double[size];

        for (int i = 0; i < size; i++) {
            xValues[i] = i;
            yValues[i] = i * i;
        }

        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();
        int count = 0;

        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(count, point.x, 0.0001);
            assertEquals(count * count, point.y, 0.0001);
            count++;
        }

        assertEquals(size, count);
    }

    @Test
    void testIteratorHasNextIdempotent() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());

        Point point1 = iterator.next();
        assertEquals(1.0, point1.x, 0.0001);

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());

        Point point2 = iterator.next();
        assertEquals(2.0, point2.x, 0.0001);

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());

        Point point3 = iterator.next();
        assertEquals(3.0, point3.x, 0.0001);

        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorWithZeroValues() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 0.0, 0.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        Point point1 = iterator.next();
        assertEquals(0.0, point1.x, 0.0001);
        assertEquals(0.0, point1.y, 0.0001);

        Point point2 = iterator.next();
        assertEquals(1.0, point2.x, 0.0001);
        assertEquals(0.0, point2.y, 0.0001);

        Point point3 = iterator.next();
        assertEquals(2.0, point3.x, 0.0001);
        assertEquals(0.0, point3.y, 0.0001);

        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorWithNegativeValues() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        Point point1 = iterator.next();
        assertEquals(-2.0, point1.x, 0.0001);
        assertEquals(4.0, point1.y, 0.0001);

        Point point2 = iterator.next();
        assertEquals(-1.0, point2.x, 0.0001);
        assertEquals(1.0, point2.y, 0.0001);

        Point point3 = iterator.next();
        assertEquals(0.0, point3.x, 0.0001);
        assertEquals(0.0, point3.y, 0.0001);

        Point point4 = iterator.next();
        assertEquals(1.0, point4.x, 0.0001);
        assertEquals(1.0, point4.y, 0.0001);

        Point point5 = iterator.next();
        assertEquals(2.0, point5.x, 0.0001);
        assertEquals(4.0, point5.y, 0.0001);

        assertFalse(iterator.hasNext());
    }

    @Test
    void testDoSynchronouslyWithIntegerOperation() {
        // Operation<Integer> - подсчет количества элементов больше определенного значения
        Integer result = list.doSynchronously(new SynchronizedTabulatedFunction.Operation<Integer>() {
            @Override
            public Integer apply(SynchronizedTabulatedFunction f) {
                int count = 0;
                for (int i = 0; i < list.getCount(); i++) {
                    if (list.getY(i) > 25.0) {
                        count++;
                    }
                }
                return count;
            }
        });

        assertEquals(0, result); // 40.0 и 50.0 больше 25.0
    }

    @Test
    void testDoSynchronouslyWithDoubleOperation() {
        // Operation<Double> - вычисление среднего значения Y
        Double result = array.doSynchronously(new SynchronizedTabulatedFunction.Operation<Double>() {
            @Override
            public Double apply(SynchronizedTabulatedFunction func) {
                double sum = 0.0;
                for (int i = 0; i < func.getCount(); i++) {
                    sum += func.getY(i);
                }
                return sum / func.getCount();
            }
        });

        assertEquals(27.71, result, 1e-3); //
    }

    @Test
    void testDoSynchronouslyWithStringOperation() {
        // Operation<String> - создание строкового представления
        String result = list.doSynchronously(new SynchronizedTabulatedFunction.Operation<String>() {
            @Override
            public String apply(SynchronizedTabulatedFunction func) {

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < func.getCount(); i++) {
                    sb.append(String.format("(%.1f, %.1f) ", func.getX(i), func.getY(i)));
                }

                return sb.toString().trim();
            }
        });

        String expected = "(1,0, 1,2) (2,0, 1,3) (3,0, 1,4) (4,0, 1,4) (5,0, 1,6) (6,0, 1,8) (7,0, 1,5)" +
                " (8,0, 1,5) (9,0, 1,6) (10,0, 1,7)";//удалились с конца и начала пробелы из-за trim()
        assertEquals(expected, result);
    }

    @Test
    void testDoSynchronouslyWithBooleanOperation() {
        // Operation<Boolean> - проверка монотонности
        Boolean result1 = array.doSynchronously(new SynchronizedTabulatedFunction.Operation<Boolean>() {
            @Override
            public Boolean apply(SynchronizedTabulatedFunction func) {
                for (int i = 1; i < func.getCount(); i++) {
                    if (func.getY(i) <= func.getY(i - 1)) {
                        return false;
                    }
                }
                return true;
            }
        });

        Boolean result2 = list.doSynchronously(new SynchronizedTabulatedFunction.Operation<Boolean>() {
            @Override
            public Boolean apply(SynchronizedTabulatedFunction func) {
                for (int i = 1; i < func.getCount(); i++) {
                    if (func.getY(i) <= func.getY(i - 1)) {
                        return false;
                    }
                }
                return true;
            }
        });

        assertTrue(result1); // Y значения строго возрастают
        assertFalse(result2); // Y значения строго возрастают
    }

    @Test
    void testDoSynchronouslyWithVoidOperation() {
        // Operation<Void> - модификация данных без возвращаемого значения
        Void result = list.doSynchronously(new SynchronizedTabulatedFunction.Operation<Void>() {
            @Override
            public Void apply(SynchronizedTabulatedFunction func) {
                // Увеличиваем все Y значения в 2 раза
                for (int i = 0; i < func.getCount(); i++) {
                    func.setY(i, func.getY(i) * 2);
                }
                return null;
            }
        });

        assertNull(result);

        // Проверяем, что данные действительно изменились
        assertEquals(2.4, list.getY(0), 1e-9);
        assertEquals(2.6, list.getY(1), 1e-9);
        assertEquals(3.2, list.getY(4), 1e-9);
    }

    @Test
    void testDoSynchronouslyWithVoidOperationAndMultipleModifications() {
        // Operation<Void> - комплексная модификация
        array.doSynchronously(new SynchronizedTabulatedFunction.Operation<Void>() {
            @Override
            public Void apply(SynchronizedTabulatedFunction func) {
                // Сбрасываем все значения к 1.0
                for (int i = 0; i < func.getCount(); i++) {
                    func.setY(i, 1.0);
                }
                // Устанавливаем средний элемент в 100.0
                func.setY(func.getCount() / 2, 100.0);
                return null;
            }
        });

        assertEquals(1.0, array.getY(0), 1e-9);
        assertEquals(1.0, array.getY(1), 1e-9);
        assertEquals(100.0, array.getY(10), 1e-9); // средний элемент
        assertEquals(1.0, array.getY(13), 1e-9);
        assertEquals(1.0, array.getY(14), 1e-9);
    }

    @Test
    void testDoSynchronouslyWithLambdaExpression() {
        // Использование лямбда-выражения для Operation<Double>
        Double maxY = array.doSynchronously((SynchronizedTabulatedFunction func) -> {
            double max = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < func.getCount(); i++) {
                if (func.getY(i) > max) {
                    max = func.getY(i);
                }
            }
            return max;
        });

        assertEquals(81.0, maxY, 1e-9);
    }

    @Test
    void testDoSynchronouslyWithLambdaVoid() {
        // Использование лямбда-выражения для Operation<Void>
        list.doSynchronously((SynchronizedTabulatedFunction func) -> {
            // Инвертируем все Y значения
            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, -func.getY(i));
            }
            return null;
        });

        assertEquals(-1.2, list.getY(0), 1e-9);
        assertEquals(-1.6, list.getY(4), 1e-9);
    }

    @Test
    void testDoSynchronouslyWithComplexCalculation() {
        // Operation<Double> - сложное вычисление с использованием нескольких методов
        Double result = array.doSynchronously(new SynchronizedTabulatedFunction.Operation<Double>() {
            @Override
            public Double apply(SynchronizedTabulatedFunction func) {
                double leftValue = func.apply(func.leftBound());
                double rightValue = func.apply(func.rightBound());
                double middleValue = func.apply((func.leftBound() + func.rightBound()) / 2);
                return (leftValue + rightValue + middleValue) / 3.0;
            }
        });

        // leftBound=1.0 (y=0.0), rightBound=10.0 (y=81.0), middle=5.5 (y=20.306094182825486)
        // (81 + 20.306094182825486) / 3 = 33.76869...
        assertEquals(33.7687, result, 1e-4);
    }

   @Test
    void testDoSynchronouslyWithSearchOperation() {
        // Operation<Integer> - поиск индекса максимального элемента
        Integer maxIndex = array.doSynchronously((SynchronizedTabulatedFunction func) -> {
            int maxIndexx = 0;

            double maxValue = func.getY(0);

            for (int i = 1; i < func.getCount(); i++) {
                if (func.getY(i) > maxValue) {
                    maxValue = func.getY(i);
                    maxIndexx = i;
                }
            }
            return maxIndexx;
        });

        assertEquals(19, maxIndex); // Максимальный Y=50.0 на индексе 4
    }

    @Test
    void testDoSynchronouslyWithConditionalModification() {
        // Operation<Void> - условная модификация
        list.doSynchronously(new SynchronizedTabulatedFunction.Operation<Void>() {
            @Override
            public Void apply(SynchronizedTabulatedFunction func) {
                // Увеличиваем только четные индексы
                for (int i = 0; i < func.getCount(); i++) {
                    if (i % 2 == 0) { // четные индексы: 0, 2, 4
                        func.setY(i, func.getY(i) + 5.0);
                    }
                }
                return null;
            }
        });

        assertEquals(6.2, list.getY(0), 1e-9);
        assertEquals(1.3, list.getY(1), 1e-9); // без изменений
        assertEquals(6.4, list.getY(2), 1e-9);
        assertEquals(1.4, list.getY(3), 1e-9); // без изменений
        assertEquals(6.6, list.getY(4), 1e-9);
    }

    @Test
    void testDoSynchronouslyChainOperations() {
        // Последовательные вызовы doSynchronously
        // Первая операция: вычисление суммы
        Double sum = list.doSynchronously(func -> {
            double total = 0;
            for (int i = 0; i < func.getCount(); i++) {
                total += func.getY(i);
            }
            return total;
        });

        // Вторая операция: модификация
        list.doSynchronously(func -> {
            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, func.getY(i) / sum);
            }
            return null;
        });

        // Проверяем нормализацию
        double normalizedSum = list.doSynchronously(func -> {
            double total = 0;
            for (int i = 0; i < func.getCount(); i++) {
                total += func.getY(i);
            }
            return total;
        });

        assertEquals(1.0, normalizedSum, 1e-5); // Сумма нормализованных значений должна быть 1.0
    }
}