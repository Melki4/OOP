package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.SimpleFunctionsDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route(value = "create-function", layout = MainLayout.class)
@PageTitle("Создать функцию | MathFunction App")
public class CreateFunctionView extends VerticalLayout {

    private final ComboBox<String> creationMode = new ComboBox<>("Способ создания");
    private final VerticalLayout formContainer = new VerticalLayout();
    private final Button createButton = new Button("Создать функцию", this::handleCreate);
    private final Button backButton = new Button("Назад", e -> getUI().ifPresent(ui -> ui.navigate("main")));

    // Способ 1: ручной ввод
    private List<NumberField> xFields = new ArrayList<>();
    private List<NumberField> yFields = new ArrayList<>();
    private IntegerField pointCountField;

    // Способ 2: простая функция
    private ComboBox<String> simpleFunctionSelect;
    private NumberField leftBorderField;
    private NumberField rightBorderField;
    private IntegerField amountOfDotsField;

    private String selectedMode = "points";
    private final ObjectMapper mapper = new ObjectMapper();

    public CreateFunctionView() {
        addClassName("create-function-view");
        setPadding(true);
        setSpacing(true);
        setSizeFull();

        creationMode.setItems("Ввести точки вручную", "Создать из простой функции");
        creationMode.setValue("Ввести точки вручную");
        creationMode.addValueChangeListener(e -> {
            selectedMode = e.getValue().contains("точки") ? "points" : "simple";
            updateForm();
        });

        formContainer.setSizeFull();
        formContainer.setPadding(false);

        HorizontalLayout buttonBar = new HorizontalLayout(backButton, createButton);
        buttonBar.setWidth("100%");

        add(new H2("Создание табулированной функции"), creationMode, formContainer, buttonBar);
        updateForm();
    }

    private void updateForm() {
        formContainer.removeAll();
        xFields.clear();
        yFields.clear();

        if ("points".equals(selectedMode)) {
            pointCountField = new IntegerField("Количество точек");
            pointCountField.setMin(2);
            pointCountField.setValue(2);
            pointCountField.addValueChangeListener(e -> {
                if (e.getValue() != null && e.getValue() >= 2) {
                    createPointInputs(e.getValue());
                }
            });

            formContainer.add(new FormLayout(pointCountField));
            createPointInputs(2);

        } else {
            simpleFunctionSelect = new ComboBox<>("Простая функция");
            loadSimpleFunctions();

            leftBorderField = new NumberField("Левая граница");
            rightBorderField = new NumberField("Правая граница");
            amountOfDotsField = new IntegerField("Количество точек");
            amountOfDotsField.setMin(2);
            amountOfDotsField.setValue(10);

            FormLayout form = new FormLayout();
            form.add(simpleFunctionSelect, leftBorderField, rightBorderField, amountOfDotsField);
            form.setMaxWidth("500px");
            formContainer.add(form);
        }
    }

    private void createPointInputs(int count) {
        VerticalLayout pointsLayout = new VerticalLayout();
        pointsLayout.setSpacing(true);

        xFields.clear();
        yFields.clear();

        for (int i = 0; i < count; i++) {
            NumberField xField = new NumberField("x" + (i + 1));
            NumberField yField = new NumberField("y" + (i + 1));
            xFields.add(xField);
            yFields.add(yField);

            HorizontalLayout row = new HorizontalLayout(xField, yField);
            row.setSpacing(true);
            pointsLayout.add(row);
        }

        formContainer.add(pointsLayout);
    }

