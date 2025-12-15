package ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation;

import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedFunctionOperationService;

public class FunctionOperationsPresenter {
    private final TabulatedFunctionOperationService operationService;

    public FunctionOperationsPresenter(TabulatedFunctionFactory factory) {
        this.operationService = new TabulatedFunctionOperationService(factory);
    }

    public TabulatedFunction performOperation(TabulatedFunction first, TabulatedFunction second, String operation) {
        return switch (operation) {
            case "Сложение" -> operationService.addition(first, second);
            case "Вычитание" -> operationService.subtraction(first, second);
            case "Умножение" -> operationService.multiplication(first, second);
            case "Деление" -> operationService.division(first, second);
            default -> throw new IllegalArgumentException("Неизвестная операция: " + operation);
        };
    }
}