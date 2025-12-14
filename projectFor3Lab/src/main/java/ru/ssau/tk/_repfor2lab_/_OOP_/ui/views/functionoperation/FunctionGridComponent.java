package ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FunctionGridComponent extends VerticalLayout {
    private final Grid<Point> grid;
    private final ListDataProvider<Point> dataProvider;
    private final boolean isEditable;
    private final int panelNumber;
    private final BiConsumer<Integer, String> actionHandler;
    private BiConsumer<Integer, Point> addPointHandler;
    private BiConsumer<Integer, Double> deletePointHandler;

    private Button saveToDatabaseButton;
    private Button saveToFileButton;
    private NumberField insertXField;
    private NumberField insertYField;

    public FunctionGridComponent(String title, boolean isEditable, int panelNumber, BiConsumer<Integer, String> actionHandler) {
        this.isEditable = isEditable;
        this.panelNumber = panelNumber;
        this.actionHandler = actionHandler;

        addClassName("function-panel");
        setWidth("100%");
        setMaxWidth("520px");
        setPadding(true);
        setSpacing(true);
        getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-m)");

        add(new H3(title));

        grid = new Grid<>(Point.class);
        grid.setWidth("100%");
        grid.setHeight("360px");
        grid.setColumns();

        grid.addColumn(new NumberRenderer<>(Point::getX, "%.4f"))
                .setHeader("X")
                .setResizable(true)
                .setSortable(true)
                .setWidth("120px")
                .setFlexGrow(1);

        if (isEditable) {
            grid.addComponentColumn(point -> {
                TextField field = new TextField();
                field.setValue(String.format("%.4f", point.getY()));
                field.setWidth("120px");
                field.addValueChangeListener(e -> {
                    try {
                        double newY = Double.parseDouble(e.getValue());
                        point.setY(newY);
                    } catch (NumberFormatException ex) {
                        Notification.show("Введите корректное число", 3000, Notification.Position.MIDDLE);
                    }
                });
                return field;
            }).setHeader("Y").setResizable(true).setWidth("120px").setFlexGrow(1);

            grid.addComponentColumn(point -> {
                Button deleteButton = new Button("Удалить", event -> {
                    if (deletePointHandler != null) {
                        deletePointHandler.accept(panelNumber, point.getX());
                    }
                });
                deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
                return deleteButton;
            }).setHeader("Действие").setWidth("110px").setFlexGrow(0);

        } else {
            grid.addColumn(new NumberRenderer<>(Point::getY, "%.4f"))
                    .setHeader("Y")
                    .setResizable(true)
                    .setSortable(true)
                    .setWidth("120px")
                    .setFlexGrow(1);
        }

        dataProvider = new ListDataProvider<>(new ArrayList<>());
        grid.setDataProvider(dataProvider);

        add(grid);

        if (isEditable) {
            addInsertControls();
            addControlButtons();
        } else {
            addResultControlButtons();
        }
    }

    private void addInsertControls() {
        insertXField = new NumberField("x");
        insertYField = new NumberField("y");
        insertXField.setWidth("120px");
        insertYField.setWidth("120px");

        Button insertButton = new Button("Вставить", event -> {
            if (addPointHandler == null) {
                return;
            }

            Double x = insertXField.getValue();
            Double y = insertYField.getValue();

            if (x == null || y == null) {
                Notification.show("Укажите x и y", 3000, Notification.Position.MIDDLE);
                return;
            }

            addPointHandler.accept(panelNumber, new Point(x, y));
        });
        insertButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout insertLayout = new HorizontalLayout(insertXField, insertYField, insertButton);
        insertLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        insertLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        insertLayout.setSpacing(true);
        insertLayout.setWidthFull();
        insertLayout.getStyle().set("flex-wrap", "wrap");

        add(insertLayout);
    }

    private void addResultControlButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setSpacing(true);
        buttonLayout.setHeight("52px"); // Фиксированная высота
        buttonLayout.getStyle().set("flex-wrap", "wrap");

        saveToDatabaseButton = new Button("Сохранить в базу");
        saveToDatabaseButton.addClickListener(e -> {
            if (actionHandler != null) {
                actionHandler.accept(panelNumber, "saveToDatabase");
            }
        });

        saveToFileButton = new Button("Сохранить в файл");
        saveToFileButton.addClickListener(e -> {
            if (actionHandler != null) {
                actionHandler.accept(panelNumber, "saveToFile");
            }
        });

        buttonLayout.add(saveToDatabaseButton, saveToFileButton);
        add(buttonLayout);
    }

    // Методы для установки высоты компонентов
    public void setGridHeight(String height) {
        grid.setHeight(height);
    }

    public void setPointHandlers(BiConsumer<Integer, Point> addHandler, BiConsumer<Integer, Double> deleteHandler) {
        this.addPointHandler = addHandler;
        this.deletePointHandler = deleteHandler;
    }

    public void setButtonLayoutHeight(String height) {
        // Находим последний компонент (кнопки) и устанавливаем высоту
        if (getComponentCount() > 0) {
            Component lastComponent = getComponentAt(getComponentCount() - 1);
            if (lastComponent instanceof HorizontalLayout) {
                ((HorizontalLayout) lastComponent).setHeight(height);
            }
        }
    }

    private void addControlButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Button createFunctionButton = new Button("Создать функцию");
        createFunctionButton.addClickListener(e -> actionHandler.accept(panelNumber, "create"));

        Button loadExistingButton = new Button("Загрузить существующую");
        loadExistingButton.addClickListener(e -> actionHandler.accept(panelNumber, "load"));

        Button loadFromJsonButton = new Button("Загрузить из JSON");
        loadFromJsonButton.addClickListener(e -> actionHandler.accept(panelNumber, "loadJson"));

        Button loadFromXmlButton = new Button("Загрузить из XML");
        loadFromXmlButton.addClickListener(e -> actionHandler.accept(panelNumber, "loadXml"));

        buttonLayout.add(createFunctionButton, loadExistingButton, loadFromJsonButton, loadFromXmlButton);
        add(buttonLayout);
    }

    public void setFunction(TabulatedFunction function) {
        List<Point> points = new ArrayList<>();
        if (function != null) {
            for (int i = 0; i < function.getCount(); i++) {
                points.add(new Point(function.getX(i), function.getY(i)));
            }
        }

        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(points);
        dataProvider.refreshAll();
    }
}