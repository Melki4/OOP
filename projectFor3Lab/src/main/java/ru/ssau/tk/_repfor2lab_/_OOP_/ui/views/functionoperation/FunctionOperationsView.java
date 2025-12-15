package ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import ru.ssau.tk._repfor2lab_._OOP_.DTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.DTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.*;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.io.FunctionsIO;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.FunctionUtils;

import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.component.dialog.Dialog;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

@Route(value = "function-operations", layout = MainLayout.class)
@PageTitle("Операции с функциями | MathFunction App")
public class FunctionOperationsView extends VerticalLayout {

    private final ObjectMapper mapper = new ObjectMapper();

    private TabulatedFunction firstFunction = null;
    private TabulatedFunction secondFunction = null;
    private TabulatedFunction resultFunction = null;

    private FunctionGridComponent firstFunctionGrid;
    private FunctionGridComponent secondFunctionGrid;
    private FunctionGridComponent resultGrid;

    private final FunctionOperationsPresenter presenter;
    private final FunctionSelectionDialog functionSelectionDialog;
    private final FunctionCreationDialog functionCreationDialog;
    private final FunctionFileUploadDialog fileUploadDialog;

    private ComboBox<String> operationSelect;
    private int targetPanelNumber = 1;

    public FunctionOperationsView() {
        String login = (String) VaadinSession.getCurrent().getAttribute("login");
        TabulatedFunctionFactory factory = FunctionUtils.getUserFunctionFactory(login);

        this.presenter = new FunctionOperationsPresenter(factory);
        this.functionSelectionDialog = new FunctionSelectionDialog();
        this.functionCreationDialog = new FunctionCreationDialog();
        this.fileUploadDialog = new FunctionFileUploadDialog();

        addClassName("function-operations-view");
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setPadding(true);
        setSpacing(true);

        functionSelectionDialog.loadAvailableFunctions(login);

        createUI();
    }

    private void createUI() {
        add(new H3("Операции над табулированными функциями"));
        createOperationPanel();
        createFunctionsPanels();
    }

    private void createOperationPanel() {
        HorizontalLayout operationLayout = new HorizontalLayout();
        operationLayout.setWidth("100%");
        operationLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        operationLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        operationLayout.setSpacing(true);

        operationSelect = new ComboBox<>("Операция", Arrays.asList("Сложение", "Вычитание", "Умножение", "Деление"));
        operationSelect.setWidth("200px");
        operationSelect.setValue("Сложение");

        Button calculateButton = new Button("Вычислить", e -> calculateOperation());
        calculateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        operationLayout.add(operationSelect, calculateButton);
        add(operationLayout);
    }

    private void createFunctionsPanels() {
        // Три области для функций
        HorizontalLayout functionsLayout = new HorizontalLayout();
        functionsLayout.setWidth("100%");
        functionsLayout.setHeight("720px");
        functionsLayout.setSpacing(true);
        functionsLayout.setPadding(true);
        functionsLayout.setDefaultVerticalComponentAlignment(Alignment.START);
        functionsLayout.setJustifyContentMode(JustifyContentMode.AROUND);

        // Создаем панели с оптимизированными размерами
        firstFunctionGrid = new FunctionGridComponent("Первая функция", true, 1, this::handlePanelAction);
        secondFunctionGrid = new FunctionGridComponent("Вторая функция", true, 2, this::handlePanelAction);
        resultGrid = new FunctionGridComponent("Результат", false, 3, this::handlePanelAction);

        firstFunctionGrid.setPointHandlers(this::handlePointInsert, this::handlePointDelete);
        secondFunctionGrid.setPointHandlers(this::handlePointInsert, this::handlePointDelete);

        // Уменьшаем размер таблиц и делаем панели более компактными
        firstFunctionGrid.setGridHeight("420px");
        secondFunctionGrid.setGridHeight("420px");
        resultGrid.setGridHeight("420px");

        // Уменьшаем высоту кнопок управления
        firstFunctionGrid.setButtonLayoutHeight("52px");
        secondFunctionGrid.setButtonLayoutHeight("52px");

        functionsLayout.add(firstFunctionGrid, secondFunctionGrid, resultGrid);
        functionsLayout.setFlexGrow(1, firstFunctionGrid, secondFunctionGrid, resultGrid);
        add(functionsLayout);
    }

    private void handlePanelAction(int panelNumber, String action) {
        targetPanelNumber = panelNumber;

        switch (action) {
            case "create":
                functionCreationDialog.open(this::handleFunctionCreated);
                break;
            case "load":
                functionSelectionDialog.open(this::handleFunctionLoaded);
                break;
            case "loadJson":
                fileUploadDialog.open("json", this::handleFileLoaded);
                break;
            case "loadXml":
                fileUploadDialog.open("xml", this::handleFileLoaded);
                break;
            case "saveToDatabase":
                saveResultToDatabase();
                break;
            case "saveToFile":
                saveResultToFile();
                break;
        }
    }

