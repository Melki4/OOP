package ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.io.FunctionsIO;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.FunctionUtils;

import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.component.dialog.Dialog;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
        functionsLayout.setHeight("600px");
        functionsLayout.setSpacing(true);
        functionsLayout.setPadding(true);

        // Создаем панели с оптимизированными размерами
        firstFunctionGrid = new FunctionGridComponent("Первая функция", true, 1, this::handlePanelAction);
        secondFunctionGrid = new FunctionGridComponent("Вторая функция", true, 2, this::handlePanelAction);
        resultGrid = new FunctionGridComponent("Результат", false, 3, this::handlePanelAction);

        // Уменьшаем размер таблиц
        firstFunctionGrid.setGridHeight("280px");
        secondFunctionGrid.setGridHeight("280px");
        resultGrid.setGridHeight("280px");

        // Уменьшаем высоту кнопок управления
        firstFunctionGrid.setButtonLayoutHeight("60px");
        secondFunctionGrid.setButtonLayoutHeight("60px");

        functionsLayout.add(firstFunctionGrid, secondFunctionGrid, resultGrid);
        functionsLayout.setFlexGrow(1, firstFunctionGrid, secondFunctionGrid, resultGrid);
        functionsLayout.setMaxHeight("500px");

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
                functionDTO.setLeftBorder(resultFunction.leftBound());
                functionDTO.setRightBorder(resultFunction.rightBound());
                functionDTO.setAmountOfDots(resultFunction.getCount());
                functionDTO.setFunctionType("manual");
                functionDTO.setOwnerId(userId);

                String jsonFunc = mapper.writeValueAsString(functionDTO);
                var funcResp = BasicAuthClient.sendPost("/math-functions/create/" + userId, jsonFunc);

                if (funcResp.statusCode() != 201) {
                    String error = BasicAuthClient.extractErrorMessage(funcResp.body());
                    Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
                    return;
                }
                // Сохраняем функцию

                if (funcResp.statusCode() == 200) {
                    // Получаем ID функции
                    var searchResp = BasicAuthClient.sendGet("/math-functions/get-by-function-name/" + name.trim());
                    if (searchResp.statusCode() != 200) {
                        Notification.show("Не удалось найти функцию", 4000, Notification.Position.MIDDLE);
                        return;
                    }
                    java.util.List<MathFunctionsDTO> found = mapper.readValue(searchResp.body(), new TypeReference<java.util.List<MathFunctionsDTO>>() {});
                    if (found.isEmpty()) {
                        Notification.show("Функция не найдена", 4000, Notification.Position.MIDDLE);
                        return;
                    }
                    int functionId = found.get(0).getFunctionId();

                    // Сохраняем точки функции
                    java.util.List<PointsDTO> points = new ArrayList<>();
                    for (int i = 0; i < resultFunction.getCount(); i++) {
                        PointsDTO point = new PointsDTO();
                        point.setXValue(resultFunction.getX(i));
                        point.setYValue(resultFunction.getY(i));
                        point.setFunctionId(functionId);
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

        Button saveButton = new Button("Сохранить", e -> {
            String fileName = fileNameField.getValue(); // getValue() работает корректно
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
                File directory = new File("src/main/resources/files");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File file = new File(directory, fileName);

                if ("json".equals(format)) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        FunctionsIO.serializeJson(writer, arrayFunction);
                    }
                } else if ("xml".equals(format)) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        FunctionsIO.serializeXml(writer, arrayFunction);
                    }
                }

                dialog.close();
                Notification.show("Функция успешно сохранена в файл " + fileName,
                        3000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Ошибка сохранения: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(fileNameField, formatSelect, saveButton, cancelButton);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.setWidth("400px");
        dialog.open();
    }
}