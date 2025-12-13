package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final TextField loginField = new TextField("Логин");
    private final PasswordField passwordField = new PasswordField("Пароль");
    private final Button loginButton = new Button("Войти");

    public LoginView() {
        // Настройки основного контейнера
        setWidth("100%");
        setHeight("100vh"); // Занимаем всю высоту viewport
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        // Контейнер для формы с отступами
        Div formContainer = new Div();
        formContainer.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("padding", "var(--lumo-space-l)")
                .set("width", "100%")
                .set("max-width", "400px");

        // Заголовок
        H2 title = new H2("Вход в систему");
        title.getStyle()
                .set("margin", "0 0 var(--lumo-space-m) 0")
                .set("text-align", "center")
                .set("color", "var(--lumo-header-text-color)");

        loginField.setRequiredIndicatorVisible(true);
        passwordField.setRequiredIndicatorVisible(true);
        loginField.setWidth("100%");
        passwordField.setWidth("100%");

        loginButton.addClickListener(e -> handleLogin());
        loginButton.setWidth("100%");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout form = new FormLayout();
        form.add(loginField, passwordField);
        form.setWidth("100%");
        form.setMaxWidth("400px");
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        // Вертикальный контейнер для всех элементов формы
        VerticalLayout formContent = new VerticalLayout(title, form, loginButton);
        formContent.setSpacing(true);
        formContent.setPadding(false);
        formContent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        formContent.getStyle().set("width", "100%");

        // Добавляем контент в контейнер формы
        formContainer.add(formContent);

        // Центрируем контейнер с формой по вертикали и горизонтали
        VerticalLayout centerWrapper = new VerticalLayout(formContainer);
        centerWrapper.setJustifyContentMode(JustifyContentMode.CENTER);
        centerWrapper.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        centerWrapper.setWidth("100%");
        centerWrapper.setPadding(true);
        centerWrapper.getStyle().set("flex-grow", "1");

        add(centerWrapper);
    }

    private void handleLogin() {
        String login = loginField.getValue().trim();
        String password = passwordField.getValue();

        if (login.isEmpty() || password.isEmpty()) {
            Notification.show("Логин и пароль обязательны", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Проверка через запрос к /users (требует аутентификации)
        try {
            String credentials = "Basic " + Base64.getEncoder().encodeToString((login + ":" + password).getBytes());
            URI uri = new URI("http://localhost:8080/projectFor3Lab/users");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", credentials)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 403) {
                // 200 — админ, 403 — обычный пользователь (но аутентификация прошла)
                VaadinSession.getCurrent().setAttribute("login", login);
                VaadinSession.getCurrent().setAttribute("password", password);

                Notification.show("Добро пожаловать, " + login + "!", 3000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("main"));
            } else if (response.statusCode() == 401) {
                Notification.show("Неверный логин или пароль", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("Ошибка сервера: " + response.statusCode(), 5000, Notification.Position.MIDDLE);
            }

        } catch (URISyntaxException e) {
            Notification.show("Ошибка URL", 3000, Notification.Position.MIDDLE);
        } catch (IOException | InterruptedException e) {
            Notification.show("Ошибка подключения", 5000, Notification.Position.MIDDLE);
        }
    }
}