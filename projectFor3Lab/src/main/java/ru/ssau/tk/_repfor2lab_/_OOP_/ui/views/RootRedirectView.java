package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")
@PageTitle("Перенаправление")
@AnonymousAllowed
public class RootRedirectView extends com.vaadin.flow.component.html.Div {

    public RootRedirectView() {
        // Автоматически перенаправляем на страницу входа
        UI.getCurrent().getPage().executeJs("window.location.href = 'login'");
    }
}