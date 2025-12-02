package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcMathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcPointRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class createAComparebleTable {

    public void testWithATF(){
        JdbcSimpleFunctionRepository JdbcSimpleFunctionRepository = new JdbcSimpleFunctionRepository();
        JdbcSimpleFunctionRepository.createTable();

        JdbcUserRepository JdbcUserRepository = new JdbcUserRepository();
        JdbcUserRepository.createTable();

        JdbcMathFunctionRepository m = new JdbcMathFunctionRepository();
        m.createTable();

        JdbcPointRepository JdbcPointRepository = new JdbcPointRepository();
        JdbcPointRepository.createTable();

        // таблица функций нужна, т.к. в points есть внешний ключ
        JdbcSimpleFunctionRepository.createSimpleFunction("Квадратичная ф-ция");

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

        JdbcUserRepository.createUser("array", "login", "hardpassword", "user");
        int user_id = JdbcUserRepository.selectIdByLogin("login");

        m.createMathFunction("x^2+2x+1_0", 10000, -100.0,
                1, user_id, "Квадратичная ф-ция");
        m.createMathFunction("x^2+2x+1_1", 20000, -100.0,
                1, user_id, "Квадратичная ф-ция");
        m.createMathFunction("x^2+2x+1_2", 40000, -100.0,
                1, user_id, "Квадратичная ф-ция");
        m.createMathFunction("x^2+2x+1_3", 80000, -100.0,
                1, user_id, "Квадратичная ф-ция");
        m.createMathFunction("x^2+2x+1_4", 100000, -100.0,
                1, user_id, "Квадратичная ф-ция");

        int function_id1 = m.findMathFunctionsByName("x^2+2x+1_0").getFirst().getFunctionId();
        int function_id2 = m.findMathFunctionsByName("x^2+2x+1_1").getFirst().getFunctionId();
        int function_id3 = m.findMathFunctionsByName("x^2+2x+1_2").getFirst().getFunctionId();
        int function_id4 = m.findMathFunctionsByName("x^2+2x+1_3").getFirst().getFunctionId();
        int function_id5 = m.findMathFunctionsByName("x^2+2x+1_4").getFirst().getFunctionId();

        long startTime1 = System.currentTimeMillis();
        JdbcPointRepository.addManyPoints(points1, function_id1);
        long endTime1 = System.currentTimeMillis();

        long startTime2 = System.currentTimeMillis();
        JdbcPointRepository.addManyPoints(points2, function_id2);
        long endTime2 = System.currentTimeMillis();

        long startTime3 = System.currentTimeMillis();
        JdbcPointRepository.addManyPoints(points3, function_id3);
        long endTime3 = System.currentTimeMillis();

        long startTime4 = System.currentTimeMillis();
        JdbcPointRepository.addManyPoints(points4, function_id4);
        long endTime4 = System.currentTimeMillis();

        long startTime5 = System.currentTimeMillis();
        JdbcPointRepository.addManyPoints(points5, function_id5);
        long endTime5 = System.currentTimeMillis();

        /*------------------------------------------------------------------*/

        long startTime1_1 = System.currentTimeMillis();
        JdbcPointRepository.findPointsByFunctionId(m.findMathFunctionIdComplex(-100.0,
                1, 100000, "x^2+2x+1_4"));
        long endTime1_1 = System.currentTimeMillis();

        /*------------------------------------------------------------------*/

        long startTime1_2 = System.currentTimeMillis();
        JdbcPointRepository.findPointsByFunctionId(function_id1);
        long endTime1_2 = System.currentTimeMillis();

        long startTime2_2 = System.currentTimeMillis();
        JdbcPointRepository.findPointsByFunctionId(function_id2);
        long endTime2_2 = System.currentTimeMillis();

        long startTime3_2 = System.currentTimeMillis();
        JdbcPointRepository.findPointsByFunctionId(function_id3);
        long endTime3_2 = System.currentTimeMillis();

        long startTime4_2 = System.currentTimeMillis();
        JdbcPointRepository.findPointsByFunctionId(function_id4);
        long endTime4_2 = System.currentTimeMillis();

        long startTime5_2 = System.currentTimeMillis();
        JdbcPointRepository.findPointsByFunctionId(function_id5);
        long endTime5_2 = System.currentTimeMillis();

        /*------------------------------------------------------------------*/

        long startTime1_3 = System.currentTimeMillis();
        for (int i = 0; i< 10;++i){
            double y = array1.getY(i);
            JdbcPointRepository.updateYValueByFunctionIdAndOldY(y, m.findMathFunctionIdComplex(-100.0,
                    1, 10000, "x^2+2x+1_0"), 1.1);
        }
        long endTime1_3 = System.currentTimeMillis();

        long startTime2_3 = System.currentTimeMillis();
        for (int i = 0; i< 10;++i){
            double y = array1.getY(i);
            JdbcPointRepository.updateYValueByFunctionIdAndOldY(y, m.findMathFunctionIdComplex(-100.0,
                    1, 20000, "x^2+2x+1_1"), 1.1);
        }
        long endTime2_3 = System.currentTimeMillis();

        long startTime3_3 = System.currentTimeMillis();
        for (int i = 0; i< 10;++i){
            double y = array1.getY(i);
            JdbcPointRepository.updateYValueByFunctionIdAndOldY(y, m.findMathFunctionIdComplex(-100.0,
                    1, 40000, "x^2+2x+1_2"), 1.1);
        }
        long endTime3_3 = System.currentTimeMillis();

        long startTime4_3 = System.currentTimeMillis();
        for (int i = 0; i< 10;++i){
            double y = array1.getY(i);
            JdbcPointRepository.updateYValueByFunctionIdAndOldY(y, m.findMathFunctionIdComplex(-100.0,
                    1, 80000, "x^2+2x+1_3"), 1.1);
        }
        long endTime4_3 = System.currentTimeMillis();

        long startTime5_3 = System.currentTimeMillis();
        for (int i = 0; i< 10;++i){
            double y = array1.getY(i);
            JdbcPointRepository.updateYValueByFunctionIdAndOldY(y, m.findMathFunctionIdComplex(-100.0,
                    1, 100000, "x^2+2x+1_4"), 1.1);
        }
        long endTime5_3 = System.currentTimeMillis();

        /*------------------------------------------------------------------*/

        long startTime1_4 = System.currentTimeMillis();
        JdbcPointRepository.deleteAllPoints();
        long endTime1_4 = System.currentTimeMillis();

        long startTime2_4 = System.currentTimeMillis();
        JdbcPointRepository.deleteAllPoints();
        long endTime2_4 = System.currentTimeMillis();

        long startTime3_4 = System.currentTimeMillis();
        JdbcPointRepository.deleteAllPoints();
        long endTime3_4 = System.currentTimeMillis();

        long startTime4_4 = System.currentTimeMillis();
        JdbcPointRepository.deleteAllPoints();
        long endTime4_4 = System.currentTimeMillis();

        long startTime5_4 = System.currentTimeMillis();
        JdbcPointRepository.deleteAllPoints();
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
            row1.createCell(4).setCellValue("Обновление 10 знач.");
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
            row3.createCell(4).setCellValue(endTime2_3-startTime2_3);
            row3.createCell(5).setCellValue(endTime2_4-startTime2_4);

            row4.createCell(0).setCellValue("40k элементов");
            row4.createCell(1).setCellValue(endTime3-startTime3);
            row4.createCell(2).setCellValue(endTime1_1-startTime1_1);//одинаковое везде
            row4.createCell(3).setCellValue(endTime3_2-startTime3_2);
            row4.createCell(4).setCellValue(endTime3_3-startTime3_3);
            row4.createCell(5).setCellValue(endTime3_4-startTime3_4);

            row5.createCell(0).setCellValue("80k элементов");
            row5.createCell(1).setCellValue(endTime4-startTime4);
            row5.createCell(2).setCellValue(endTime1_1-startTime1_1);//одинаковое везде
            row5.createCell(3).setCellValue(endTime4_2-startTime4_2);
            row5.createCell(4).setCellValue(endTime4_3-startTime4_3);
            row5.createCell(5).setCellValue(endTime4_4-startTime4_4);

            row6.createCell(0).setCellValue("100k элементов");
            row6.createCell(1).setCellValue(endTime5-startTime5);
            row6.createCell(2).setCellValue(endTime1_1-startTime1_1);//одинаковое везде
            row6.createCell(3).setCellValue(endTime5_2-startTime5_2);
            row6.createCell(4).setCellValue(endTime5_3-startTime5_3);
            row6.createCell(5).setCellValue(endTime5_4-startTime5_4);

            try (FileOutputStream out = new FileOutputStream("New_Table3.xlsx")) {
                workbook.write(out);
            }  // Работа с файлом завершена, он закрыт
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JdbcPointRepository.deleteAllPoints();
        JdbcSimpleFunctionRepository.deleteAllFunctions();
        m.deleteAllFunctions();
        JdbcUserRepository.deleteAllUsers();
        System.out.println("Done");
    }

    void fillArrays(ArrayTabulatedFunction arrayTabulatedFunction, List<Point> list){
        for (int i = 0; i< arrayTabulatedFunction.getCount(); ++i){
            Point p = new Point(arrayTabulatedFunction.getX(i), arrayTabulatedFunction.getY(i));
            list.add(p);
        }
    }
}
