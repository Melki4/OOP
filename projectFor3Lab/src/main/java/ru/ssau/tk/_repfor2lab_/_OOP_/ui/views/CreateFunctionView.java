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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.UserDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.SimpleFunctionRegistry;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.SimpleFunctionsDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
        import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route(value = "create-function", layout = MainLayout.class)
@PageTitle("Создать функцию | MathFunction App")
public class CreateFunctionView extends VerticalLayout {

    private static final int AUTO_FILL_THRESHOLD = 20;

    private final ComboBox<String> creationMode = new ComboBox<>("Способ создания");
    private final VerticalLayout formContainer = new VerticalLayout();

    private TextField functionNameField;
    private List<NumberField> xFields = new ArrayList<>();
    private List<NumberField> yFields = new ArrayList<>();
    private IntegerField pointCountField;
    private Button autoFillButton;

    private ComboBox<String> simpleFunctionSelect;
    private NumberField leftBorderField;
    private NumberField rightBorderField;
    private IntegerField amountOfDotsField;

    private String selectedMode = "points";
    private final ObjectMapper mapper = new ObjectMapper();

    public CreateFunctionView() {
        addClassName("create-function-view");
        setSpacing(true);
        setPadding(true);
        setSizeFull();
        // === ВСЁ ПО ЦЕНТРУ ===
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        formContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        formContainer.setWidthFull();
        formContainer.setSpacing(true);
        formContainer.setPadding(false);

        creationMode.setItems("Ввести точки вручную", "Создать из простой функции");
        creationMode.setWidth("300px");
        creationMode.addValueChangeListener(e -> {
            selectedMode = e.getValue().contains("точки") ? "points" : "simple";
            updateForm();
        });

        Button createButton = new Button("Создать функцию", this::handleCreate);
        Button backButton = new Button("Назад", e -> getUI().ifPresent(ui -> ui.navigate("main")));

        add(new H2("Создание табулированной функции"));
        add(creationMode);
        add(formContainer);
        add(createButton, backButton);
        updateForm();
    }

    private void updateForm() {
        formContainer.removeAll();
        xFields.clear();
        yFields.clear();

        // === Имя функции — обязательно для обоих способов ===
        functionNameField = new TextField("Имя функции");
        functionNameField.setRequiredIndicatorVisible(true);
        functionNameField.setPlaceholder("Введите имя...");
        functionNameField.setWidth("320px");
        formContainer.add(functionNameField);


        if ("points".equals(selectedMode)) {
            // === Способ 1: ручной ввод ===
            pointCountField = new IntegerField("Количество точек");
            pointCountField.setMin(2);
            pointCountField.setMax(1000);
            pointCountField.setValue(2);
            pointCountField.addValueChangeListener(e -> {
                Integer val = e.getValue();
                if (val != null && val >= 2) {
                    createPointInputs(val);
                }
            });
            pointCountField.setWidth("200px");

            VerticalLayout manualSettings = new VerticalLayout(pointCountField);
            manualSettings.setSpacing(false);
            manualSettings.setPadding(false);
            manualSettings.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

            formContainer.add(manualSettings);

        } else {
            // === Способ 2: простая функция ===
            simpleFunctionSelect = new ComboBox<>("Простая функция");
            loadSimpleFunctions();

            leftBorderField = new NumberField("Левая граница");
            rightBorderField = new NumberField("Правая граница");
            amountOfDotsField = new IntegerField("Количество точек");
            amountOfDotsField.setMin(2);
            amountOfDotsField.setMax(1000);
            amountOfDotsField.setValue(10);

            simpleFunctionSelect.setWidth("320px");
            leftBorderField.setWidth("220px");
            rightBorderField.setWidth("220px");
            amountOfDotsField.setWidth("220px");

            VerticalLayout generatedSettings = new VerticalLayout();
            generatedSettings.setSpacing(false);
            generatedSettings.setPadding(false);
            generatedSettings.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

            generatedSettings.add(simpleFunctionSelect);

            HorizontalLayout bordersRow = new HorizontalLayout(leftBorderField, rightBorderField);
            bordersRow.setDefaultVerticalComponentAlignment(Alignment.END);
            bordersRow.setSpacing(true);
            bordersRow.setPadding(false);
            bordersRow.setJustifyContentMode(JustifyContentMode.CENTER);

            HorizontalLayout amountRow = new HorizontalLayout(amountOfDotsField);
            amountRow.setDefaultVerticalComponentAlignment(Alignment.END);
            amountRow.setSpacing(true);
            amountRow.setPadding(false);
            amountRow.setJustifyContentMode(JustifyContentMode.CENTER);

            generatedSettings.add(bordersRow, amountRow);
            formContainer.add(generatedSettings);
        }
    }

