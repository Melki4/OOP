package ru.ssau.tk._repfor2lab_._OOP_.functions;

import java.util.Arrays;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction{//нет реализации отрицательный индекс - начинаем с хвоста

    Node head = null;
    private int count;

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues){
        for (int i=0;i<xValues.length;++i){
            addNode(xValues[i], yValues[i]);
        }
        count = xValues.length;
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count){

        if (xFrom > xTo) {
            double boof = xFrom;
            xFrom = xTo;
            xTo = boof;
        }

        if (xFrom!=xTo){
            //double curX = xFrom;
            if (count == 1){
                addNode(xFrom, source.apply(xFrom));
            }
            else {
                int UpLim = count - 1;
                double step = (xTo - xFrom) / (UpLim);

                for (int i = 0; i < UpLim; ++i) {
                    addNode(xFrom, source.apply(xFrom));
                    xFrom += step;
                }
                addNode(xTo, source.apply(xTo));
            }
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
        if (count == 0) addNode(x, y);
        else if (indexOfX(x)!= -1) {
            setY(indexOfX(x), y);
        }
        else if (x < leftBound()) {

            Node boof = new Node();

            boof.y = y;
            boof.x = x;

            boof.prev = head.prev;
            boof.next = head;

            head.prev.next = boof;
            head.prev = boof;

            head = boof;

            count++;

        }
        else {

            int index = floorIndexOfX(x);

            Node boof = new Node();

            boof.y = y;
            boof.x = x;

            boof.prev = getNode(index);
            boof.next = getNode(index+1);

            getNode(index+1).prev = boof;
            getNode(index).next = boof;

            count++;
        }

    }

    @Override
    public void remove(int indexDelX) {//нет реализации отрицательный индекс - начинаем с хвоста
        if (count == 0 || indexDelX < 0 || indexDelX >= count){
            return;
        }
        else if (count == 1) {
            head.prev=head.next=head=null;
            count = 0;
        }
        else if (indexDelX == 0){
            head.prev.next=head.next;
            head.next.prev = head.prev;
            head = head.next;
            count--;
        }
        else if (indexDelX == count-1){
            head.prev.prev.next=head;
            head.prev = head.prev.prev;

            count--;
        }
        else{
//            System.out.println(getNode(indexDelX).x);
            getNode(indexDelX-1).next = getNode(indexDelX+1);
            getNode(indexDelX+1).prev = getNode(indexDelX-1);
//            System.out.println(getNode(indexDelX).x);
            count--;
        }
    }

    private void addNode(double x, double y) {
        if (head == null){
            head = new Node();

            head.next = head;
            head.prev = head;
            head.y = y;

            head.x = x;
            count = 1;
        }
        else if (count == 1) {
            Node boof = new Node();

            boof.y = y;
            boof.x = x;

            boof.prev = head;
            boof.next = head;
            head.prev = boof;
            head.next= boof;
            count+=1;
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
    }

    protected Node getNode(int index){//индекс больше count - ошибка
        Node toReturn = head;
        for (int i =0; i<index; ++i){
            toReturn = toReturn.next;
        }
        return toReturn;
    }

    @Override
    protected double extrapolateRight(double newX) {
        if (count == 1) return head.y;
        double x = rightBound();
        double y = getY(indexOfX(x));

        double lowerX = getX(indexOfX(x)-1);
        double lowerY = getY(indexOfX(lowerX));

        return lowerY + (newX - lowerX)/(x-lowerX)*(y-lowerY);
    }

    @Override
    protected double extrapolateLeft(double x) {
        if (count == 1) return head.y;
        double leftX = leftBound();
        double leftY = getY(indexOfX(leftX));

        double rightX = getX(indexOfX(leftX)+1);
        double rightY = getY(indexOfX(rightX));

        return leftY + (x - leftX)/(rightX- leftX)*(rightY- leftY);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        double leftX = getX(floorIndex);
        double leftY = getY(floorIndex);
        double rightX = getX(floorIndex+1);
        double rightY = getY(floorIndex+1);

        return interpolate(x, leftX, rightX, leftY, rightY);
    }

    protected double interpolate(double x){
        return interpolate(x, floorIndexOfX(x));
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        if (count == 1) return head.y;
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
        getNode(index).y = value;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getY(int index) {
        return getNode(index).y;
    }

    @Override
    public double getX(int index) {
        return getNode(index).x;
    }
}
