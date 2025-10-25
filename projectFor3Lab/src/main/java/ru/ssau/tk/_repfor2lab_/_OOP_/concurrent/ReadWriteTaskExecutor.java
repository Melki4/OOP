package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.ConstantFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class ReadWriteTaskExecutor {
    public static void main(String[] args){
        TabulatedFunction function = new LinkedListTabulatedFunction(new ConstantFunction(-1.0), 1.0, 1000.0, 1000);
        ReadTask readTask = new ReadTask(function);
        WriteTask writeTask = new WriteTask(function, 0.5);

        Thread readThread = new Thread(readTask, "ReadThread");
        Thread writeThread = new Thread(writeTask, "WriteThread");

        readThread.start();
        writeThread.start();
    }
}
