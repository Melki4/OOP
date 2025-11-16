package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PointsSortingPerformanceTest {

    private static final int POINTS_COUNT = 10000;
    private static final int FUNCTION_COUNT = 5;
    private static final Random random = new Random();

    @Test
    void testSortingPerformanceAndGenerateReport() {
        JdbcSimpleFunctionRepository simpleRepo = new JdbcSimpleFunctionRepository();
        JdbcUserRepository userRepo = new JdbcUserRepository();
        JdbcMathFunctionRepository mathRepo = new JdbcMathFunctionRepository();
        JdbcPointRepository pointRepo = new JdbcPointRepository();

        // Инициализация
        simpleRepo.createTable();
        userRepo.createTable();
        mathRepo.createTable();
        pointRepo.createTable();

        simpleRepo.addSimpleFunction("PerfFunc", "Функция для тестирования");
        userRepo.addUser("perfUser", "perfLogin", "perfPass", "user");
        int userId = userRepo.selectIdByLogin("perfLogin");

        // Создаем математические функции
        MathFunction[] mathFunctions = {
                x -> x * x + 2 * x + 1,           // Квадратичная
                x -> Math.sin(x) * 10,            // Синус
                x -> Math.log(Math.abs(x) + 1) * 5, // Логарифмическая
                x -> Math.exp(x * 0.01),          // Экспоненциальная
                x -> x * x * x - 3 * x + 2        // Кубическая
        };

        double[][] ranges = {
                {-100, 100},
                {-Math.PI * 2, Math.PI * 2},
                {0.1, 200},
                {-50, 50},
                {-5, 5}
        };

        String[] names = {
                "x^2+2x+1",
                "10*sin(x)",
                "5*ln(|x|+1)",
                "exp(x/100)",
                "x^3-3x+2"
        };

        int[] functionIds = new int[FUNCTION_COUNT];

        // Создаем функции в БД
        for (int i = 0; i < FUNCTION_COUNT; i++) {
            mathRepo.addMathFunction(names[i], POINTS_COUNT, ranges[i][0], 1, userId, "PerfFunc");
        }

        // Получаем ID функций
        List<String> functions = mathRepo.selectAllMathFunctions();
        for (int i = 0; i < FUNCTION_COUNT; i++) {
            functionIds[i] = Integer.parseInt(functions.get(i).split(" ")[0]);
        }

        // Массивы для хранения времени выполнения
        long[] insertTimes = new long[FUNCTION_COUNT];
        long[] sortedSelectTimes = new long[FUNCTION_COUNT];
        long[] unsortedSelectTimes = new long[FUNCTION_COUNT];
        long[] sortedDTOSelectTimes = new long[FUNCTION_COUNT];

        // Тестируем каждую функцию
        for (int i = 0; i < FUNCTION_COUNT; i++) {
            System.out.printf("\n=== Тестирование функции %d: %s ===%n", i + 1, names[i]);

            // Создаем и перемешиваем точки
            ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(
                    mathFunctions[i], ranges[i][0], ranges[i][1], POINTS_COUNT);

            List<Point> points = new ArrayList<>();
            for (int j = 0; j < arrayFunc.getCount(); j++) {
                points.add(new Point(arrayFunc.getX(j), arrayFunc.getY(j)));
            }

            Collections.shuffle(points, random);

            // Вставка точек
            long insertStart = System.currentTimeMillis();
            pointRepo.bulkInsertPointsDirect(points, functionIds[i]);
            long insertEnd = System.currentTimeMillis();
            insertTimes[i] = insertEnd - insertStart;

            // Выборка без сортировки
            long unsortedStart = System.currentTimeMillis();
            pointRepo.selectPointsByFunctionId(functionIds[i]);
            long unsortedEnd = System.currentTimeMillis();
            unsortedSelectTimes[i] = unsortedEnd - unsortedStart;

            // Выборка с сортировкой
            long sortedStart = System.currentTimeMillis();
            pointRepo.selectPointsByFunctionIdSorted(functionIds[i]);
            long sortedEnd = System.currentTimeMillis();
            sortedSelectTimes[i] = sortedEnd - sortedStart;

            // Выборка DTO с сортировкой
            long sortedDTOStart = System.currentTimeMillis();
            pointRepo.selectPointsByFunctionIdSortedAsDTO(functionIds[i]);
            long sortedDTOEnd = System.currentTimeMillis();
            sortedDTOSelectTimes[i] = sortedDTOEnd - sortedDTOStart;

            System.out.printf("Вставка: %d мс, Выборка без сортировки: %d мс, Выборка с сортировкой: %d мс, DTO с сортировкой: %d мс%n",
                    insertTimes[i], unsortedSelectTimes[i], sortedSelectTimes[i], sortedDTOSelectTimes[i]);
        }

        // Создаем Excel отчет
        createExcelReport(insertTimes, unsortedSelectTimes, sortedSelectTimes, sortedDTOSelectTimes, names);

        // Очистка
        pointRepo.deleteAllPoints();
        mathRepo.deleteAllFunctions();
        simpleRepo.deleteAllFunctions();
        userRepo.deleteAllUsers();

        System.out.println("\n=== Тестирование завершено ===");
    }

    private void createExcelReport(long[] insertTimes, long[] unsortedSelectTimes,
                                   long[] sortedSelectTimes, long[] sortedDTOSelectTimes,
                                   String[] functionNames) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Производительность сортировки");

            // Стили для заголовков
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Создаем заголовки
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Функция", "Вставка (мс)", "Выборка без сортировки (мс)",
                    "Выборка с сортировкой (мс)", "DTO с сортировкой (мс)",
                    "Разница сортировка/несортировка (мс)"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Заполняем данные
            for (int i = 0; i < FUNCTION_COUNT; i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(functionNames[i]);
                row.createCell(1).setCellValue(insertTimes[i]);
                row.createCell(2).setCellValue(unsortedSelectTimes[i]);
                row.createCell(3).setCellValue(sortedSelectTimes[i]);
                row.createCell(4).setCellValue(sortedDTOSelectTimes[i]);
                row.createCell(5).setCellValue(sortedSelectTimes[i] - unsortedSelectTimes[i]);
            }

            // Итоговая строка
            Row totalRow = sheet.createRow(FUNCTION_COUNT + 1);
            totalRow.createCell(0).setCellValue("СРЕДНЕЕ");
            totalRow.createCell(1).setCellValue(average(insertTimes));
            totalRow.createCell(2).setCellValue(average(unsortedSelectTimes));
            totalRow.createCell(3).setCellValue(average(sortedSelectTimes));
            totalRow.createCell(4).setCellValue(average(sortedDTOSelectTimes));
            totalRow.createCell(5).setCellValue(average(sortedSelectTimes) - average(unsortedSelectTimes));

            // Авто-размер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Сохраняем файл
            try (FileOutputStream out = new FileOutputStream("Sorting_Performance_Report.xlsx")) {
                workbook.write(out);
            }

            System.out.println("Отчет сохранен в файл: Sorting_Performance_Report.xlsx");

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании Excel отчета", e);
        }
    }

    private double average(long[] values) {
        long sum = 0;
        for (long value : values) {
            sum += value;
        }
        return (double) sum / values.length;
    }
}