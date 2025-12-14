package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

@Route(value = "main", layout = ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout.class)
@PageTitle("Главная | MathFunction App")
public class MainView extends VerticalLayout {

    public MainView() {
        addClassName("main-view");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        H2 title = new H2("Добро пожаловать!");
        Button createBtn = new Button("Создать функцию");
        Button myFuncBtn = new Button("Мои функции");
        Button diffBtn = new Button("Дифференцирование");
        Button settingsBtn = new Button("Настройки");

        createBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("create-function")));
        myFuncBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("my-functions")));
        diffBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("function-differentiation")));
        settingsBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("settings")));

        add(title, createBtn, myFuncBtn, diffBtn, settingsBtn);
    }
}