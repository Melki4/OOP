package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class createAComparebleTable {

    public void testWithATF(){

        pointsInterface pointsInterface = new pointsInterface();
        simpleFunctionInterface simpleFunctionInterface = new simpleFunctionInterface();

        // таблица функций нужна, т.к. в points есть внешний ключ
        simpleFunctionInterface.createTable();
        simpleFunctionInterface.addSimpleFunction("SqrFunc", "Квадратичная ф-ция");
        pointsInterface.createTable();

        MathFunction mathFunction = (double x)-> x*x + 2*x +1;

        var array1 = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 10000);
        var array2 = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 20000);
        var array3 = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 40000);
        var array4 = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 80000);
        var array5 = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 100000);

        List<Point> points1 = new ArrayList<>();
        List<Point> points2 = new ArrayList<>();
        List<Point> points3 = new ArrayList<>();
        List<Point> points4 = new ArrayList<>();
        List<Point> points5 = new ArrayList<>();

        fillArrays(array1, points1);
        fillArrays(array2, points2);
        fillArrays(array3, points3);
        fillArrays(array4, points4);
        fillArrays(array5, points5);

        userInterface userInterface = new userInterface();

        userInterface.addUser("array", "login", "hardpassword", "user");
        int user_id = userInterface.selectIdByLogin("login");

        mathFunctionsInterface m = new mathFunctionsInterface();
        m.addMathFunction("x^2+2x+1", 10000, -100.0,
                1, user_id, "SqrFunc");
        m.addMathFunction("x^2+2x+1", 20000, -100.0,
                1, user_id, "SqrFunc");
        m.addMathFunction("x^2+2x+1", 40000, -100.0,
                1, user_id, "SqrFunc");
        m.addMathFunction("x^2+2x+1", 80000, -100.0,
                1, user_id, "SqrFunc");
        m.addMathFunction("x^2+2x+1", 100000, -100.0,
                1, user_id, "SqrFunc");

        int function_id1 = Integer.parseInt(m.selectAllMathFunctions().get(0).split(" ")[0]);
        int function_id2 = Integer.parseInt(m.selectAllMathFunctions().get(1).split(" ")[0]);
        int function_id3 = Integer.parseInt(m.selectAllMathFunctions().get(2).split(" ")[0]);
        int function_id4 = Integer.parseInt(m.selectAllMathFunctions().get(3).split(" ")[0]);
        int function_id5 = Integer.parseInt(m.selectAllMathFunctions().get(4).split(" ")[0]);

        long startTime1 = System.currentTimeMillis();
        pointsInterface.bulkInsertPointsDirect(points1, function_id1);
        long endTime1 = System.currentTimeMillis();

        long startTime2 = System.currentTimeMillis();
        pointsInterface.bulkInsertPointsDirect(points2, function_id2);
        long endTime2 = System.currentTimeMillis();

        long startTime3 = System.currentTimeMillis();
        pointsInterface.bulkInsertPointsDirect(points3, function_id3);
        long endTime3 = System.currentTimeMillis();

        long startTime4 = System.currentTimeMillis();
        pointsInterface.bulkInsertPointsDirect(points4, function_id4);
        long endTime4 = System.currentTimeMillis();

        long startTime5 = System.currentTimeMillis();
        pointsInterface.bulkInsertPointsDirect(points5, function_id5);
        long endTime5 = System.currentTimeMillis();

        /*------------------------------------------------------------------*/

        long startTime1_1 = System.currentTimeMillis();
        pointsInterface.selectAllPoints();
        long endTime1_1 = System.currentTimeMillis();

        /*------------------------------------------------------------------*/

        long startTime1_2 = System.currentTimeMillis();
        pointsInterface.selectPointsByFunctionId(function_id1);
        long endTime1_2 = System.currentTimeMillis();

        long startTime2_2 = System.currentTimeMillis();
        pointsInterface.selectPointsByFunctionId(function_id2);
        long endTime2_2 = System.currentTimeMillis();

        long startTime3_2 = System.currentTimeMillis();
        pointsInterface.selectPointsByFunctionId(function_id3);
        long endTime3_2 = System.currentTimeMillis();

        long startTime4_2 = System.currentTimeMillis();
        pointsInterface.selectPointsByFunctionId(function_id4);
        long endTime4_2 = System.currentTimeMillis();

        long startTime5_2 = System.currentTimeMillis();
        pointsInterface.selectPointsByFunctionId(function_id5);
        long endTime5_2 = System.currentTimeMillis();

        /*------------------------------------------------------------------*/

        long startTime1_3 = System.currentTimeMillis();
        for (int i = 0; i< array1.getCount();++i){
            double x = array1.getX(i);
            int index_in_table = pointsInterface.selectPointIdByXValueAndFunctionId(x, function_id1);
            pointsInterface.updateYValueById(1.1, index_in_table);
        }
        long endTime1_3 = System.currentTimeMillis();

        /*------------------------------------------------------------------*/

        long startTime1_4 = System.currentTimeMillis();
        pointsInterface.deleteAllPoints();
        long endTime1_4 = System.currentTimeMillis();

        long startTime2_4 = System.currentTimeMillis();
        pointsInterface.deleteAllPoints();
        long endTime2_4 = System.currentTimeMillis();

        long startTime3_4 = System.currentTimeMillis();
        pointsInterface.deleteAllPoints();
        long endTime3_4 = System.currentTimeMillis();

        long startTime4_4 = System.currentTimeMillis();
        pointsInterface.deleteAllPoints();
        long endTime4_4 = System.currentTimeMillis();

        long startTime5_4 = System.currentTimeMillis();
        pointsInterface.deleteAllPoints();
        long endTime5_4 = System.currentTimeMillis();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Example");

            Row row1 = sheet.createRow(1);
            Row row2 = sheet.createRow(2);
            Row row3 = sheet.createRow(3);
            Row row4 = sheet.createRow(4);
            Row row5 = sheet.createRow(5);
            Row row6 = sheet.createRow(6);

            row1.createCell(1).setCellValue("Вставка значений");
            row1.createCell(2).setCellValue("Чтение всего");
            row1.createCell(3).setCellValue("Чтение по ф-ции");
            row1.createCell(4).setCellValue("Обновление");
            row1.createCell(5).setCellValue("Удаление");

            row2.createCell(0).setCellValue("10k элементов");
            row2.createCell(1).setCellValue(endTime1-startTime1);
            row2.createCell(2).setCellValue(endTime1_1-startTime1_1);//одинаковое везде
            row2.createCell(3).setCellValue(endTime1_2-startTime1_2);
            row2.createCell(4).setCellValue(endTime1_3-startTime1_3);
            row2.createCell(5).setCellValue(endTime1_4-startTime1_4);

            row3.createCell(0).setCellValue("20k элементов");
            row3.createCell(1).setCellValue(endTime2-startTime2);
            row3.createCell(2).setCellValue(endTime1_1-startTime1_1);//одинаковое везде
            row3.createCell(3).setCellValue(endTime2_2-startTime2_2);
            row3.createCell(4).setCellValue(0);
            row3.createCell(5).setCellValue(endTime2_4-startTime2_4);

            row4.createCell(0).setCellValue("40k элементов");
            row4.createCell(1).setCellValue(endTime3-startTime3);
            row4.createCell(2).setCellValue(endTime1_1-startTime1_1);//одинаковое везде
            row4.createCell(3).setCellValue(endTime3_2-startTime3_2);
            row4.createCell(4).setCellValue(0);
            row4.createCell(5).setCellValue(endTime3_4-startTime3_4);

            row5.createCell(0).setCellValue("80k элементов");
            row5.createCell(1).setCellValue(endTime4-startTime4);
            row5.createCell(2).setCellValue(endTime1_1-startTime1_1);//одинаковое везде
            row5.createCell(3).setCellValue(endTime4_2-startTime4_2);
            row5.createCell(4).setCellValue(0);
            row5.createCell(5).setCellValue(endTime4_4-startTime4_4);

            row6.createCell(0).setCellValue("100k элементов");
            row6.createCell(1).setCellValue(endTime5-startTime5);
            row6.createCell(2).setCellValue(endTime1_1-startTime1_1);//одинаковое везде
            row6.createCell(3).setCellValue(endTime5_2-startTime5_2);
            row6.createCell(4).setCellValue(0);
            row6.createCell(5).setCellValue(endTime5_4-startTime5_4);

            try (FileOutputStream out = new FileOutputStream("Example.xlsx")) {
                workbook.write(out);
            }  // Работа с файлом завершена, он закрыт
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pointsInterface.deleteAllPoints();
        simpleFunctionInterface.deleteAllFunctions();
        userInterface.deleteAllUsers();

        System.out.println("Done");
    }

    void fillArrays(ArrayTabulatedFunction arrayTabulatedFunction, List<Point> list){
        for (int i = 0; i< arrayTabulatedFunction.getCount(); ++i){
            Point p = new Point(arrayTabulatedFunction.getX(i), arrayTabulatedFunction.getY(i));
            list.add(p);
        }
    }
}
