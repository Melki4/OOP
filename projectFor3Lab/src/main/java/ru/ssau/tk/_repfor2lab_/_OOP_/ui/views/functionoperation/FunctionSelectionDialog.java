package ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.notification.Notification;
import ru.ssau.tk._repfor2lab_._OOP_.DTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.DTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import java.util.List;
import java.util.function.Consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.FunctionUtils;

public class FunctionSelectionDialog {
    private final Dialog dialog;
    private final Grid<MathFunctionsDTO> grid;
    private final ObjectMapper mapper = new ObjectMapper();
    private Consumer<TabulatedFunction> selectionHandler;

    public FunctionSelectionDialog() {
        dialog = new Dialog();
        dialog.setHeaderTitle("Выберите функцию");

        grid = new Grid<>(MathFunctionsDTO.class);
        grid.setColumns("functionName", "leftBoarder", "rightBoarder", "amountOfDots");
        grid.getColumnByKey("functionName").setHeader("Имя функции");
        grid.getColumnByKey("leftBoarder").setHeader("Левая граница");
        grid.getColumnByKey("rightBoarder").setHeader("Правая граница");
        grid.getColumnByKey("amountOfDots").setHeader("Точек");
        grid.setHeight("400px");
        grid.setWidth("600px");

        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout footerLayout = new HorizontalLayout(cancelButton);
        footerLayout.setWidth("100%");
        footerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        dialog.add(grid);
        dialog.getFooter().add(footerLayout);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null && selectionHandler != null) {
                loadSelectedFunction(event.getValue());
                dialog.close();
            }
        });
    }

    public void loadAvailableFunctions(String login) {
        try {
            int userId = FunctionUtils.getUserIdByLogin(login);
            var response = BasicAuthClient.sendGet("/math-functions/get-by-user-id/" + userId);

            if (response.statusCode() == 200) {
                List<MathFunctionsDTO> functions = mapper.readValue(
                        response.body(),
                        new TypeReference<List<MathFunctionsDTO>>() {}
                );
                grid.setItems(functions);
            }
        } catch (Exception e) {
            Notification.show("Ошибка загрузки списка функций: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    public void open(Consumer<TabulatedFunction> handler) {
        this.selectionHandler = handler;
        grid.deselectAll();
        dialog.open();
    }

    private void loadSelectedFunction(MathFunctionsDTO selectedFunction) {
        try {
            var response = BasicAuthClient.sendGet("/points/get-points-by-function-id-sorted/" + selectedFunction.getFunctionID());
            if (response.statusCode() == 200) {
                List<PointsDTO> pointsDTOs = mapper.readValue(
                        response.body(),
                        new TypeReference<List<PointsDTO>>() {}
                );

                double[] xValues = pointsDTOs.stream().mapToDouble(PointsDTO::getxValue).toArray();
                double[] yValues = pointsDTOs.stream().mapToDouble(PointsDTO::getyValue).toArray();

                TabulatedFunctionFactory factory = FunctionUtils.getUserFunctionFactory(
                        (String) VaadinSession.getCurrent().getAttribute("login"));
                TabulatedFunction function = factory.create(xValues, yValues);

                if (selectionHandler != null) {
                    selectionHandler.accept(function);
                }
            }
        } catch (Exception e) {
            Notification.show("Ошибка загрузки функции: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}