package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.UserDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.io.FunctionsIO;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedFunctionOperationService;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "function-operations", layout = MainLayout.class)
@PageTitle("Операции с функциями | MathFunction App")
public class FunctionOperationsView extends VerticalLayout {

    private final ObjectMapper mapper = new ObjectMapper();

    private TabulatedFunction firstFunction = null;
    private TabulatedFunction secondFunction = null;
    private TabulatedFunction resultFunction = null;

    private Grid<Point> firstFunctionGrid;
    private Grid<Point> secondFunctionGrid;
    private Grid<Point> resultGrid;

    private ListDataProvider<Point> firstDataProvider;
    private ListDataProvider<Point> secondDataProvider;
    private ListDataProvider<Point> resultDataProvider;

    private final TabulatedFunctionOperationService operationService;
    private TabulatedFunctionFactory factory;

    private ComboBox<String> operationSelect;

    private Dialog functionSelectionDialog;
    private List<MathFunctionsDTO> availableFunctions = new ArrayList<>();
    private Grid<MathFunctionsDTO> functionsGrid;
    private int targetPanelNumber = 1; // 1 для первой функции, 2 для второй

    public FunctionOperationsView() {
        // Получаем тип фабрики из сессии
        String login = (String) VaadinSession.getCurrent().getAttribute("login");
        String factoryType = "array"; // по умолчанию

        try {
            var response = BasicAuthClient.sendGet("/users/get/" + login);
            if (response.statusCode() == 200) {
                UserDTO user = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                        response.body(), UserDTO.class);
                factoryType = user.getFactoryType();
            }
        } catch (Exception e) {
            Notification.show("Не удалось загрузить настройки пользователя", 3000, Notification.Position.MIDDLE);
        }

        if ("list".equals(factoryType)) {
            factory = new LinkedListTabulatedFunctionFactory();
        } else {
            factory = new ArrayTabulatedFunctionFactory();
        }

        operationService = new TabulatedFunctionOperationService(factory);

        addClassName("function-operations-view");
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setPadding(true);
        setSpacing(true);

        // Загружаем список доступных функций
        loadAvailableFunctions();

