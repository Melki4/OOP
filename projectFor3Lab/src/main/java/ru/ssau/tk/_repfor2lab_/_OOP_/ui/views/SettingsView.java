package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import ru.ssau.tk._repfor2lab_._OOP_.controller.databaseDTO.UserDTO;

import java.io.IOException;
import java.net.URISyntaxException;

@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Настройки | MathFunction App")
public class SettingsView extends VerticalLayout {

    private final ComboBox<String> factorySelect = new ComboBox<>("Тип фабрики");
    private final Button saveBtn = new Button("Сохранить", this::saveSettings);
    private final ObjectMapper mapper = new ObjectMapper();

    public SettingsView() {
        addClassName("settings-view");
        setPadding(true);

        factorySelect.setItems("array", "list");
        factorySelect.setPlaceholder("Выберите");

        add(new H2("Настройки"), factorySelect, saveBtn);
        loadSettings();
    }

    private void loadSettings() {
        try {
            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            var response = BasicAuthClient.sendGet("/users/get/" + login);
            if (response.statusCode() == 200) {
                UserDTO user = mapper.readValue(response.body(), UserDTO.class);
                factorySelect.setValue(user.getFactoryType());
            }
        } catch (Exception e) {
            Notification.show("Ошибка загрузки настроек", 4000, Notification.Position.MIDDLE);
        }
    }

    private void saveSettings(com.vaadin.flow.component.ClickEvent<Button> event) {
        String type = factorySelect.getValue();
        if (type == null || type.isEmpty()) {
            Notification.show("Выберите тип фабрики", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            String login = (String) VaadinSession.getCurrent().getAttribute("login");
            int userId = getUserIdByLogin(login);

            String json = "{\"factory-type\": \"" + type + "\"}";
            var response = BasicAuthClient.sendPut("/users/update/factory-type/" + userId, json);

            if (response.statusCode() == 200) {
                Notification.show("Настройки сохранены", 3000, Notification.Position.MIDDLE);
            } else {
                String error = BasicAuthClient.extractErrorMessage(response.body());
                Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Ошибка: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private int getUserIdByLogin(String login) throws IOException, InterruptedException, URISyntaxException {
        var response = BasicAuthClient.sendGet("/users/get-id-by-login/" + login);
        return mapper.readValue(response.body(), Integer.class);
    }
}