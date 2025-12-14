package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Insertable;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Removable;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.io.FunctionsIO;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedDifferentialOperator;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "function-differentiation", layout = MainLayout.class)
@PageTitle("Дифференцирование | MathFunction App")
public class FunctionDifferentiationView extends VerticalLayout {

    private final ObjectMapper mapper = new ObjectMapper();
    private final TabulatedDifferentialOperator differentialOperator;

    private final FunctionSelectionDialog functionSelectionDialog;
    private final FunctionCreationDialog functionCreationDialog;
    private final FunctionFileUploadDialog fileUploadDialog;

    private TabulatedFunction sourceFunction;
    private TabulatedFunction resultFunction;

    private FunctionGridComponent sourceGrid;
    private FunctionGridComponent resultGrid;

    public FunctionDifferentiationView() {
        String login = (String) VaadinSession.getCurrent().getAttribute("login");
        TabulatedFunctionFactory factory = FunctionUtils.getUserFunctionFactory(login);
        this.differentialOperator = new TabulatedDifferentialOperator(factory);

        this.functionSelectionDialog = new FunctionSelectionDialog();
        this.functionCreationDialog = new FunctionCreationDialog();
        this.fileUploadDialog = new FunctionFileUploadDialog();

        addClassName("function-differentiation-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        functionSelectionDialog.loadAvailableFunctions(login);

        createUI();
    }

    private void createUI() {
        add(new H3("Дифференцирование табулированной функции"));

        Button differentiateButton = new Button("Дифференцировать", e -> calculateDerivative());
        differentiateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(differentiateButton);

        HorizontalLayout functionsLayout = new HorizontalLayout();
        functionsLayout.setWidth("100%");
        functionsLayout.setHeight("720px");
        functionsLayout.setSpacing(true);
        functionsLayout.setPadding(true);
        functionsLayout.setDefaultVerticalComponentAlignment(Alignment.START);
        functionsLayout.setJustifyContentMode(JustifyContentMode.AROUND);

        sourceGrid = new FunctionGridComponent("Исходная функция", true, 1, this::handlePanelAction);
        resultGrid = new FunctionGridComponent("Результат", false, 2, this::handlePanelAction);

        sourceGrid.setPointHandlers(this::handlePointInsert, this::handlePointDelete);
        sourceGrid.setGridHeight("420px");
        resultGrid.setGridHeight("420px");

        functionsLayout.add(sourceGrid, resultGrid);
        functionsLayout.setFlexGrow(1, sourceGrid, resultGrid);
        add(functionsLayout);
    }

    private void handlePanelAction(int panelNumber, String action) {
        if (panelNumber == 1) {
            switch (action) {
                case "create":
                    functionCreationDialog.open(this::handleFunctionLoaded);
                    break;
                case "load":
                    functionSelectionDialog.open(this::handleFunctionLoaded);
                    break;
                case "loadJson":
                    fileUploadDialog.open("json", this::handleFunctionLoaded);
                    break;
                case "loadXml":
                    fileUploadDialog.open("xml", this::handleFunctionLoaded);
                    break;
            }
        } else if (panelNumber == 2) {
            if ("saveToDatabase".equals(action)) {
                saveResultToDatabase();
            } else if ("saveToFile".equals(action)) {
                saveResultToFile();
            }
        }
    }

    private void handleFunctionLoaded(TabulatedFunction function) {
        sourceFunction = function;
        sourceGrid.setFunction(function);
    }

    private void calculateDerivative() {
        if (sourceFunction == null) {
            Notification.show("Сначала выберите или создайте функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            resultFunction = differentialOperator.derive(sourceFunction);
            resultGrid.setFunction(resultFunction);
        } catch (Exception e) {
            Notification.show("Ошибка при дифференцировании: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void handlePointInsert(int panelNumber, Point newPoint) {
        TabulatedFunction target = ensureEditableFunction(sourceFunction);
        if (target == null) {
            Notification.show("Сначала выберите или создайте функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            if (target.indexOfX(newPoint.getX()) != -1) {
                throw new IllegalArgumentException("Точка с таким x уже существует");
            }
            if (target instanceof Insertable) {
                ((Insertable) target).insert(newPoint.getX(), newPoint.getY());
                sourceFunction = target;
                sourceGrid.setFunction(sourceFunction);
            } else {
                Notification.show("Эта функция не поддерживает добавление точек", 4000, Notification.Position.MIDDLE);
            }
        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage(), 4000, Notification.Position.MIDDLE);
        } catch (Exception ex) {
            Notification.show("Ошибка при добавлении точки: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private void handlePointDelete(int panelNumber, Double xValue) {
        TabulatedFunction target = ensureEditableFunction(sourceFunction);
        if (target == null) {
            Notification.show("Сначала выберите или создайте функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            int index = target.indexOfX(xValue);
            if (index == -1) {
                Notification.show("Точка не найдена", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (target instanceof Removable) {
                ((Removable) target).remove(index);
                sourceFunction = target;
                sourceGrid.setFunction(sourceFunction);
            } else {
                Notification.show("Эта функция не поддерживает удаление точек", 4000, Notification.Position.MIDDLE);
            }
        } catch (Exception ex) {
            Notification.show("Ошибка при удалении точки: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private TabulatedFunction ensureEditableFunction(TabulatedFunction function) {
        if (function == null) {
            return null;
        }
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
        sourceFunction = editableCopy;
        sourceGrid.setFunction(sourceFunction);
        return editableCopy;
    }

    private void saveResultToDatabase() {
        if (resultFunction == null) {
            Notification.show("Нет функции для сохранения", 3000, Notification.Position.MIDDLE);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Сохранение результата");

        TextField nameField = new TextField("Имя функции");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setPlaceholder("Введите имя для сохранения");
        nameField.setWidth("300px");

        Button saveButton = new Button("Сохранить", e -> {
            String name = nameField.getValue();
            if (name == null || name.trim().isEmpty()) {
                Notification.show("Введите имя функции", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                String login = (String) VaadinSession.getCurrent().getAttribute("login");
                int userId = FunctionUtils.getUserIdByLogin(login);

                MathFunctionsDTO functionDTO = new MathFunctionsDTO();
                functionDTO.setFunctionName(name.trim());
                functionDTO.setLeftBorder(resultFunction.leftBound());
                functionDTO.setRightBorder(resultFunction.rightBound());
                functionDTO.setAmountOfDots(resultFunction.getCount());
                functionDTO.setFunctionType("manual");
                functionDTO.setOwnerId(userId);

                String jsonFunc = mapper.writeValueAsString(functionDTO);
                var funcResp = BasicAuthClient.sendPost("/math-functions/create/" + userId, jsonFunc);

                if (funcResp.statusCode() != 201 && funcResp.statusCode() != 200) {
                    String error = BasicAuthClient.extractErrorMessage(funcResp.body());
                    Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
                    return;
                }

                var searchResp = BasicAuthClient.sendGet("/math-functions/get-by-function-name/" + name.trim());
                if (searchResp.statusCode() != 200) {
                    Notification.show("Не удалось найти функцию", 4000, Notification.Position.MIDDLE);
                    return;
                }
                List<MathFunctionsDTO> found = mapper.readValue(searchResp.body(), new TypeReference<List<MathFunctionsDTO>>() {});
                if (found.isEmpty()) {
                    Notification.show("Функция не найдена", 4000, Notification.Position.MIDDLE);
                    return;
                }
                int functionId = found.get(0).getFunctionId();

                List<PointsDTO> points = new ArrayList<>();
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
                    dialog.close();
                } else {
                    String error = BasicAuthClient.extractErrorMessage(pointsResp.body());
                    Notification.show("Ошибка точек: " + error, 5000, Notification.Position.MIDDLE);
                }
            } catch (Exception ex) {
                Notification.show("Ошибка: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
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

        ArrayTabulatedFunction arrayFunction;
        if (resultFunction instanceof ArrayTabulatedFunction) {
            arrayFunction = (ArrayTabulatedFunction) resultFunction;
        } else {
            double[] xValues = new double[resultFunction.getCount()];
            double[] yValues = new double[resultFunction.getCount()];
            for (int i = 0; i < resultFunction.getCount(); i++) {
                xValues[i] = resultFunction.getX(i);
                yValues[i] = resultFunction.getY(i);
            }
            arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Сохранение результата в файл");

        TextField fileNameField = new TextField("Имя файла");
        fileNameField.setRequiredIndicatorVisible(true);
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
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                    if ("json".equals(format)) {
                        FunctionsIO.serializeJson(writer, arrayFunction);
                    } else {
                        FunctionsIO.serializeXml(writer, arrayFunction);
                    }
                    writer.flush();
                }

                byte[] data = outputStream.toByteArray();
                String base64Data = Base64.getEncoder().encodeToString(data);
                String safeFileName = fileName.replace("\"", "");
                String contentType = "json".equals(format) ? "application/json" : "application/xml";

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