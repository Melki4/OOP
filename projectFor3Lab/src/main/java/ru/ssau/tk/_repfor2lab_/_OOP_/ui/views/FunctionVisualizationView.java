package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
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
import com.vaadin.flow.component.UI;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Insertable;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Removable;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.io.FunctionsIO;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionCreationDialog;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionFileUploadDialog;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionGridComponent;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionSelectionDialog;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

@Route(value = "function-visualization", layout = MainLayout.class)
@PageTitle("Графики функций | MathFunction App")
public class FunctionVisualizationView extends VerticalLayout {

    private final FunctionGridComponent functionGrid;
    private final FunctionCreationDialog creationDialog;
    private final FunctionSelectionDialog selectionDialog;
    private final FunctionFileUploadDialog fileUploadDialog;

    private TabulatedFunction currentFunction;
    private final Div chartContainer;
    private final CanvasElement chartCanvas;

    private final NumberField xInputField = new NumberField("x для вычисления");
    private final Span applyResult = new Span("Значение: —");

    private final NumberField xMinField = new NumberField("Мин. X");
    private final NumberField xMaxField = new NumberField("Макс. X");
    private final NumberField yMinField = new NumberField("Мин. Y");
    private final NumberField yMaxField = new NumberField("Макс. Y");
    private boolean userAdjustedScale = false;
    private boolean updatingScaleFields = false;

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

        functionGrid = new FunctionGridComponent("Точки функции", true, 1, this::handlePanelAction, false);
        functionGrid.setPointHandlers(this::handlePointInsert, this::handlePointDelete);
        functionGrid.setUpdatePointHandler(this::handlePointUpdate);
        functionGrid.setWidth("40%");
        functionGrid.setHeight("100%");

        VerticalLayout rightSide = new VerticalLayout();
        rightSide.setSizeFull();
        rightSide.setPadding(true);
        rightSide.setSpacing(true);
        rightSide.getStyle().set("padding-bottom", "var(--lumo-space-l)");

        chartContainer = new Div();
        chartContainer.setWidth("100%");
        chartContainer.setHeight("420px");
        chartCanvas = new CanvasElement();
        chartCanvas.setWidthFull();
        chartCanvas.setHeight("420px");
        chartContainer.add(chartCanvas);
        loadChartJsOnce();
        renderChart();

        HorizontalLayout applyLayout = createApplyLayout();

        rightSide.add(createScaleControls(), chartContainer, applyLayout);
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
        applyLayout.getStyle().set("margin-bottom", "16px");

        xInputField.setWidth("200px");

