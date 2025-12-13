package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route(value = "my-functions", layout = MainLayout.class)
@PageTitle("Мои функции | MathFunction App")
public class MyFunctionView extends VerticalLayout {

    private final Grid<MathFunctionsDTO> grid = new Grid<>(MathFunctionsDTO.class);
    private final Button refreshBtn = new Button("Обновить", e -> loadFunctions());
    private final ObjectMapper mapper = new ObjectMapper();
    private static final int PAGE_SIZE = 100;

    public MyFunctionView() {
        addClassName("my-functions-view");
        setSizeFull();

        grid.removeAllColumns();
        grid.addColumn(MathFunctionsDTO::getFunctionName).setHeader("Имя");
        grid.addColumn(MathFunctionsDTO::getAmountOfDots).setHeader("Точек");
        grid.addColumn(MathFunctionsDTO::getLeftBorder).setHeader("Левая граница");
        grid.addColumn(MathFunctionsDTO::getRightBorder).setHeader("Правая граница");
        grid.addColumn(MathFunctionsDTO::getFunctionType).setHeader("Тип");
        grid.addComponentColumn(this::createActions).setHeader("Действия");

        add(new H2("Мои функции"), refreshBtn, grid);
        loadFunctions();
    }

    private HorizontalLayout createActions(MathFunctionsDTO func) {
        Button viewBtn = new Button("Просмотреть", e -> viewPoints(func));
        Button editBtn = new Button("Изменить", e -> editFunction(func));
        Button delBtn = new Button("Удалить", e -> deleteFunction(func));

        return new HorizontalLayout(viewBtn, editBtn, delBtn);
    }

