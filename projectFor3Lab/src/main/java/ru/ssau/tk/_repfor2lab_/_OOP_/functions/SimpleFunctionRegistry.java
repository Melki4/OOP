// ru.ssau.tk.repfor2lab.OOP.functions.SimpleFunctionRegistry.java
package ru.ssau.tk._repfor2lab_._OOP_.functions;

import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DaoException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реестр простых функций: связывает локализованное имя → MathFunction
 */
public class SimpleFunctionRegistry {

    private static final Map<String, MathFunction> REGISTRY = new HashMap<>();

    static {
        // Заполняем реестр актуальными функциями
        REGISTRY.put("Квадратичная функция", new SqrFunction());
        REGISTRY.put("Тождественная функция", new IdentityFunction());
        REGISTRY.put("Постоянная единичная функция", new UnitFunction());
        REGISTRY.put("Постоянная нулевая функция", new ZeroFunction());
        // Добавьте свои функции здесь
    }

    public static MathFunction getFunction(String localizedName) {
        return REGISTRY.get(localizedName);
    }

    public static boolean contains(String localizedName) {
        return REGISTRY.containsKey(localizedName);
    }

    public static Iterable<String> getAllNamesSorted() {
        return REGISTRY.keySet().stream()
                .sorted()
                .toList();
    }

    public static List<PointsDTO> CreatePoints(String localizedName, double left, double right, int amount, String type, int functionId) {
        MathFunction mathFunction = REGISTRY.get(localizedName);

        List<PointsDTO> pointsDTOS = new java.util.ArrayList<>(List.of());

        if(type.equals("array")){
            ArrayTabulatedFunction arrayTabulatedFunction = new ArrayTabulatedFunction(mathFunction, left, right, amount);
            for (var el : arrayTabulatedFunction){
                pointsDTOS.add(new PointsDTO(el.x, el.y, functionId));
            }
        } else if(type.equals("list")){
            LinkedListTabulatedFunction listTabulatedFunction = new LinkedListTabulatedFunction(mathFunction, left, right, amount);
            for (var el : listTabulatedFunction){
                pointsDTOS.add(new PointsDTO(el.x, el.y, functionId));
            }
        } else throw new IllegalArgumentException("Незнакомый тип фабрики");

        return pointsDTOS;
    }
}