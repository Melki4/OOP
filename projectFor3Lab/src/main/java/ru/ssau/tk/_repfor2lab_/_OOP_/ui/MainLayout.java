package ru.ssau.tk._repfor2lab_._OOP_.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.HashMap;
import java.util.Map;

@AnonymousAllowed
public class MainLayout extends AppLayout {

    private final Tabs menu;
    private final Map<Tab, String> tabToRoute = new HashMap<>();

    public MainLayout() {
        H1 title = new H1("MathFunction App");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        menu = createMenuTabs();
        menu.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab != null && tabToRoute.containsKey(selectedTab)) {
                String route = tabToRoute.get(selectedTab);
                if ("logout".equals(route)) {
                    VaadinSession.getCurrent().setAttribute("login", null);
                    VaadinSession.getCurrent().setAttribute("password", null);
                    getUI().ifPresent(ui -> ui.navigate("login"));
                    menu.setSelectedTab(null); // сброс выделения
                } else {
                    getUI().ifPresent(ui -> ui.navigate(route));
                }
            }
        });

        addToNavbar(createTopBar(title, menu));
    }

    private Tabs createMenuTabs() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);

        addTab(tabs, "Главная", "main");
        addTab(tabs, "Создать функцию", "create-function");
        addTab(tabs, "Мои функции", "my-functions");
        addTab(tabs, "Настройки", "settings");
        addTab(tabs, "Выйти", "logout");

        return tabs;
    }

    private void addTab(Tabs tabs, String title, String route) {
        Tab tab = new Tab(title);
        tabToRoute.put(tab, route);
        tabs.add(tab);
    }

    private HorizontalLayout createTopBar(H1 title, Tabs menu) {
        HorizontalLayout topBar = new HorizontalLayout(title, menu);
        topBar.setWidth("100%");
        topBar.expand(menu);
        topBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        return topBar;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        // Защита маршрутов
        if (VaadinSession.getCurrent().getAttribute("login") == null) {
            String currentRoute = getCurrentViewRoute();
            if (!"login".equals(currentRoute) && !"register".equals(currentRoute)) {
                getUI().ifPresent(ui -> ui.navigate("login"));
            }
            return;
        }

        // Опционально: автоматически выделять активную вкладку
        String currentRoute = getCurrentViewRoute();
        for (Map.Entry<Tab, String> entry : tabToRoute.entrySet()) {
            if (entry.getValue().equals(currentRoute)) {
                menu.setSelectedTab(entry.getKey());
                break;
            }
        }
    }

    private String getCurrentViewRoute() {
        return RouteConfiguration.forSessionScope()
                .getUrl(getContent().getClass());
    }
}