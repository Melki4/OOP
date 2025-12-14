package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.MarkersBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Zoom;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.AxisType;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Insertable;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Removable;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.io.FunctionsIO;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.FunctionUtils;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionCreationDialog;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionFileUploadDialog;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionGridComponent;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionSelectionDialog;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Route(value = "function-visualization", layout = MainLayout.class)
@PageTitle("Графики функций | MathFunction App")
public class FunctionVisualizationView extends VerticalLayout {

    private final FunctionGridComponent functionGrid;
    private final FunctionCreationDialog creationDialog;
    private final FunctionSelectionDialog selectionDialog;
    private final FunctionFileUploadDialog fileUploadDialog;

    private TabulatedFunction currentFunction;
    private final Div chartContainer;

    private final NumberField xInputField;
    private final Span applyResult;

    public FunctionVisualizationView() {
        String login = (String) VaadinSession.getCurrent().getAttribute("login");
        creationDialog = new FunctionCreationDialog();
        selectionDialog = new FunctionSelectionDialog();
        selectionDialog.loadAvailableFunctions(login);
        fileUploadDialog = new FunctionFileUploadDialog();

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H3("Просмотр и редактирование табулированной функции"));
        add(createToolbar());

        HorizontalLayout content = new HorizontalLayout();
        content.setSizeFull();
        content.setSpacing(true);

        functionGrid = new FunctionGridComponent("Точки функции", true, 1, this::handlePanelAction);
        functionGrid.setPointHandlers(this::handlePointInsert, this::handlePointDelete);
        functionGrid.setUpdatePointHandler(this::handlePointUpdate);
        functionGrid.setWidth("40%");
        functionGrid.setHeight("100%");

        VerticalLayout rightSide = new VerticalLayout();
        rightSide.setSizeFull();
        rightSide.setPadding(false);
        rightSide.setSpacing(true);

        chartContainer = new Div();
        chartContainer.setWidth("100%");
        chartContainer.setHeight("420px");
        renderChart();

        HorizontalLayout applyLayout = createApplyLayout();

        rightSide.add(chartContainer, applyLayout);
        rightSide.setFlexGrow(1, chartContainer);

        content.add(functionGrid, rightSide);
        content.setFlexGrow(1, rightSide);
        add(content);
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setSpacing(true);

        Button createButton = new Button("Создать", e -> creationDialog.open(this::setCurrentFunction));
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button loadButton = new Button("Загрузить", e -> selectionDialog.open(this::setCurrentFunction));

        Button loadJsonButton = new Button("Загрузить JSON", e -> fileUploadDialog.open("json", this::setCurrentFunction));
        Button loadXmlButton = new Button("Загрузить XML", e -> fileUploadDialog.open("xml", this::setCurrentFunction));