    private void loadSimpleFunctions() {
        try {
            var response = BasicAuthClient.sendGet("/simple-functions");
            if (response.statusCode() == 200) {
                List<SimpleFunctionsDTO> functions = mapper.readValue(
                        response.body(),
                        new TypeReference<List<SimpleFunctionsDTO>>() {}
                );
                List<String> names = functions.stream()
                        .map(SimpleFunctionsDTO::getLocalName)
                        .sorted()
                        .collect(Collectors.toList());
                simpleFunctionSelect.setItems(names);
            } else {
                Notification.show("Не удалось загрузить простые функции", 4000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Ошибка загрузки: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void handleCreate(ClickEvent<Button> event) {
        if ("points".equals(selectedMode)) {
            createFromPoints();
        } else {
            createFromSimpleFunction();
        }
    }

    private void createFromPoints() {
        try {
            List<Double> xList = xFields.stream().map(f -> f.getValue() != null ? f.getValue() : 0.0).collect(Collectors.toList());
            List<Double> yList = yFields.stream().map(f -> f.getValue() != null ? f.getValue() : 0.0).collect(Collectors.toList());

            if (xList.size() < 2) {
                Notification.show("Нужно минимум 2 точки", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Проверка уникальности и сортировки
            List<Double> sortedX = new ArrayList<>(xList);
            sortedX.sort(Double::compareTo);
            if (!sortedX.equals(xList)) {
                Notification.show("x должны быть строго возрастающими", 4000, Notification.Position.MIDDLE);
                return;
            }
            if (new HashSet<>(xList).size() != xList.size()) {
                Notification.show("x должны быть уникальными", 4000, Notification.Position.MIDDLE);
                return;
            }

            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            int userId = getUserIdByLogin(login);

            // Генерация имени (можно сделать редактируемым, но по ТЗ — не обязательно)
            String functionName = "Функция от " + new java.util.Date();

            // Создание функции
            MathFunctionsDTO funcDto = new MathFunctionsDTO();
            funcDto.setFunctionName(functionName);
            funcDto.setAmountOfDots(xList.size());
            funcDto.setLeftBorder(Collections.min(xList));
            funcDto.setRightBorder(Collections.max(xList));
            funcDto.setFunctionType("manual");

            String jsonFunc = mapper.writeValueAsString(funcDto);
            var funcResp = BasicAuthClient.sendPost("/math-functions/create/" + userId, jsonFunc);

            if (funcResp.statusCode() != 201) {
                String error = BasicAuthClient.extractErrorMessage(funcResp.body());
                Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
                return;
            }

            // Получить ID функции: запрос по имени
            var searchResp = BasicAuthClient.sendGet("/math-functions/get-by-function-name/" + functionName);
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

            // Создание точек
            List<PointsDTO> points = IntStream.range(0, xList.size())
                    .mapToObj(i -> {
                        PointsDTO p = new PointsDTO();
                        p.setXValue(xList.get(i));
                        p.setYValue(yList.get(i));
                        p.setFunctionId(functionId);
                        return p;
                    })
                    .collect(Collectors.toList());

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

        } catch (Exception e) {
            Notification.show("Ошибка: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    private void createFromSimpleFunction() {
        try {
            String funcName = simpleFunctionSelect.getValue();
            Double left = leftBorderField.getValue();
            Double right = rightBorderField.getValue();
            Integer dots = amountOfDotsField.getValue();

            if (funcName == null || funcName.isEmpty()) {
                Notification.show("Выберите функцию", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (left == null || right == null || dots == null) {
                Notification.show("Заполните все поля", 3000, Notification.Position.MIDDLE);
                return;
            }

            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            int userId = getUserIdByLogin(login);

            MathFunctionsDTO funcDto = new MathFunctionsDTO();
            funcDto.setFunctionName(funcName);
            funcDto.setAmountOfDots(dots);
            funcDto.setLeftBorder(left);
            funcDto.setRightBorder(right);
            funcDto.setFunctionType("simple");

            String json = mapper.writeValueAsString(funcDto);
            var response = BasicAuthClient.sendPost("/math-functions/create/" + userId, json);

            if (response.statusCode() == 201) {
                Notification.show("Функция успешно создана!", 4000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("my-functions"));
            } else {
                String error = BasicAuthClient.extractErrorMessage(response.body());
                Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
            }

        } catch (Exception e) {
            Notification.show("Ошибка: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    private int getUserIdByLogin(String login) throws IOException, InterruptedException, URISyntaxException {
        var response = BasicAuthClient.sendGet("/users/get-id-by-login/" + login);
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), Integer.class);
        } else {
            throw new RuntimeException("Не удалось получить ID пользователя");
        }
    }
}