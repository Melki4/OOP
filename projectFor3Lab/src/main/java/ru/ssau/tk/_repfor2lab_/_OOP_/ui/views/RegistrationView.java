package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Route("register")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    private final TextField loginField = new TextField("Логин");
    private final PasswordField passwordField = new PasswordField("Пароль");
    private final PasswordField confirmPasswordField = new PasswordField("Подтвердите пароль");
    private final Button registerButton = new Button("Зарегистрироваться");
    private final Button backButton = new Button("Вернуться ко входу");

    private final ObjectMapper mapper = new ObjectMapper();

    public RegistrationView() {
        addClassName("registration-view");
        setWidth("100%");
        setHeight("100vh");
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        H2 title = new H2("Регистрация");
        title.getStyle()
                .set("margin", "0 0 var(--lumo-space-m) 0")
                .set("text-align", "center")
                .set("color", "var(--lumo-header-text-color)");

        loginField.setRequiredIndicatorVisible(true);
        passwordField.setRequiredIndicatorVisible(true);
        confirmPasswordField.setRequiredIndicatorVisible(true);

        loginField.setWidth("100%");
        passwordField.setWidth("100%");
        confirmPasswordField.setWidth("100%");

        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidth("100%");

        registerButton.addClickListener(e -> registerUser());

        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.setWidth("100%");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("login")));

        FormLayout form = new FormLayout();
        form.add(loginField, passwordField, confirmPasswordField);
        form.setWidth("100%");
        form.setMaxWidth("400px");

        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        Div formContainer = new Div();
        formContainer.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("padding", "var(--lumo-space-l)")
                .set("width", "100%")
                .set("max-width", "420px");

        VerticalLayout formContent = new VerticalLayout(title, form, registerButton, backButton);
        formContent.setSpacing(true);
        formContent.setPadding(false);
        formContent.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        formContent.getStyle().set("width", "100%");

        formContainer.add(formContent);

        VerticalLayout centerWrapper = new VerticalLayout(formContainer);
        centerWrapper.setJustifyContentMode(JustifyContentMode.CENTER);
        centerWrapper.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        centerWrapper.setWidth("100%");
        centerWrapper.setPadding(true);

        add(centerWrapper);
    }

    private void registerUser() {
        String login = loginField.getValue().trim();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        // Валидация на фронтенде
        if (login.isEmpty() || password.isEmpty()) {
            Notification.show("Логин и пароль обязательны", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            Notification.show("Пароли не совпадают", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Подготовка JSON
        String json = """
            {
              "login": "%s",
              "password": "%s"
            }
            """.formatted(login, password);

        try {
            // ⚠️ Замените URL на ваш сервер (если фронт и бэк на одном порту — оставьте как есть)
            URI uri = new URI("http://localhost:8080/projectFor3Lab/users/auth/register");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                Notification.show("Регистрация успешна! Теперь войдите в систему.", 4000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("login"));
            } else {
                // Обработка ошибки
                String errorMsg = extractErrorMessage(response.body());
                Notification.show("Ошибка: " + errorMsg, 5000, Notification.Position.MIDDLE);
            }

        } catch (URISyntaxException e) {
            Notification.show("Ошибка URL: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        } catch (IOException | InterruptedException e) {
            Notification.show("Ошибка подключения к серверу", 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    private String extractErrorMessage(String responseBody) {
        try {
            JsonNode node = mapper.readTree(responseBody);
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (Exception ignored) {
            // Если не JSON — просто вернём тело
        }
        return "Неизвестная ошибка сервера";
    }
}