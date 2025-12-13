package ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.io.FunctionsIO;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.FunctionUtils;

import java.io.*;
import java.util.function.Consumer;

public class FunctionFileUploadDialog {
    private final Dialog dialog;
    private Consumer<TabulatedFunction> loadHandler;
    private String currentFormat;

    public FunctionFileUploadDialog() {
        dialog = new Dialog();
    }

    public void open(String format, Consumer<TabulatedFunction> handler) {
        this.loadHandler = handler;
        this.currentFormat = format;
        dialog.removeAll();

        dialog.setHeaderTitle("Загрузить функцию из " + format.toUpperCase());

        // Поле для ввода имени файла
        TextField fileNameField = new TextField("Имя файла");
        fileNameField.setPlaceholder("Введите имя файла");
        fileNameField.setRequired(true);

        // Кнопка загрузки
        Button loadButton = new Button("Загрузить", e -> {
            String fileName = fileNameField.getValue().trim();
            if (fileName.isEmpty()) {
                Notification.show("Введите имя файла", 3000, Notification.Position.MIDDLE);
                return;
            }
            // Добавляем расширение, если его нет
            if (!fileName.toLowerCase().endsWith("." + format.toLowerCase())) {
                fileName += "." + format.toLowerCase();
            }
            loadFunctionFromFile(fileName);
        });
        loadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Кнопка отмены
        Button cancelButton = new Button("Отмена", e -> dialog.close());

        // Кнопки управления
        HorizontalLayout buttonLayout = new HorizontalLayout(loadButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Собираем интерфейс диалога
        VerticalLayout dialogLayout = new VerticalLayout(
                new Span("Введите имя файла для загрузки:"),
                fileNameField,
                buttonLayout
        );
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        dialog.add(dialogLayout);
        dialog.setWidth("500px");
        dialog.open();
    }

    private void loadFunctionFromFile(String fileName) {
        try {
            // Получаем фабрику пользователя
            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            TabulatedFunctionFactory factory = FunctionUtils.getUserFunctionFactory(login);

            // Читаем файл
            InputStream inputStream = getClass().getResourceAsStream("/files/" + fileName);
            if (inputStream == null) {
                Notification.show("Файл " + fileName + " не найден", 5000, Notification.Position.MIDDLE);
                return;
            }

            TabulatedFunction loadedFunction = null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                if ("json".equals(currentFormat)) {
                    // Загрузка из JSON
                    ArrayTabulatedFunction arrayFunc = FunctionsIO.deserializeJson(reader);

                    double[] x = new double[arrayFunc.getCount()];
                    double[] y = new double[arrayFunc.getCount()];

                    for (int i =0; i< arrayFunc.getCount(); ++i){
                        x[i] = arrayFunc.getX(i);
                        y[i] = arrayFunc.getY(i);
                    }

                    loadedFunction = factory.create(
                            x,
                           y
                    );
                } else if ("xml".equals(currentFormat)) {
                    // Загрузка из XML
                    ArrayTabulatedFunction arrayFunc = FunctionsIO.deserializeXml(reader);

                    double[] x = new double[arrayFunc.getCount()];
                    double[] y = new double[arrayFunc.getCount()];

                    for (int i =0; i< arrayFunc.getCount(); ++i){
                        x[i] = arrayFunc.getX(i);
                        y[i] = arrayFunc.getY(i);
                    }

                    loadedFunction = factory.create(
                            x,
                            y
                    );
                }
            }

            if (loadedFunction != null && loadHandler != null) {
                loadHandler.accept(loadedFunction);
                dialog.close();
                Notification.show("Функция успешно загружена из " + fileName, 3000, Notification.Position.MIDDLE);
            }

        } catch (Exception ex) {
            Notification.show("Ошибка загрузки: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            ex.printStackTrace();
        }
    }
}