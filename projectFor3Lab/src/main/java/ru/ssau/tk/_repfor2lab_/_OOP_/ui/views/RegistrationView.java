package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.PasswordField;
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

    private final ObjectMapper mapper = new ObjectMapper();

    public RegistrationView() {
        addClassName("registration-view");
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();

        H2 title = new H2("Регистрация");
        title.getStyle().set("margin-bottom", "1.5rem");

        loginField.setRequiredIndicatorVisible(true);
        passwordField.setRequiredIndicatorVisible(true);
        confirmPasswordField.setRequiredIndicatorVisible(true);

        registerButton.addClickListener(e -> registerUser());

        FormLayout form = new FormLayout();
        form.add(loginField, passwordField, confirmPasswordField);
        form.setMaxWidth("400px");

        add(title, form, registerButton);
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
            URI uri = new URI("http://localhost:8080/projectFor3Lab/user/auth/register");

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