        Button saveButton = new Button("Сохранить", e -> saveCurrentFunction());
        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        toolbar.add(createButton, loadButton, loadJsonButton, loadXmlButton, saveButton);
        return toolbar;
    }

    private HorizontalLayout createApplyLayout() {
        HorizontalLayout applyLayout = new HorizontalLayout();
        applyLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        applyLayout.setSpacing(true);
        applyLayout.setWidthFull();

        xInputField = new NumberField("x для вычисления");
        xInputField.setWidth("200px");

        Button applyButton = new Button("Вычислить f(x)", e -> calculateApply());
        applyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        applyResult = new Span("Значение: —");

        applyLayout.add(xInputField, applyButton, applyResult);
        applyLayout.expand(applyResult);
        return applyLayout;
    }

    private void handlePanelAction(int panel, String action) {
        switch (action) {
            case "create":
                creationDialog.open(this::setCurrentFunction);
                break;
            case "load":
                selectionDialog.open(this::setCurrentFunction);
                break;
            case "loadJson":
                fileUploadDialog.open("json", this::setCurrentFunction);
                break;
            case "loadXml":
                fileUploadDialog.open("xml", this::setCurrentFunction);
                break;
            default:
                break;
        }
    }

    private void handlePointInsert(int panelNumber, Point newPoint) {
        if (currentFunction == null) {
            Notification.show("Сначала создайте или загрузите функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            TabulatedFunction editable = ensureEditableFunction(currentFunction);
            if (editable.indexOfX(newPoint.getX()) != -1) {
                Notification.show("Точка с таким x уже существует", 3000, Notification.Position.MIDDLE);
                return;
            }

            ((Insertable) editable).insert(newPoint.getX(), newPoint.getY());
            setCurrentFunction(editable);
        } catch (Exception ex) {
            Notification.show("Ошибка при добавлении точки: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private void handlePointDelete(int panelNumber, Double xValue) {
        if (currentFunction == null) {
            Notification.show("Сначала создайте или загрузите функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            TabulatedFunction editable = ensureEditableFunction(currentFunction);
            int index = editable.indexOfX(xValue);
            if (index == -1) {
                Notification.show("Точка не найдена", 3000, Notification.Position.MIDDLE);
                return;
            }

            ((Removable) editable).remove(index);
            setCurrentFunction(editable);
        } catch (Exception ex) {
            Notification.show("Ошибка при удалении точки: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private void handlePointUpdate(int panelNumber, Point point) {
        if (currentFunction == null) {
            return;
        }
        int index = currentFunction.indexOfX(point.getX());
        if (index != -1) {
            currentFunction.setY(index, point.getY());
            renderChart();
        }
    }

    private TabulatedFunction ensureEditableFunction(TabulatedFunction function) {
        if (function instanceof Insertable && function instanceof Removable) {
            return function;
        }
        double[] xValues = new double[function.getCount()];
        double[] yValues = new double[function.getCount()];
        for (int i = 0; i < function.getCount(); i++) {
            xValues[i] = function.getX(i);
            yValues[i] = function.getY(i);
        }
        return new ArrayTabulatedFunction(xValues, yValues);
    }

    private void setCurrentFunction(TabulatedFunction function) {
        if (function == null) {
            currentFunction = null;
            functionGrid.setFunction(null);
            renderChart();
            return;
        }
        currentFunction = ensureEditableFunction(function);
        functionGrid.setFunction(currentFunction);
        renderChart();
    }

    private void renderChart() {
        chartContainer.removeAll();

        Series<Double> series = buildSeries();
        String[] labels = buildLabels();

        ApexCharts chart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.line)
                        .withZoom(new Zoom().setEnabled(true))
                        .build())
                .withStroke(StrokeBuilder.get().withCurve(Curve.straight).build())
                .withMarkers(MarkersBuilder.get().withSize(5.0).build())
                .withXaxis(XAxisBuilder.get()
                        .withType(AxisType.numeric)
                        .withCategories(labels)
                        .build())
                .withSeries(series)
                .build();

        chart.setWidth("100%");
        chart.setHeight("380px");
        chartContainer.add(chart);
    }

    private Series<Double> buildSeries() {
        if (currentFunction == null || currentFunction.getCount() == 0) {
            return new Series<>("f(x)", new Double[]{});
        }

        List<Double> yValues = new ArrayList<>();
        for (int i = 0; i < currentFunction.getCount(); i++) {
            yValues.add(currentFunction.getY(i));
        }
        return new Series<>("f(x)", yValues.toArray(new Double[0]));
    }

    private String[] buildLabels() {
        if (currentFunction == null || currentFunction.getCount() == 0) {
            return new String[]{};
        }
        String[] labels = new String[currentFunction.getCount()];
        for (int i = 0; i < currentFunction.getCount(); i++) {
            labels[i] = String.format("%.3f", currentFunction.getX(i));
        }
        return labels;
    }

    private void saveCurrentFunction() {
        if (currentFunction == null) {
            Notification.show("Нет функции для сохранения", 3000, Notification.Position.MIDDLE);
            return;
        }

        ArrayTabulatedFunction arrayFunction = copyToArray(currentFunction);

        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Сохранение функции");

        com.vaadin.flow.component.textfield.TextField fileNameField = new com.vaadin.flow.component.textfield.TextField("Имя файла");
        fileNameField.setRequiredIndicatorVisible(true);
        fileNameField.setPlaceholder("Введите имя файла");
        fileNameField.setValue("tabulated_function");

        com.vaadin.flow.component.combobox.ComboBox<String> formatSelect = new com.vaadin.flow.component.combobox.ComboBox<>("Формат файла");
        formatSelect.setItems("JSON", "XML");
        formatSelect.setValue("JSON");

        Button saveButton = new Button("Сохранить", e -> {
            String fileName = fileNameField.getValue();
            String format = formatSelect.getValue().toLowerCase();

            if (fileName == null || fileName.isBlank()) {
                Notification.show("Введите имя файла", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (!fileName.toLowerCase().endsWith("." + format)) {
                fileName += "." + format;
            }

            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                    if ("json".equals(format)) {
                        FunctionsIO.serializeJson(writer, arrayFunction);
                    } else {
                        FunctionsIO.serializeXml(writer, arrayFunction);
                    }
                }

                byte[] data = outputStream.toByteArray();
                String base64Data = Base64.getEncoder().encodeToString(data);
                String contentType = "json".equals(format) ? "application/json" : "application/xml";
                String finalFileName = fileName;

                getUI().ifPresent(ui -> ui.getPage().executeJs(
                        "const data = atob($0);" +
                                "const len = data.length;" +
                                "const bytes = new Uint8Array(len);" +
                                "for (let i = 0; i < len; i++) { bytes[i] = data.charCodeAt(i); }" +
                                "const blob = new Blob([bytes], { type: $1 });" +
                                "const url = URL.createObjectURL(blob);" +
                                "const a = document.createElement('a');" +
                                "a.style.display = 'none';" +
                                "a.href = url;" +
                                "a.download = $2;" +
                                "document.body.appendChild(a);" +
                                "a.click();" +
                                "setTimeout(() => { URL.revokeObjectURL(url); a.remove(); }, 1000);",
                        base64Data, contentType, finalFileName));

                dialog.close();
                Notification.show("Файл " + finalFileName + " готов к сохранению", 3000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Ошибка сохранения: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> dialog.close());

        VerticalLayout layout = new VerticalLayout(fileNameField, formatSelect, new HorizontalLayout(saveButton, cancelButton));
        layout.setPadding(false);
        layout.setSpacing(true);
        dialog.add(layout);
        dialog.open();
    }

    private ArrayTabulatedFunction copyToArray(TabulatedFunction function) {
        double[] xValues = new double[function.getCount()];
        double[] yValues = new double[function.getCount()];
        for (int i = 0; i < function.getCount(); i++) {
            xValues[i] = function.getX(i);
            yValues[i] = function.getY(i);
        }
        return new ArrayTabulatedFunction(xValues, yValues);
    }

    private void calculateApply() {
        if (currentFunction == null) {
            Notification.show("Нет функции для вычисления", 3000, Notification.Position.MIDDLE);
            return;
        }

        Double x = xInputField.getValue();
        if (x == null) {
            Notification.show("Введите значение x", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            double y = currentFunction.apply(x);
            applyResult.setText(String.format("Значение: f(%.3f) = %.6f", x, y));
        } catch (Exception ex) {
            Notification.show("Ошибка вычисления: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }
}