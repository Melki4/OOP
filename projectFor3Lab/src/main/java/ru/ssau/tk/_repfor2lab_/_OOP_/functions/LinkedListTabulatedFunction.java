package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.InterpolationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable{//нет реализации отрицательный индекс - начинаем с хвоста
    @Serial
    private static final long serialVersionUID = -1612741369740171735L;
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkedListTabulatedFunction.class);

    public Iterator<Point> iterator(){
        return new Iterator<Point>() {
            private Node node = head;
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return currentIndex < count;
            }

            @Override
            public Point next() {
                if (!(hasNext())) {
                    LOGGER.warn("Элементов не осталось");
                    throw new NoSuchElementException("Элементов не осталось");
                }
                Point point = new Point(node.x, node.y);
                node = node.next;
                currentIndex++;
                return point;
            }
        };
    }

    static class Node implements Serializable{
        @Serial
        private static final long serialVersionUID = -7834994689110174879L;
        public Node next;
        public Node prev;
        public double x;
        public double y;
    }

    Node head;
    private int count;
    {
        head = null;
    }

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues){
        if (xValues.length < 2 || yValues.length < 2) {
            LOGGER.warn("Длина массивов меньше минимальной возможной");
            throw new IllegalArgumentException("Длина массивов меньше минимальной возможной");
        }
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);
        for (int i=0;i<xValues.length;++i){
            addNode(xValues[i], yValues[i]);
        }
        count = xValues.length;
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count){

        if (count < 2) {
            LOGGER.warn("Количество элементов меньше минимума");
            throw new IllegalArgumentException("Количество элементов меньше минимума");
        }

        if (xFrom > xTo) {
            double boof = xFrom;
            xFrom = xTo;
            xTo = boof;
        }

        if (xFrom!=xTo){
            int UpLim = count - 1;
            double step = (xTo - xFrom) / (UpLim);

            for (int i = 0; i < UpLim; ++i) {
                addNode(xFrom, source.apply(xFrom));
                xFrom += step;
            }
            addNode(xTo, source.apply(xTo));
        }

        else {
            for (int i =0; i<count;++i){
                addNode(xFrom, source.apply(xFrom));
            }
        }

        this.count = count;
    }

    @Override
    public void insert(double x, double y) {
        LOGGER.trace("Вставляем элемент");
        if (count == 0) addNode(x, y);
        else if (indexOfX(x)!= -1) {
            setY(indexOfX(x), y);
        }
        else if (x < leftBound()) {
            addNode(x,y);
            head = head.prev;
        }
        else {
            addNode(getNode(floorIndexOfX(x)), x, y);
        }
        LOGGER.trace("Закончили вставку");
    }

    @Override
    public void remove(int indexDelX) {//нет реализации отрицательный индекс - начинаем с хвоста
        LOGGER.trace("удаляем элемент из списка");
        if (indexDelX < 0 || indexDelX >= count){
            LOGGER.warn("Неверный индекс для удаления");
            throw new IllegalArgumentException("Неверный индекс для удаления");
        }
        else if (count == 1) {
            head.prev=head.next=head=null;
            count = 0;
        }
        else if (indexDelX == 0){
            getNode(count-1).next = head.next;
            getNode(1).prev = head.prev;
            head = head.next;
            count--;
        }
        else if (indexDelX == count-1){
            getNode(count-2).next=head;
            head.prev = getNode(count-2);
            count--;
        }
        else{
            getNode(indexDelX-1).next = getNode(indexDelX+1);
            getNode(indexDelX+1).prev = getNode(indexDelX-1);
            count--;
        }
        LOGGER.trace("корректно удалили");
    }

    private void addNode(double x, double y) {
        LOGGER.trace("начинаем добавление узла");
        if (head == null){
            head = new Node();

            head.next = head;
            head.prev = head;
            head.y = y;

            head.x = x;
            count = 1;
        }

        else {
            Node boof = new Node();

            boof.y = y;
            boof.x = x;

            boof.prev = head.prev;
            boof.next = head;
            head.prev.next = boof;
            head.prev = boof;
            count+=1;
        }
        LOGGER.trace("узел добавлен");
    }//вставка в конец

    private void addNode(Node node, double x, double y) {//Вставка за элементом node
        Node boof = new Node();

        boof.y = y;
        boof.x = x;

        boof.prev = node;
        boof.next = node.next;

        node.next.prev = boof;
        node.next = boof;
        count+=1;
    }

    protected Node getNode(int index){//индекс больше count - ошибка
        if (index < 0 || index >= count){
            LOGGER.warn("Неверный индекс для получения узла");
            throw new IllegalArgumentException("Неверный индекс для получения узла");
        }
        else if (index == 0) return head;
        Node toReturn = head;
        for (int i =0; i<index-1; ++i){
            toReturn = toReturn.next;
        }

        return toReturn.next;
    }

    @Override
    protected double extrapolateRight(double newX) {
        LOGGER.trace("начинаем экстраполяцию справа");
        if (count == 1) {
            LOGGER.warn("Мало аргументов для экстраполяции справа");
            throw new InterpolationException("Мало аргументов для экстраполяции");
        }
        double x = rightBound();
        double y = getY(indexOfX(x));

        double lowerX = getX(indexOfX(x)-1);
        double lowerY = getY(indexOfX(lowerX));
        LOGGER.trace("закончили экстраполяцию справа");
        return lowerY + (newX - lowerX)/(x-lowerX)*(y-lowerY);
    }

    @Override
    protected double extrapolateLeft(double x) {
        LOGGER.trace("начинаем экстраполяцию слева");
        if (count == 1) {
            LOGGER.warn("Мало аргументов для экстраполяции слева");
            throw new InterpolationException("Мало аргументов для экстраполяции");
        }
        double leftX = leftBound();
        double leftY = getY(indexOfX(leftX));

        double rightX = getX(indexOfX(leftX)+1);
        double rightY = getY(indexOfX(rightX));
        LOGGER.trace("закончили экстраполяцию слева");
        return (leftY*(rightX-x)-rightY*(leftX-x))/(rightX-leftX);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        LOGGER.trace("начинаем интерполяцию");
        if (count <= 1) {
            LOGGER.warn("Мало аргументов для интерполяции");
            throw new InterpolationException("Мало аргументов для интерполяции");
        }

        double leftX = getX(floorIndex);
        double leftY = getY(floorIndex);
        double rightX = getX(floorIndex+1);
        double rightY = getY(floorIndex+1);

        if (x > rightX || x < leftX){
            LOGGER.warn("Поступивший икс больше максимального значения или меньше минимального значения.");
            throw new InterpolationException();
        }
        LOGGER.trace("закончили интерполяцию");
        return interpolate(x, leftX, rightX, leftY, rightY);
    }

    protected double interpolate(double x){
        return interpolate(x, floorIndexOfX(x));
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        if (count <= 1) {
            LOGGER.warn("Мало аргументов для интерполяции, кол-во точек меньше двух в интерполяции для четырёх значений");
            throw new InterpolationException("Мало аргументов для интерполяции");
        }
        return super.interpolate(x, leftX, rightX, leftY, rightY);
    }

    @Override
    public int indexOfY(double y) {
        for (int i=0; i<count;++i){
            if (getY(i) == y) return i;
        }
        return -1;
    }

    @Override
    public int indexOfX(double x) {
        for (int i=0; i<count;++i){
            if (getX(i) == x) return i;
        }
        return -1;
    }

    @Override
    public double leftBound() {
        return head.x;
    }

    @Override
    public double rightBound() {
        return head.prev.x;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < head.x) {
            LOGGER.warn("Икс меньше левой границы при поиске индекса меньшего элемента");
            throw new IllegalArgumentException("Икс меньше левой границы");
        }
        for (int i=0; i<count;++i){
            if (getX(i) == x) return i;
        }
        int ind = 0;
        double max = head.x;

        for (int i=0; i<count;++i) {
            if (max < getX(i) && getX(i) < x) {
                max = getX(i);
                ind = i;
            }
        }
        return ind;
    }

    @Override
    public void setY(int index, double value) {
        if (count == 0) {
            LOGGER.warn("Обращение к пустому списку");
            throw new NullPointerException("Обращение к пустому списку");
        }
        else if (index < 0 || index >= count){
            LOGGER.warn("Неверный индекс");
            throw new IllegalArgumentException("Неверный индекс");
        }
        getNode(index).y = value;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getY(int index) {
        if (count == 0) {
            LOGGER.warn("Попытка получить значение игрек из пустого списка");
            throw new NullPointerException("Обращение к пустому списку");
        }
        else
            if (index < 0 || index >= count){
            LOGGER.warn("Неверный индекс при попытке получить значение игрек из пустого списка");
            throw new IllegalArgumentException("Неверный индекс");
        }
        return getNode(index).y;
    }

    @Override
    public double getX(int index) {
        if (index == count && count == 0) {
            LOGGER.warn("Попытка получить значение икс из пустого списка");
            throw new NullPointerException("Обращение к пустому списку");
        }
        else if (index < 0 || index >= count){
            LOGGER.warn("Неверный индекс при попытке получить значение икс из пустого списка");
            throw new IllegalArgumentException("Неверный индекс");
        }
        return getNode(index).x;
    }
}