        // Создаем интерфейс
        createUI();
    }

    private void createUI() {
        // Заголовок
        add(new H3("Операции над табулированными функциями"));

        // Верхняя панель с выбором операции
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

        // Три области для функций
        HorizontalLayout functionsLayout = new HorizontalLayout();
        functionsLayout.setWidth("100%");
        functionsLayout.setHeight("600px");
        functionsLayout.setSpacing(true);
        functionsLayout.setPadding(true);

        // Первая функция
        VerticalLayout firstFunctionLayout = createFunctionPanel("Первая функция", true, 1);

        // Вторая функция
        VerticalLayout secondFunctionLayout = createFunctionPanel("Вторая функция", true, 2);

        // Результат
        VerticalLayout resultLayout = createFunctionPanel("Результат", false, 3);

        functionsLayout.add(firstFunctionLayout);
        functionsLayout.add(secondFunctionLayout);
        functionsLayout.add(resultLayout);
        functionsLayout.setFlexGrow(1, firstFunctionLayout, secondFunctionLayout, resultLayout);

        add(functionsLayout, new VerticalLayout());

        // Создаем диалог для выбора функции
        createFunctionSelectionDialog();
    }

    private VerticalLayout createFunctionPanel(String title, boolean isEditable, int panelNumber) {
        VerticalLayout panel = new VerticalLayout();
        panel.addClassName("function-panel");
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.setPadding(true);
        panel.setSpacing(true);
        panel.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-m)");

        // Заголовок панели
        H3 panelTitle = new H3(title);
        panel.add(panelTitle);

        // Таблица для отображения функции
        Grid<Point> grid = new Grid<>(Point.class);
        grid.setWidth("100%");
        grid.setHeight("350px");
        grid.setColumns(); // Убираем стандартные колонки

        // Добавляем колонку X (нередактируемая для всех панелей)
        grid.addColumn(new NumberRenderer<>(Point::getX, "%.4f"))
                .setHeader("X")
                .setResizable(true)
                .setSortable(true)
                .setWidth("50%");

        // Добавляем колонку Y в зависимости от типа панели
        if (isEditable) {
            // Для операндов - редактируемая колонка Y
            grid.addComponentColumn(point -> {
                TextField field = new TextField();
                field.setValue(String.format("%.4f", point.getY()));
                field.setWidth("100%");
                field.addValueChangeListener(e -> {
                    try {
                        double newY = Double.parseDouble(e.getValue());
                        point.setY(newY);
                        updateFunctionFromGrid(panelNumber);
                    } catch (NumberFormatException ex) {
                        Notification.show("Введите корректное число", 3000, Notification.Position.MIDDLE);
                    }
                });
                return field;
            }).setHeader("Y").setResizable(true).setWidth("50%");
        } else {
            // Для результата - нередактируемая колонка Y
            grid.addColumn(new NumberRenderer<>(Point::getY, "%.4f"))
                    .setHeader("Y")
                    .setResizable(true)
                    .setSortable(true)
                    .setWidth("50%");
        }

        // Кнопки управления
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setSpacing(true);

        if (isEditable) {
            // Для редактируемых панелей добавляем кнопки управления
            Button createFunctionButton = new Button("Создать функцию");
            createFunctionButton.addClickListener(e -> {
                targetPanelNumber = panelNumber;
                openCreateFunctionDialog();
            });

            Button loadExistingButton = new Button("Загрузить существующую");
            loadExistingButton.addClickListener(e -> {
                targetPanelNumber = panelNumber;
                openFunctionSelectionDialog();
            });

//            Button loadFromJsonButton = new Button("Загрузить из JSON");
//            loadFromJsonButton.addClickListener(e -> {
//                targetPanelNumber = panelNumber;
//                openFileUploadDialog("json");
//            });
//
//            Button loadFromXmlButton = new Button("Загрузить из XML");
//            loadFromXmlButton.addClickListener(e -> {
//                targetPanelNumber = panelNumber;
//                openFileUploadDialog("xml");
//            });

            buttonLayout.add(createFunctionButton, loadExistingButton, loadFromJsonButton, loadFromXmlButton);
            // Кнопка "Сохранить" удалена согласно требованиям задания
        }

        panel.add(grid);
        panel.add(buttonLayout);

        // Создаем пустой провайдер для начала
        ListDataProvider<Point> dataProvider = new ListDataProvider<>(new ArrayList<>());
        grid.setDataProvider(dataProvider);

        // Сохраняем ссылки на гриды и провайдеры для использования
        if (panelNumber == 1) {
            firstFunctionGrid = grid;
            firstDataProvider = dataProvider;
        } else if (panelNumber == 2) {
            secondFunctionGrid = grid;
            secondDataProvider = dataProvider;
        } else {
            resultGrid = grid;
            resultDataProvider = dataProvider;
        }

        return panel;
    }

    private void openCreateFunctionDialog() {
        Dialog dialog = new Dialog();
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

                // Создаем точки
                List<Point> points = new ArrayList<>();
                double step = (right - left) / (pointsCount - 1);

                for (int i = 0; i < pointsCount; i++) {
                    double x = left + i * step;
                    points.add(new Point(x, 0.0)); // Начальные значения Y = 0.0
                }

                // Создаем функцию
                TabulatedFunction function = factory.create(
                        points.stream().mapToDouble(Point::getX).toArray(),
                        points.stream().mapToDouble(Point::getY).toArray()
                );

                // Обновляем соответствующую панель
                if (targetPanelNumber == 1) {
                    firstFunction = function;
                    updateGridFromFunction(firstFunctionGrid, firstDataProvider, firstFunction);
                } else if (targetPanelNumber == 2) {
                    secondFunction = function;
                    updateGridFromFunction(secondFunctionGrid, secondDataProvider, secondFunction);
                }

                dialog.close();
                Notification.show("Функция создана успешно", 3000, Notification.Position.MIDDLE);
            } catch (NumberFormatException ex) {
                Notification.show("Введите корректные числа", 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancelBtn = new Button("Отмена", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(nameField, leftBorderField, rightBorderField,
                pointsCountField, createBtn, cancelBtn);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void createEmptyFunction(int panelNumber) {
        // Создаем диалог для ввода параметров пустой функции
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Создание пустой функции");

        TextField nameField = new TextField("Имя функции");
        nameField.setRequired(true);

        IntegerField pointsCountField = new IntegerField("Количество точек");
        pointsCountField.setMin(2);
        pointsCountField.setMax(50);
        pointsCountField.setValue(5);

        Button createBtn = new Button("Создать", e -> {
            if (nameField.getValue().trim().isEmpty()) {
                Notification.show("Введите имя функции", 3000, Notification.Position.MIDDLE);
                return;
            }

            int pointsCount = pointsCountField.getValue();
            if (pointsCount < 2) {
                Notification.show("Минимум 2 точки", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Создаем пустые точки
            List<Point> points = new ArrayList<>();
            double step = 10.0 / (pointsCount - 1);
            for (int i = 0; i < pointsCount; i++) {
                points.add(new Point(i * step, 0.0));
            }

            // Обновляем соответствующую панель
            if (panelNumber == 1) {
                firstFunction = factory.create(
                        points.stream().mapToDouble(Point::getX).toArray(),
                        points.stream().mapToDouble(Point::getY).toArray()
                );
                updateGridFromFunction(firstFunctionGrid, firstDataProvider, firstFunction);
            } else if (panelNumber == 2) {
                secondFunction = factory.create(
                        points.stream().mapToDouble(Point::getX).toArray(),
                        points.stream().mapToDouble(Point::getY).toArray()
                );
                updateGridFromFunction(secondFunctionGrid, secondDataProvider, secondFunction);
            }

            dialog.close();
            Notification.show("Пустая функция создана успешно", 3000, Notification.Position.MIDDLE);
        });

        Button cancelBtn = new Button("Отмена", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(nameField, pointsCountField, createBtn, cancelBtn);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void updateFunctionFromGrid(int panelNumber) {
        // Получаем точки из грида
        List<Point> points = new ArrayList<>();
        if (panelNumber == 1) {
            firstDataProvider.getItems().forEach(points::add);
        } else if (panelNumber == 2) {
            secondDataProvider.getItems().forEach(points::add);
        }

        if (points.isEmpty()) return;

        // Создаем новые массивы на основе текущих точек
        double[] xValues = points.stream().mapToDouble(Point::getX).toArray();
        double[] yValues = points.stream().mapToDouble(Point::getY).toArray();

        // Создаем новую функцию
        TabulatedFunction newFunction = factory.create(xValues, yValues);

        // Заменяем старую функцию на новую
        if (panelNumber == 1) {
            firstFunction = newFunction;
        } else if (panelNumber == 2) {
            secondFunction = newFunction;
        }
    }

    private void createFunctionSelectionDialog() {
        functionSelectionDialog = new Dialog();
        functionSelectionDialog.setHeaderTitle("Выберите функцию");

        functionsGrid = new Grid<>(MathFunctionsDTO.class);
        functionsGrid.setColumns("functionName", "leftBorder", "rightBorder", "amountOfDots");
        functionsGrid.getColumnByKey("functionName").setHeader("Имя функции");
        functionsGrid.getColumnByKey("leftBorder").setHeader("Левая граница");
        functionsGrid.getColumnByKey("rightBorder").setHeader("Правая граница");
        functionsGrid.getColumnByKey("amountOfDots").setHeader("Точек");
        functionsGrid.setHeight("400px");
        functionsGrid.setWidth("600px");

        functionsGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                loadSelectedFunction(event.getValue());
                functionSelectionDialog.close();
            }
        });

        Button closeButton = new Button("Отмена", e -> functionSelectionDialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout footerLayout = new HorizontalLayout(closeButton);
        footerLayout.setWidth("100%");
        footerLayout.setJustifyContentMode(JustifyContentMode.END);

        functionSelectionDialog.add(functionsGrid);
        functionSelectionDialog.getFooter().add(footerLayout);
    }

    private void openFunctionSelectionDialog() {
        if (availableFunctions.isEmpty()) {
            // Пробуем перезагрузить функции при пустом списке
            loadAvailableFunctions();
            if (availableFunctions.isEmpty()) {
                Notification.show("Нет доступных функций для загрузки. Создайте хотя бы одну функцию.", 5000, Notification.Position.MIDDLE);
                return;
            }
        }

        functionsGrid.setItems(availableFunctions);
        functionSelectionDialog.open();
    }

    private void updateGridFromFunction(Grid<Point> grid, ListDataProvider<Point> dataProvider, TabulatedFunction function) {
        if (function == null || function.getCount() == 0) {
            // Заменяем провайдер на новый с пустым списком
            ListDataProvider<Point> newProvider = new ListDataProvider<>(new ArrayList<Point>());
            grid.setDataProvider(newProvider);
            return;
        }

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < function.getCount(); i++) {
            points.add(new Point(function.getX(i), function.getY(i)));
        }

        // Заменяем провайдер на новый со списком точек
        ListDataProvider<Point> newProvider = new ListDataProvider<>(points);
        grid.setDataProvider(newProvider);
    }

    private void loadSelectedFunction(MathFunctionsDTO selectedFunction) {
        try {
            // Получаем точки функции
            var response = BasicAuthClient.sendGet("/points/get-points-by-function-id/" + selectedFunction.getFunctionId());
            if (response.statusCode() == 200) {
                List<PointsDTO> pointsDTOs = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                        response.body(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<PointsDTO>>() {}
                );

                // Проверяем, получили ли мы точки
                if (pointsDTOs == null || pointsDTOs.isEmpty()) {
                    Notification.show("Функция не содержит точек", 3000, Notification.Position.MIDDLE);
                    return;
                }

                // Преобразуем в массивы x и y
                double[] xValues = pointsDTOs.stream().mapToDouble(PointsDTO::getXValue).toArray();
                double[] yValues = pointsDTOs.stream().mapToDouble(PointsDTO::getYValue).toArray();

                // Создаем табулированную функцию
                TabulatedFunction function = factory.create(xValues, yValues);

                // Устанавливаем функцию в нужную панель
                if (targetPanelNumber == 1) {
                    firstFunction = function;
                    updateGridFromFunction(firstFunctionGrid, firstDataProvider, firstFunction);
                } else if (targetPanelNumber == 2) {
                    secondFunction = function;
                    updateGridFromFunction(secondFunctionGrid, secondDataProvider, secondFunction);
                }

                Notification.show("Функция \"" + selectedFunction.getFunctionName() + "\" загружена", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("Ошибка загрузки точек функции. Код: " + response.statusCode(), 5000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Ошибка загрузки функции: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    private void saveFunction(int panelNumber) {
        Notification.show("Сохранение функции будет реализовано в будущем", 3000, Notification.Position.MIDDLE);
    }

    private void calculateOperation() {
        if (firstFunction == null || secondFunction == null) {
            Notification.show("Обе функции должны быть заданы", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Проверка совместимости функций
        if (firstFunction.getCount() != secondFunction.getCount()) {
            Notification.show("Функции должны иметь одинаковое количество точек", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            String operation = operationSelect.getValue();
            switch (operation) {
                case "Сложение":
                    resultFunction = operationService.addition(firstFunction, secondFunction);
                    break;
                case "Вычитание":
                    resultFunction = operationService.subtraction(firstFunction, secondFunction);
                    break;
                case "Умножение":
                    resultFunction = operationService.multiplication(firstFunction, secondFunction);
                    break;
                case "Деление":
                    resultFunction = operationService.division(firstFunction, secondFunction);
                    break;
                default:
                    Notification.show("Неизвестная операция", 3000, Notification.Position.MIDDLE);
                    return;
            }

            // Обновляем таблицу результата
            updateGridFromFunction(resultGrid, resultDataProvider, resultFunction);
            Notification.show("Операция выполнена успешно", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show("Ошибка при выполнении операции: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    private void loadAvailableFunctions() {
        try {
            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            if (login == null || login.trim().isEmpty()) {
                Notification.show("Пользователь не авторизован", 3000, Notification.Position.MIDDLE);
                return;
            }

            int userId = getUserIdByLogin(login);
            var response = BasicAuthClient.sendGet("/math-functions/get-by-user-id/" + userId);

            // Используем правильный эндпоинт для получения функций пользователя
//            var response = BasicAuthClient.sendGet("/math-functions/by-user-login/" + login);
            if (response.statusCode() == 200) {
                availableFunctions = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                        response.body(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<MathFunctionsDTO>>() {}
                );

                // Отладка: выводим количество функций
                System.out.println("Загружено функций: " + availableFunctions.size());
            } else {
                System.out.println("Ошибка загрузки функций. Код: " + response.statusCode());
                System.out.println("Ответ: " + response.body());
                Notification.show("Ошибка загрузки списка функций. Код: " + response.statusCode(), 5000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            System.out.println("Исключение при загрузке функций: " + e.getMessage());
            e.printStackTrace();
            Notification.show("Ошибка загрузки списка функций: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private int getUserIdByLogin(String login) throws IOException, InterruptedException, URISyntaxException {
        var response = BasicAuthClient.sendGet("/users/get-id-by-login/" + login);
        return mapper.readValue(response.body(), Integer.class);
    }
}