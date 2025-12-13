package ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.FunctionUtils;
import com.vaadin.flow.server.VaadinSession;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FunctionCreationDialog {
    private final Dialog dialog;
    private Consumer<TabulatedFunction> createHandler;

    public FunctionCreationDialog() {
        dialog = new Dialog();
    }

    public void open(Consumer<TabulatedFunction> handler) {
        this.createHandler = handler;
        dialog.removeAll();

        dialog.setHeaderTitle("Создание функции");

        TextField nameField = new TextField("Имя функции");
        nameField.setRequired(true);

        TextField leftBorderField = new TextField("Левая граница");
        leftBorderField.setRequired(true);
        leftBorderField.setValue("-10");

        TextField rightBorderField = new TextField("Правая граница");
        rightBorderField.setRequired(true);
        rightBorderField.setValue("10");

        IntegerField pointsCountField = new IntegerField("Количество точек");
        pointsCountField.setMin(2);
        pointsCountField.setMax(100);
        pointsCountField.setValue(10);

        Button createBtn = new Button("Создать", e -> {
            try {
                double left = Double.parseDouble(leftBorderField.getValue());
                double right = Double.parseDouble(rightBorderField.getValue());
                int pointsCount = pointsCountField.getValue();

                if (pointsCount < 2) {
                    Notification.show("Минимум 2 точки", 3000, Notification.Position.MIDDLE);
                    return;
                }

                if (left >= right) {
                    Notification.show("Левая граница должна быть меньше правой", 3000, Notification.Position.MIDDLE);
                    return;
                }

                List<Point> points = new ArrayList<>();
                double step = (right - left) / (pointsCount - 1);

                for (int i = 0; i < pointsCount; i++) {
                    double x = left + i * step;
                    points.add(new Point(x, 0.0));
                }

                TabulatedFunctionFactory factory = FunctionUtils.getUserFunctionFactory(
                        (String) VaadinSession.getCurrent().getAttribute("login"));
                TabulatedFunction function = factory.create(
                        points.stream().mapToDouble(Point::getX).toArray(),
                        points.stream().mapToDouble(Point::getY).toArray()
                );

                if (createHandler != null) {
                    createHandler.accept(function);
                }

                dialog.close();
                Notification.show("Функция создана успешно", 3000, Notification.Position.MIDDLE);
            } catch (NumberFormatException ex) {
                Notification.show("Введите корректные числа", 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancelBtn = new Button("Отмена", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(
                nameField, leftBorderField, rightBorderField,
                pointsCountField, createBtn, cancelBtn
        );
        dialog.add(dialogLayout);
        dialog.open();
    }
}