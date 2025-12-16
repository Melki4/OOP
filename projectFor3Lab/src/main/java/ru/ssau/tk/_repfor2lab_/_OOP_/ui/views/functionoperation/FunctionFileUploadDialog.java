package ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.VaadinSession;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.io.FunctionsIO;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.FunctionUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
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

        Upload upload = createUploadComponent();

        // Кнопка отмены
        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Кнопки управления

        HorizontalLayout buttonLayout = new HorizontalLayout(upload, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Собираем интерфейс диалога
        VerticalLayout dialogLayout = new VerticalLayout(
                new Span("Выберите файл для загрузки:"),
                buttonLayout
        );
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        dialog.add(dialogLayout);
        dialog.setWidth("500px");
        dialog.open();
    }

    private Upload createUploadComponent() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.setMaxFiles(1);
        String mimeType = "json".equalsIgnoreCase(currentFormat) ? "application/json" : "application/xml";
        String fileExtension = "." + currentFormat.toLowerCase();
        upload.setAcceptedFileTypes(mimeType); // можно также использовать fileExtension

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            if (!fileName.toLowerCase().endsWith(fileExtension)) {
                Notification.show("Выберите файл в формате " + currentFormat.toUpperCase(), 4000, Notification.Position.MIDDLE);
                return;
            }

            try (InputStream data = buffer.getInputStream()) {
                handleUploadedFile(data.readAllBytes(), fileName);
            } catch (Exception ex) {
                Notification.show("Ошибка загрузки: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });

        return upload;
    }

    private void handleUploadedFile(byte[] data, String fileName) {

        try {
            // Получаем фабрику пользователя
            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            TabulatedFunctionFactory factory = FunctionUtils.getUserFunctionFactory(login);

            TabulatedFunction loadedFunction = null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8))) {
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
            if (loadedFunction != null && hasDuplicateX(loadedFunction)) {
                Notification.show("Значения X в загруженной функции должны быть уникальными", 4000, Notification.Position.MIDDLE);
                return;
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
    private boolean hasDuplicateX(TabulatedFunction function) {
        HashSet<Double> xValues = new HashSet<>();
        for (int i = 0; i < function.getCount(); i++) {
            double x = function.getX(i);
            if (!xValues.add(x)) {
                return true;
            }
        }
        return false;
    }
}