        Button applyButton = new Button("Вычислить f(x)", e -> calculateApply());
        applyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        applyLayout.add(xInputField, applyButton, applyResult);
        applyLayout.expand(applyResult);
        return applyLayout;
    }

    private VerticalLayout createScaleControls() {
        configureScaleField(xMinField);
        configureScaleField(xMaxField);
        configureScaleField(yMinField);
        configureScaleField(yMaxField);

        HorizontalLayout xScale = new HorizontalLayout(xMinField, xMaxField);
        xScale.setSpacing(true);
        xScale.setDefaultVerticalComponentAlignment(Alignment.END);

        HorizontalLayout yScale = new HorizontalLayout(yMinField, yMaxField);
        yScale.setSpacing(true);
        yScale.setDefaultVerticalComponentAlignment(Alignment.END);

        Button applyScaleButton = new Button("Применить масштаб", e -> applyScaleSettings());
        applyScaleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button resetScaleButton = new Button("Сбросить", e -> resetScale());

        HorizontalLayout actions = new HorizontalLayout(applyScaleButton, resetScaleButton);
        actions.setSpacing(true);

        VerticalLayout scaleLayout = new VerticalLayout(new Span("Масштабирование графика"), xScale, yScale, actions);
        scaleLayout.setPadding(false);
        scaleLayout.setSpacing(true);

        return scaleLayout;
    }

    private void configureScaleField(NumberField field) {
        field.setPlaceholder("авто");
        field.setWidth("140px");
        field.setStep(0.1);
        field.addValueChangeListener(event -> {
            if (!updatingScaleFields) {
                userAdjustedScale = true;
            }
        });
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
            if (!userAdjustedScale) {
                updateScaleFields();
            }
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
            resetScaleFields();
            userAdjustedScale = false;
            renderChart();
            return;
        }
        currentFunction = ensureEditableFunction(function);
        functionGrid.setFunction(currentFunction);
        if (!userAdjustedScale) {
            updateScaleFields();
        }
        renderChart();
    }

    private void renderChart() {
        updateChartData();
    }

    private void loadChartJsOnce() {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.getPage().addJavaScript("https://cdn.jsdelivr.net/npm/chart.js");
        }
    }

    private void updateChartData() {
        UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }

        if (!validateScaleRanges()) {
            return;
        }

        Double xMin = xMinField.getValue();
        Double xMax = xMaxField.getValue();
        Double yMin = yMinField.getValue();
        Double yMax = yMaxField.getValue();

        JsonArray points = buildPointMap();
        ui.getPage().executeJs(
                "(function(canvas, points, xMin, xMax, yMin, yMax){" +
                        " if (!canvas) return;" +
                        " const ctx = canvas.getContext('2d');" +
                        " if (!window.Chart) { return; }" +
                        " if (window.functionChart) { window.functionChart.destroy(); }" +
                        " const normalize = (value) => (typeof value === 'number' && !Number.isNaN(value) ? value : undefined);" +
                        " const resolvedXMin = normalize(xMin);" +
                        " const resolvedXMax = normalize(xMax);" +
                        " const resolvedYMin = normalize(yMin);" +
                        " const resolvedYMax = normalize(yMax);" +
                        " window.functionChart = new Chart(ctx, {" +
                        "   type: 'line'," +
                        "   data: { datasets: [{" +
                        "     label: 'f(x)'," +
                        "     data: points," +
                        "     borderColor: '#1f77b4'," +
                        "     fill: false," +
                        "     tension: 0.25," +
                        "     pointRadius: 4" +
                        "   }]}," +
                        "   options: {" +
                        "     responsive: true," +
                        "     maintainAspectRatio: false," +
                        "     parsing: false," +
                        "     scales: {" +
                        "       x: { type: 'linear', title: { display: true, text: 'x' }, min: resolvedXMin, max: resolvedXMax }," +
                        "       y: { title: { display: true, text: 'f(x)' }, min: resolvedYMin, max: resolvedYMax }" +
                        "     }," +
                        "     plugins: { legend: { display: true } }" +
                        "   }" +
                        " });" +
                        "})(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);",
                chartCanvas.getElement(), points, xMin, xMax, yMin, yMax);

    }

    private boolean validateScaleRanges() {
        return validateAxisRange(xMinField, xMaxField, "X") && validateAxisRange(yMinField, yMaxField, "Y");
    }

    private boolean validateAxisRange(NumberField minField, NumberField maxField, String axisName) {
        Double min = minField.getValue();
        Double max = maxField.getValue();
        if (min != null && max != null && min >= max) {
            Notification.show(String.format("Минимальное значение оси %s должно быть меньше максимального", axisName), 3000, Notification.Position.MIDDLE);
            return false;
        }
        return true;
    }

    private JsonArray buildPointMap() {
        JsonArray points = Json.createArray();
        if (currentFunction == null) {
            return points;
        }
        for (int i = 0; i < currentFunction.getCount(); i++) {
            JsonObject point = Json.createObject();
            point.put("x", currentFunction.getX(i));
            point.put("y", currentFunction.getY(i));
            points.set(i, point);
        }
        return points;
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

    private void applyScaleSettings() {
        if (!validateScaleRanges()) {
            return;
        }
        userAdjustedScale = true;
        renderChart();
    }

    private void resetScale() {
        userAdjustedScale = false;
        updateScaleFields();
        renderChart();
    }

    private void updateScaleFields() {
        if (currentFunction == null || currentFunction.getCount() == 0) {
            resetScaleFields();
            return;
        }

        double minX = currentFunction.getX(0);
        double maxX = currentFunction.getX(0);
        double minY = currentFunction.getY(0);
        double maxY = currentFunction.getY(0);

        for (int i = 1; i < currentFunction.getCount(); i++) {
            double x = currentFunction.getX(i);
            double y = currentFunction.getY(i);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        double xPadding = Math.max((maxX - minX) * 0.1, 1e-3);
        double yPadding = Math.max((maxY - minY) * 0.1, 1e-3);

        updatingScaleFields = true;
        xMinField.setValue(minX - xPadding);
        xMaxField.setValue(maxX + xPadding);
        yMinField.setValue(minY - yPadding);
        yMaxField.setValue(maxY + yPadding);
        updatingScaleFields = false;
    }

    private void resetScaleFields() {
        updatingScaleFields = true;
        xMinField.clear();
        xMaxField.clear();
        yMinField.clear();
        yMaxField.clear();
        updatingScaleFields = false;
    }

    @Tag("canvas")
    private static class CanvasElement extends com.vaadin.flow.component.Component implements HasSize {
        CanvasElement() {
            getElement().getStyle().set("display", "block");
        }
    }
}