    private void handlePointInsert(int panelNumber, Point newPoint) {
        if (panelNumber == 3) {
            return;
        }

        TabulatedFunction targetFunction = getFunctionByPanel(panelNumber);

        if (targetFunction == null) {
            Notification.show("Сначала выберите или создайте функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            TabulatedFunction editableFunction = ensureEditableFunction(targetFunction, panelNumber);

            if (editableFunction.indexOfX(newPoint.getX()) != -1) {
                throw new IllegalArgumentException("Точка с таким x уже существует");
            }

            if (editableFunction instanceof Insertable) {
                ((Insertable) editableFunction).insert(newPoint.getX(), newPoint.getY());
                setFunctionForPanel(editableFunction, panelNumber);
            } else {
                Notification.show("Эта функция не поддерживает добавление точек", 4000, Notification.Position.MIDDLE);
            }
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 4000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show("Ошибка при добавлении точки: " + e.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private void handlePointDelete(int panelNumber, Double xValue) {
        if (panelNumber == 3) {
            return;
        }

        TabulatedFunction targetFunction = getFunctionByPanel(panelNumber);

        if (targetFunction == null) {
            Notification.show("Сначала выберите или создайте функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            TabulatedFunction editableFunction = ensureEditableFunction(targetFunction, panelNumber);
            int index = editableFunction.indexOfX(xValue);

            if (index == -1) {
                Notification.show("Точка не найдена", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (editableFunction instanceof Removable) {
                ((Removable) editableFunction).remove(index);
                setFunctionForPanel(editableFunction, panelNumber);
            } else {
                Notification.show("Эта функция не поддерживает удаление точек", 4000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Ошибка при удалении точки: " + e.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private TabulatedFunction ensureEditableFunction(TabulatedFunction function, int panelNumber) {
        if (function instanceof Insertable && function instanceof Removable) {
            return function;
        }

        double[] xValues = new double[function.getCount()];
        double[] yValues = new double[function.getCount()];

        for (int i = 0; i < function.getCount(); i++) {
            xValues[i] = function.getX(i);
            yValues[i] = function.getY(i);
        }

        TabulatedFunction editableCopy = new ArrayTabulatedFunction(xValues, yValues);
        setFunctionForPanel(editableCopy, panelNumber);
        return editableCopy;
    }

    private TabulatedFunction getFunctionByPanel(int panelNumber) {
        switch (panelNumber) {
            case 1:
                return firstFunction;
            case 2:
                return secondFunction;
            case 3:
                return resultFunction;
            default:
                return null;
        }
    }

    private void handleFunctionCreated(TabulatedFunction function) {
        setFunctionForPanel(function, targetPanelNumber);
    }

    private void handleFunctionLoaded(TabulatedFunction function) {
        setFunctionForPanel(function, targetPanelNumber);
    }

    private void handleFileLoaded(TabulatedFunction function) {
        setFunctionForPanel(function, targetPanelNumber);
    }

    private void setFunctionForPanel(TabulatedFunction function, int panelNumber) {
        switch (panelNumber) {
            case 1:
                firstFunction = function;
                firstFunctionGrid.setFunction(function);
                break;
            case 2:
                secondFunction = function;
                secondFunctionGrid.setFunction(function);
                break;
            case 3:
                resultFunction = function;
                resultGrid.setFunction(function);
                break;
        }
    }

    private void calculateOperation() {
        if (firstFunction == null || secondFunction == null) {
            Notification.show("Обе функции должны быть заданы", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (firstFunction.getCount() != secondFunction.getCount()) {
            Notification.show("Функции должны иметь одинаковое количество точек", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            String operation = operationSelect.getValue();
            resultFunction = presenter.performOperation(firstFunction, secondFunction, operation);
            resultGrid.setFunction(resultFunction);
        } catch (Exception e) {
            Notification.show("Ошибка при выполнении операции: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void saveResultToDatabase() {
        if (resultFunction == null) {
            Notification.show("Нет функции для сохранения", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Диалог для ввода имени функции
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Сохранение результата");

        // Правильное создание TextField
        TextField nameField = new TextField("Имя функции");
        nameField.setRequiredIndicatorVisible(true); // Правильный метод вместо setRequired(true)
        nameField.setPlaceholder("Введите имя для сохранения"); // Этот метод существует в Vaadin Flow
        nameField.setWidth("300px");

        Button saveButton = new Button("Сохранить", e -> {
            String name = nameField.getValue(); // getValue() работает корректно
            if (name == null || name.trim().isEmpty()) {
                Notification.show("Введите имя функции", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                // Сохраняем функцию в базу данных через API
                String login = (String) VaadinSession.getCurrent().getAttribute("login");
                int userId = FunctionUtils.getUserIdByLogin(login);

                // Формируем DTO для сохранения
                MathFunctionsDTO functionDTO = new MathFunctionsDTO();
                functionDTO.setFunctionName(name.trim());
                functionDTO.setLeftBoarder(resultFunction.leftBound());
                functionDTO.setRightBoarder(resultFunction.rightBound());
                functionDTO.setAmountOfDots((long) resultFunction.getCount());
                functionDTO.setFunctionType("manual");
                functionDTO.setOwnerID((long) userId);

                String jsonFunc = mapper.writeValueAsString(functionDTO);
                var funcResp = BasicAuthClient.sendPost("/math-functions/create/" + userId, jsonFunc);

                if (funcResp.statusCode() != 201) {
                    String error = BasicAuthClient.extractErrorMessage(funcResp.body());
                    Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
                    return;
                }
                // Сохраняем функцию

                if (funcResp.statusCode() == 200 || funcResp.statusCode() == 201) {
                    // Получаем ID функции
                    var searchResp = BasicAuthClient.sendGet("/math-functions/get-by-function-name/" + name);
                    if (searchResp.statusCode() != 200) {
                        Notification.show("Не удалось найти функцию", 4000, Notification.Position.MIDDLE);
                        return;
                    }
                    java.util.List<MathFunctionsDTO> found = mapper.readValue(searchResp.body(), new TypeReference<java.util.List<MathFunctionsDTO>>() {});
                    if (found.isEmpty()) {
                        Notification.show("Функция не найдена", 4000, Notification.Position.MIDDLE);
                        return;
                    }
                    int functionId = Math.toIntExact(found.get(0).getFunctionID());

                    // Сохраняем точки функции
                    java.util.List<PointsDTO> points = new ArrayList<>();
                    for (int i = 0; i < resultFunction.getCount(); i++) {
                        PointsDTO point = new PointsDTO();
                        point.setxValue(resultFunction.getX(i));
                        point.setyValue(resultFunction.getY(i));
                        point.setFunctionID((long) functionId);
                        points.add(point);
                    }

                    Map<String, List<PointsDTO>> payload = new HashMap<>();
                    payload.put("points", points);
                    String pointsJson = mapper.writeValueAsString(payload);

                    var pointsResp = BasicAuthClient.sendPost("/points/create-points/" + functionId, pointsJson);

                    if (pointsResp.statusCode() == 201) {
                        Notification.show("Функция успешно создана!", 4000, Notification.Position.MIDDLE);
                        getUI().ifPresent(ui -> ui.navigate("my-functions"));
                    } else {
                        String error = BasicAuthClient.extractErrorMessage(pointsResp.body());
                        Notification.show("Ошибка точек: " + error, 5000, Notification.Position.MIDDLE);
                    }
                } else {
                    Notification.show("Ошибка сохранения функции: " + funcResp.body(),
                            5000, Notification.Position.MIDDLE);
                }
            } catch (Exception ex) {
                Notification.show("Ошибка: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(nameField, saveButton, cancelButton);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.setWidth("400px");
        dialog.open();
    }

    private void saveResultToFile() {
        if (resultFunction == null) {
            Notification.show("Нет функции для сохранения", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Гарантируем, что функция будет преобразована в ArrayTabulatedFunction для сериализации
        ArrayTabulatedFunction arrayFunction;
        if (resultFunction instanceof ArrayTabulatedFunction) {
            arrayFunction = (ArrayTabulatedFunction) resultFunction;
        } else {
            // Создаем новый ArrayTabulatedFunction из существующей функции
            double[] xValues = new double[resultFunction.getCount()];
            double[] yValues = new double[resultFunction.getCount()];

            for (int i = 0; i < resultFunction.getCount(); i++) {
                xValues[i] = resultFunction.getX(i);
                yValues[i] = resultFunction.getY(i);
            }

            arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        }

        // Диалог выбора формата и имени файла
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Сохранение результата в файл");

        // Правильное создание TextField
        TextField fileNameField = new TextField("Имя файла");
        fileNameField.setRequiredIndicatorVisible(true); // Правильный метод
        fileNameField.setPlaceholder("Введите имя файла");
        fileNameField.setValue("result_function");
        fileNameField.setWidth("300px");

        ComboBox<String> formatSelect = new ComboBox<>("Формат файла");
        formatSelect.setItems("JSON", "XML");
        formatSelect.setValue("JSON");
        formatSelect.setWidth("150px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        Button saveButton = new Button("Сохранить", e -> {
            String fileName = fileNameField.getValue();
            String format = formatSelect.getValue().toLowerCase();

            if (fileName == null || fileName.trim().isEmpty()) {
                Notification.show("Введите имя файла", 3000, Notification.Position.MIDDLE);
                return;
            }

            fileName = fileName.trim();
            if (!fileName.toLowerCase().endsWith("." + format)) {
                fileName += "." + format;
            }

            try {
                // Определяем директорию для сохранения
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                    if ("json".equals(format)) {
                        FunctionsIO.serializeJson(writer, arrayFunction);
                    } else {
                        FunctionsIO.serializeXml(writer, arrayFunction);
                    }
                    writer.flush();
                }

                byte[] data = outputStream.toByteArray();
                String finalFileName = fileName;

                String contentType = "json".equals(format)
                        ? "application/json"
                        : "application/xml";

                String base64Data = Base64.getEncoder().encodeToString(data);
                String safeFileName = finalFileName.replace("\"", "");

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
                        base64Data, contentType, safeFileName));

                dialog.close();

                Notification.show("Выберите место для сохранения файла " + fileName,
                        3000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Ошибка сохранения: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> dialog.close());

        dialogLayout.add(fileNameField, formatSelect, saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.setWidth("400px");
        dialog.open();
    }
}