    private void loadFunctions() {
        try {
            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            int userId = getUserIdByLogin(login);
            var response = BasicAuthClient.sendGet("/math-functions/get-by-user-id/" + userId);
            if (response.statusCode() == 200) {
                List<MathFunctionsDTO> funcs = mapper.readValue(response.body(), new TypeReference<List<MathFunctionsDTO>>() {});
                grid.setItems(funcs);
            } else {
                Notification.show("Ошибка загрузки: " + response.statusCode(), 4000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Ошибка: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void viewPoints(MathFunctionsDTO func) {
        openPointsDialog(func, false);
    }

    private void editFunction(MathFunctionsDTO func) {
        openPointsDialog(func, true);
    }

    private void deleteFunction(MathFunctionsDTO func) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Удалить функцию?");
        confirmDialog.add(new Span("Вы уверены, что хотите удалить \"" + func.getFunctionName() + "\"?"));

        Button confirmBtn = new Button("Да", e -> {
            confirmDialog.close();
            performDelete(func);
        });
        Button cancelBtn = new Button("Нет", e -> confirmDialog.close());
        confirmDialog.add(new HorizontalLayout(confirmBtn, cancelBtn));
        confirmDialog.open();
    }

    private void performDelete(MathFunctionsDTO func) {
        try {
            var response = BasicAuthClient.sendDelete("/math-functions/delete-by-function-id/" + func.getFunctionId());
            if (response.statusCode() == 200) {
                Notification.show("Функция удалена", 3000, Notification.Position.MIDDLE);
                loadFunctions();
            } else {
                String error = BasicAuthClient.extractErrorMessage(response.body());
                Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Ошибка удаления: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private int getUserIdByLogin(String login) throws IOException, InterruptedException, URISyntaxException {
        var response = BasicAuthClient.sendGet("/users/get-id-by-login/" + login);
        return mapper.readValue(response.body(), Integer.class);
    }

    private void openPointsDialog(MathFunctionsDTO func, boolean editable) {
        Optional<List<PointsDTO>> points = fetchPoints(func.getFunctionId());
        if (points.isEmpty()) {
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("700px");
        dialog.setCloseOnOutsideClick(true);
        dialog.setCloseOnEsc(true);

        List<PointsDTO> cachedPoints = new ArrayList<>(points.get());

        Span pageInfo = new Span();
        Grid<PointsDTO> pointsGrid = new Grid<>();
        pointsGrid.addColumn(PointsDTO::getXValue).setHeader("X");
        pointsGrid.addColumn(PointsDTO::getYValue).setHeader("Y");
        if (editable) {
            pointsGrid.addComponentColumn(point -> new Button("Удалить", e -> deletePoint(func, point, cachedPoints, pointsGrid, pageInfo)))
                    .setHeader("Действия");
        }

        Button prev = new Button("Предыдущая", e -> changePage(pointsGrid, cachedPoints, pageInfo, -1));
        Button next = new Button("Следующая", e -> changePage(pointsGrid, cachedPoints, pageInfo, 1));

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.add(new H2((editable ? "Редактирование функции: " : "Точки функции: ") + func.getFunctionName()));
        content.add(pointsGrid, new HorizontalLayout(prev, pageInfo, next));

        if (editable) {
            NumberField xField = new NumberField("X");
            NumberField yField = new NumberField("Y");
            Button addBtn = new Button("Добавить точку", e -> addPoint(func, xField.getValue(), yField.getValue(), cachedPoints, pointsGrid, pageInfo));
            content.add(new HorizontalLayout(xField, yField, addBtn));
        }

        Button closeButton = new Button("Закрыть", e -> dialog.close());
        content.add(new HorizontalLayout(closeButton));

        dialog.add(content);
        dialog.addDialogCloseActionListener(e -> loadFunctions());
        dialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                loadFunctions();
            }
        });
        dialog.open();

        changePage(pointsGrid, cachedPoints, pageInfo, 0);
    }

    private void changePage(Grid<PointsDTO> grid, List<PointsDTO> points, Span info, int delta) {
        int currentPage = info.getText().isEmpty() ? 0 : Integer.parseInt(info.getText().split("/")[0]) - 1;
        int totalPages = Math.max(1, (int) Math.ceil(points.size() / (double) PAGE_SIZE));
        int newPage = Math.min(Math.max(0, currentPage + delta), totalPages - 1);

        int fromIndex = newPage * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, points.size());
        grid.setItems(points.subList(fromIndex, toIndex));
        info.setText((newPage + 1) + "/" + totalPages);
    }

    private Optional<List<PointsDTO>> fetchPoints(Integer functionId) {
        try {
            HttpResponse<String> response = BasicAuthClient.sendGet("/points/get-points-by-function-id-sorted/" + functionId);
            if (response.statusCode() == 200) {
                List<PointsDTO> points = mapper.readValue(response.body(), new TypeReference<List<PointsDTO>>() {});
                return Optional.of(points);
            }
            Notification.show("Ошибка загрузки точек: " + response.statusCode(), 4000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show("Ошибка загрузки точек: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
        return Optional.empty();
    }

    private void addPoint(MathFunctionsDTO func, Double xValue, Double yValue, List<PointsDTO> points,
                          Grid<PointsDTO> grid, Span info) {
        if (xValue == null || yValue == null) {
            Notification.show("Введите X и Y", 3000, Notification.Position.MIDDLE);
            return;
        }
        try {
            String body = mapper.writeValueAsString(Map.of("xvalue", xValue, "yvalue", yValue));
            var response = BasicAuthClient.sendPost("/points/create-point/" + func.getFunctionId(), body);
            if (response.statusCode() == 201) {
                Notification.show("Точка добавлена", 2500, Notification.Position.MIDDLE);
                fetchPoints(func.getFunctionId()).ifPresent(newPoints -> {
                    points.clear();
                    points.addAll(newPoints);
                    changePage(grid, points, info, 0);
                });
                loadFunctions();
            } else {
                String error = BasicAuthClient.extractErrorMessage(response.body());
                Notification.show("Ошибка добавления: " + error, 4000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Ошибка добавления: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void deletePoint(MathFunctionsDTO func, PointsDTO point, List<PointsDTO> points,
                             Grid<PointsDTO> grid, Span info) {
        try {
            List<PointsDTO> updated = new ArrayList<>(points);
            updated.removeIf(p -> p.getXValue().equals(point.getXValue()) && p.getYValue().equals(point.getYValue()));

            var deleteResp = BasicAuthClient.sendDelete("/points/delete-points-for-function/" + func.getFunctionId());
            if (deleteResp.statusCode() != 200) {
                String error = BasicAuthClient.extractErrorMessage(deleteResp.body());
                Notification.show("Ошибка удаления: " + error, 4000, Notification.Position.MIDDLE);
                return;
            }

            if (!updated.isEmpty()) {
                String payload = mapper.writeValueAsString(Map.of("points", updated));
                var createResp = BasicAuthClient.sendPost("/points/create-points/" + func.getFunctionId(), payload);
                if (createResp.statusCode() != 201) {
                    String error = BasicAuthClient.extractErrorMessage(createResp.body());
                    Notification.show("Ошибка сохранения: " + error, 4000, Notification.Position.MIDDLE);
                    return;
                }
            }

            Notification.show("Точка удалена", 2500, Notification.Position.MIDDLE);
            points.clear();
            points.addAll(updated);
            changePage(grid, points, info, 0);
            loadFunctions();
        } catch (Exception e) {
            Notification.show("Ошибка удаления: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}