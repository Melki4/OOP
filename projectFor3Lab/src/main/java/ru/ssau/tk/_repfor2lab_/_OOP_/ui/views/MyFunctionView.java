package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.MathFunctionsDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Route(value = "my-functions", layout = MainLayout.class)
@PageTitle("Мои функции | MathFunction App")
public class MyFunctionView extends VerticalLayout {

    private final Grid<MathFunctionsDTO> grid = new Grid<>(MathFunctionsDTO.class);
    private final Button refreshBtn = new Button("Обновить", e -> loadFunctions());
    private final ObjectMapper mapper = new ObjectMapper();

    public MyFunctionView() {
        addClassName("my-functions-view");
        setSizeFull();

        grid.addColumn(MathFunctionsDTO::getFunctionName).setHeader("Имя");
        grid.addColumn(MathFunctionsDTO::getAmountOfDots).setHeader("Точек");
        grid.addColumn(MathFunctionsDTO::getLeftBorder).setHeader("Левая");
        grid.addColumn(MathFunctionsDTO::getRightBorder).setHeader("Правая");
        grid.addColumn(MathFunctionsDTO::getFunctionType).setHeader("Тип");
        grid.addComponentColumn(this::createActions).setHeader("Действия");

        add(new H2("Мои функции"), refreshBtn, grid);
        loadFunctions();
    }

    private HorizontalLayout createActions(MathFunctionsDTO func) {
        Button viewBtn = new Button("Точки", e -> viewPoints(func));
        Button editBtn = new Button("Имя", e -> editName(func));
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
        Notification.show("Просмотр точек (реализуйте отдельно)", 3000, Notification.Position.MIDDLE);
        // TODO: открыть модальное окно с точками
    }

    private void editName(MathFunctionsDTO func) {
        Notification.show("Редактирование имени (реализуйте)", 3000, Notification.Position.MIDDLE);
        // TODO: диалоговое окно для изменения имени
    }

    private void deleteFunction(MathFunctionsDTO func) {
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
}