    private void createPointInputs(int count) {
        formContainer.getChildren()
                .filter(comp -> comp instanceof VerticalLayout && comp != formContainer)
                .forEach(formContainer::remove);

        xFields.clear();
        yFields.clear();

        VerticalLayout pointsLayout = new VerticalLayout();
        pointsLayout.setSpacing(true);
        pointsLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        for (int i = 0; i < count; i++) {
            NumberField xField = new NumberField("x" + (i + 1));
            NumberField yField = new NumberField("y" + (i + 1));
            xField.setWidth("160px");
            yField.setWidth("160px");
            xFields.add(xField);
            yFields.add(yField);
            pointsLayout.add(new HorizontalLayout(xField, yField));
        }

        formContainer.add(pointsLayout);

        if (count > AUTO_FILL_THRESHOLD && autoFillButton == null) {
            autoFillButton = new Button("Заполнить автоматически (линейно)", e -> autoFillPoints());
            formContainer.add(autoFillButton);
        } else if (count <= AUTO_FILL_THRESHOLD && autoFillButton != null) {
            formContainer.remove(autoFillButton);
            autoFillButton = null;
        }
    }

    private void autoFillPoints() {
        // (реализация как в предыдущей версии)
        Double x0 = xFields.get(0).getValue();
        Double y0 = yFields.get(0).getValue();
        Double xN = xFields.get(xFields.size() - 1).getValue();
        Double yN = yFields.get(yFields.size() - 1).getValue();

        if (x0 == null || y0 == null || xN == null || yN == null) {
            Notification.show("Заполните первую и последнюю точку", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (x0.equals(xN)) {
            Notification.show("x0 и xN должны быть разными", 3000, Notification.Position.MIDDLE);
            return;
        }

        int n = xFields.size();
        double step = (xN - x0) / (n - 1);
        for (int i = 0; i < n; i++) {
            double x = x0 + i * step;
            double y = y0 + (yN - y0) * i / (n - 1);
            xFields.get(i).setValue(x);
            yFields.get(i).setValue(y);
        }
        Notification.show("Автоматическое заполнение завершено", 2000, Notification.Position.MIDDLE);
    }

    private void loadSimpleFunctions() {
        try {
            var response = BasicAuthClient.sendGet("/simple-functions/sorted");
            if (response.statusCode() == 200) {
                List<SimpleFunctionsDTO> functions = mapper.readValue(
                        response.body(),
                        new TypeReference<List<SimpleFunctionsDTO>>() {}
                );
                List<String> names = functions.stream()
                        .map(SimpleFunctionsDTO::getLocalName)
                        .collect(Collectors.toList());
                simpleFunctionSelect.setItems(names);
            }
        } catch (Exception e) {
            Notification.show("Ошибка загрузки функций", 4000, Notification.Position.MIDDLE);
        }
    }

    private void handleCreate(ClickEvent<Button> event) {
        String userProvidedName = functionNameField.getValue();
        if (userProvidedName == null || userProvidedName.trim().isEmpty()) {
            Notification.show("Укажите имя функции", 3000, Notification.Position.MIDDLE);
            return;
        }

        if ("points".equals(selectedMode)) {
            createFromPoints(userProvidedName.trim());
        } else {
            createFromSimpleFunction(userProvidedName.trim());
        }
    }

    private void createFromPoints(String functionName) {
        try {
            List<Double> xList = new ArrayList<>();
            for (NumberField field : xFields) {
                Double value = field.getValue();
                if (value == null) {
                    Notification.show("Заполните все значения X", 3000, Notification.Position.MIDDLE);
                    return;
                }
                xList.add(value);
            }

            List<Double> yList = new ArrayList<>();
            for (NumberField field : yFields) {
                Double value = field.getValue();
                if (value == null) {
                    Notification.show("Заполните все значения Y", 3000, Notification.Position.MIDDLE);
                    return;
                }
                yList.add(value);
            }

            if (xList.size() < 2) {
                Notification.show("Нужно минимум 2 точки", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Проверка: x строго возрастает
            for (int i = 0; i < xList.size() - 1; i++) {
                if (xList.get(i) >= xList.get(i + 1)) {
                    Notification.show("x должны быть строго возрастающими", 4000, Notification.Position.MIDDLE);
                    return;
                }
            }

            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            int userId = getUserIdByLogin(login);

            // === Создание функции через API ===
            MathFunctionsDTO funcDto = new MathFunctionsDTO();
            funcDto.setFunctionName(functionName);
            funcDto.setAmountOfDots(xList.size());
            funcDto.setLeftBorder(Collections.min(xList));
            funcDto.setRightBorder(Collections.max(xList));
            funcDto.setFunctionType("manual");
            funcDto.setOwnerId(userId);

            String jsonFunc = mapper.writeValueAsString(funcDto);
            var funcResp = BasicAuthClient.sendPost("/math-functions/create/" + userId, jsonFunc);

            if (funcResp.statusCode() != 201) {
                String error = BasicAuthClient.extractErrorMessage(funcResp.body());
                Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
                return;
            }

            // Получаем ID функции
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

            // === Отправка точек ===
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

    private void createFromSimpleFunction(String userProvidedName) {
        try {
            String localizedFuncName = simpleFunctionSelect.getValue(); // ← "Квадратичная функция"
            Double left = leftBorderField.getValue();
            Double right = rightBorderField.getValue();
            Integer dots = amountOfDotsField.getValue();

            if (localizedFuncName == null || localizedFuncName.isEmpty()) {
                Notification.show("Выберите функцию", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (left == null || right == null || dots == null) {
                Notification.show("Заполните все поля", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (left >= right) {
                Notification.show("Левая граница должна быть < правой", 3000, Notification.Position.MIDDLE);
                return;
            }

            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            int userId = getUserIdByLogin(login);

            MathFunctionsDTO dto = new MathFunctionsDTO();
            dto.setFunctionName(userProvidedName);
            dto.setAmountOfDots(dots);
            dto.setLeftBorder(left);
            dto.setRightBorder(right);
            dto.setFunctionType("tabulated");
            dto.setOwnerId(userId);
//            dto.setLocalizedName(localizedFuncName); // ← КЛЮЧЕВОЕ ПОЛЕ!

            String json = new ObjectMapper().writeValueAsString(dto);
            var response = BasicAuthClient.sendPost("/math-functions/create/" + userId, json);

            if (response.statusCode() != 201) {
                String error = BasicAuthClient.extractErrorMessage(response.body());
                Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
                return;
            }

            //!!!

            // Получаем ID функции
            var searchResp = BasicAuthClient.sendGet("/math-functions/get-by-function-name/" + userProvidedName);
            if (searchResp.statusCode() != 200) {
                Notification.show("Не удалось найти функцию", 4000, Notification.Position.MIDDLE);
                return;
            }
            List<MathFunctionsDTO> found = mapper.readValue(searchResp.body(), new TypeReference<>() {});
            if (found.isEmpty()) {
                Notification.show("Функция не найдена", 4000, Notification.Position.MIDDLE);
                return;
            }
            int functionId = found.get(0).getFunctionId();

            //Нужно создать ф-цию по типу выбранной
            
            String f_type = "";
            
            var response1 = BasicAuthClient.sendGet("/users/get/" + login);
            if (response1.statusCode() == 200) {
                UserDTO user = mapper.readValue(response1.body(), new TypeReference<>() {});
                f_type = user.getFactoryType();
            }

            List<PointsDTO> points = SimpleFunctionRegistry.CreatePoints(localizedFuncName, left, right, dots,
                    f_type, functionId);

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
        }
    }

    private int getUserIdByLogin(String login) throws IOException, InterruptedException, URISyntaxException {
        var response = BasicAuthClient.sendGet("/users/get-id-by-login/" + login);
        return new ObjectMapper().readValue(response.body(), Integer.class);
    }

    // createFromPoints() — оставьте как есть (с отправкой x/y)
}