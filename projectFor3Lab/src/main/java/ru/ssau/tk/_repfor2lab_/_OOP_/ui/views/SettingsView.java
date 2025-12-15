package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.utils.BasicAuthClient;
import ru.ssau.tk._repfor2lab_._OOP_.DTO.UserDTO;

import java.io.IOException;
import java.net.URISyntaxException;

@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Настройки | MathFunction App")
public class SettingsView extends VerticalLayout {

    private final ComboBox<String> factorySelect = new ComboBox<>("Тип фабрики");
    private final Button saveBtn = new Button("Сохранить", this::saveSettings);
    private final Button changeLoginBtn = new Button("Сменить логин", e -> openChangeLoginDialog());
    private final Button changePasswordBtn = new Button("Сменить пароль", e -> openChangePasswordDialog());
    private final ObjectMapper mapper = new ObjectMapper();

    public SettingsView() {
        addClassName("settings-view");
        setPadding(true);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        factorySelect.setItems("array", "list");
        factorySelect.setPlaceholder("Выберите");
        factorySelect.setWidth("320px");
        factorySelect.getStyle().set("text-align", "center");

        saveBtn.getStyle().set("margin-top", "8px");

        changeLoginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changeLoginBtn.setWidth("320px");
        changePasswordBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changePasswordBtn.setWidth("320px");

        VerticalLayout accountLayout = new VerticalLayout(changeLoginBtn, changePasswordBtn);
        accountLayout.setPadding(false);
        accountLayout.setSpacing(true);
        accountLayout.setWidthFull();
        accountLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        Span accountTitle = new Span("Управление аккаунтом");
        accountTitle.getStyle()
                .set("font-weight", "600")
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin-top", "var(--lumo-space-m)");

        add(new H2("Настройки"), factorySelect, saveBtn, accountTitle, accountLayout);
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

    private void openChangeLoginDialog() {
        Dialog dialog = createStyledDialog("Смена логина");

        TextField newLoginField = new TextField("Новый логин");
        newLoginField.setWidthFull();
        PasswordField oldPasswordField = new PasswordField("Старый пароль");
        oldPasswordField.setWidthFull();
        oldPasswordField.setRequiredIndicatorVisible(true);

        FormLayout form = new FormLayout(newLoginField, oldPasswordField);
        form.setWidth("100%");
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        Button backBtn = new Button("Назад", e -> dialog.close());
        Button submitBtn = new Button("Сохранить", e -> {
            if (!validateOldPassword(oldPasswordField.getValue())) {
                return;
            }

            String newLogin = newLoginField.getValue().trim();
            if (newLogin.isEmpty()) {
                Notification.show("Введите новый логин", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                String currentLogin = (String) VaadinSession.getCurrent().getAttribute("login");
                int userId = getUserIdByLogin(currentLogin);
                String json = "{\"login\": \"" + newLogin + "\"}";
                var response = BasicAuthClient.sendPut("/users/update/login/" + userId, json);

                if (response.statusCode() == 200) {
                    VaadinSession.getCurrent().setAttribute("login", newLogin);
                    Notification.show("Логин обновлен", 3000, Notification.Position.MIDDLE);
                    dialog.close();
                } else {
                    String error = BasicAuthClient.extractErrorMessage(response.body());
                    Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
                }
            } catch (Exception ex) {
                Notification.show("Ошибка: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(form);
        dialog.getFooter().add(createDialogFooter(backBtn, submitBtn));
        dialog.open();
    }

    private void openChangePasswordDialog() {
        Dialog dialog = createStyledDialog("Смена пароля");

        PasswordField newPasswordField = new PasswordField("Новый пароль");
        newPasswordField.setWidthFull();
        PasswordField confirmPasswordField = new PasswordField("Подтвердите пароль");
        confirmPasswordField.setWidthFull();
        PasswordField oldPasswordField = new PasswordField("Старый пароль");
        oldPasswordField.setWidthFull();
        oldPasswordField.setRequiredIndicatorVisible(true);

        FormLayout form = new FormLayout(newPasswordField, confirmPasswordField, oldPasswordField);
        form.setWidth("100%");
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        Button backBtn = new Button("Назад", e -> dialog.close());
        Button submitBtn = new Button("Сохранить", e -> {
            if (!validateOldPassword(oldPasswordField.getValue())) {
                return;
            }

            String newPassword = newPasswordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (newPassword.isEmpty()) {
                Notification.show("Введите новый пароль", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                Notification.show("Пароли не совпадают", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                String login = (String) VaadinSession.getCurrent().getAttribute("login");
                int userId = getUserIdByLogin(login);
                String json = "{\"password\": \"" + newPassword + "\"}";
                var response = BasicAuthClient.sendPut("/users/update/password/" + userId, json);

                if (response.statusCode() == 200) {
                    VaadinSession.getCurrent().setAttribute("password", newPassword);
                    Notification.show("Пароль обновлен", 3000, Notification.Position.MIDDLE);
                    dialog.close();
                } else {
                    String error = BasicAuthClient.extractErrorMessage(response.body());
                    Notification.show("Ошибка: " + error, 5000, Notification.Position.MIDDLE);
                }
            } catch (Exception ex) {
                Notification.show("Ошибка: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(form);
        dialog.getFooter().add(createDialogFooter(backBtn, submitBtn));
        dialog.open();
    }

    private Dialog createStyledDialog(String title) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(title);
        dialog.setWidth("480px");
        dialog.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)");
        return dialog;
    }

    private HorizontalLayout createDialogFooter(Button backBtn, Button submitBtn) {
        HorizontalLayout footer = new HorizontalLayout(backBtn, submitBtn);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);
        footer.setSpacing(true);
        return footer;
    }

    private boolean validateOldPassword(String oldPassword) {
        if (oldPassword == null || oldPassword.isEmpty()) {
            Notification.show("Введите старый пароль", 3000, Notification.Position.MIDDLE);
            return false;
        }

        String currentPassword = (String) VaadinSession.getCurrent().getAttribute("password");
        if (currentPassword == null) {
            Notification.show("Сессия истекла. Войдите заново.", 3000, Notification.Position.MIDDLE);
            return false;
        }

        if (!oldPassword.equals(currentPassword)) {
            Notification.show("Неверный старый пароль", 3000, Notification.Position.MIDDLE);
            return false;
        }

        return true;
    }
}