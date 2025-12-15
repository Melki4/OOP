package ru.ssau.tk._repfor2lab_._OOP_.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Insertable;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Removable;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.integration.Integrate;
import ru.ssau.tk._repfor2lab_._OOP_.integration.Interval;
import ru.ssau.tk._repfor2lab_._OOP_.integration.Params;
import ru.ssau.tk._repfor2lab_._OOP_.ui.MainLayout;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionCreationDialog;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionFileUploadDialog;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionGridComponent;
import ru.ssau.tk._repfor2lab_._OOP_.ui.views.functionoperation.FunctionSelectionDialog;

import java.text.DecimalFormat;
import java.util.concurrent.ForkJoinPool;

@Route(value = "function-integration", layout = MainLayout.class)
@PageTitle("Интегрирование | MathFunction App")
public class FunctionIntegrationView extends VerticalLayout {

    private final FunctionSelectionDialog functionSelectionDialog;
    private final FunctionCreationDialog functionCreationDialog;
    private final FunctionFileUploadDialog fileUploadDialog;

    private TabulatedFunction sourceFunction;

    private FunctionGridComponent sourceGrid;
    private final NumberField threadField;
    private final TextField resultField;

    public FunctionIntegrationView() {
        String login = (String) VaadinSession.getCurrent().getAttribute("login");

        this.functionSelectionDialog = new FunctionSelectionDialog();
        this.functionCreationDialog = new FunctionCreationDialog();
        this.fileUploadDialog = new FunctionFileUploadDialog();

        functionSelectionDialog.loadAvailableFunctions(login);

        addClassName("function-integration-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        threadField = new NumberField("Количество потоков");
        threadField.setMin(1);
        threadField.setStep(1);
        threadField.setStepButtonsVisible(true);
        threadField.setWidth("180px");
        threadField.setValue(4d);

        resultField = new TextField("Результат интегрирования");
        resultField.setReadOnly(true);
        resultField.setWidth("280px");

        createUI();
    }

    private void createUI() {
        add(new H3("Вычисление определённого интеграла табулированной функции"));

        Button integrateButton = new Button("Вычислить интеграл", e -> calculateIntegral());
        integrateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout controls = new HorizontalLayout(threadField, integrateButton);
        controls.setWidthFull();
        controls.setJustifyContentMode(JustifyContentMode.CENTER);
        controls.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        controls.setSpacing(true);
        add(controls, resultField);

        sourceGrid = new FunctionGridComponent("Исходная функция", true, 1, this::handlePanelAction);
        sourceGrid.setPointHandlers(this::handlePointInsert, this::handlePointDelete);
        sourceGrid.setGridHeight("420px");

        HorizontalLayout functionsLayout = new HorizontalLayout(sourceGrid);
        functionsLayout.setWidth("100%");
        functionsLayout.setHeight("720px");
        functionsLayout.setSpacing(true);
        functionsLayout.setPadding(true);
        functionsLayout.setDefaultVerticalComponentAlignment(Alignment.START);
        functionsLayout.setJustifyContentMode(JustifyContentMode.AROUND);

        add(functionsLayout);
    }

    private void handlePanelAction(int panelNumber, String action) {
        if (panelNumber != 1) {
            return;
        }

        switch (action) {
            case "create":
                functionCreationDialog.open(this::handleFunctionLoaded);
                break;
            case "load":
                functionSelectionDialog.open(this::handleFunctionLoaded);
                break;
            case "loadJson":
                fileUploadDialog.open("json", this::handleFunctionLoaded);
                break;
            case "loadXml":
                fileUploadDialog.open("xml", this::handleFunctionLoaded);
                break;
            default:
                break;
        }
    }

    private void handleFunctionLoaded(TabulatedFunction function) {
        sourceFunction = function;
        sourceGrid.setFunction(function);
        resultField.clear();
    }

    private void calculateIntegral() {
        if (sourceFunction == null) {
            Notification.show("Сначала выберите или создайте функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        int threads = threadField.getValue() != null ? threadField.getValue().intValue() : 1;
        if (threads < 1) {
            threads = 1;
        }
        if (threads > 32) {
            threads = 32;
            threadField.setValue((double) threads);
        }

        int intervalLength = sourceFunction.getCount();
        int maxInterval = Math.max(2000, (int) Math.ceil(intervalLength / 100.0));

        Params params = new Params(threads, maxInterval);
        Interval interval = new Interval(0, sourceFunction.getCount() - 1);

        ForkJoinPool forkJoinPool = new ForkJoinPool(threads);
        try {
            double result = forkJoinPool.invoke(new Integrate(sourceFunction, params, interval));
            resultField.setValue(new DecimalFormat("0.################").format(result));
        } catch (Exception e) {
            Notification.show("Ошибка при вычислении интеграла: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        } finally {
            forkJoinPool.shutdown();
        }
    }

    private void handlePointInsert(int panelNumber, Point newPoint) {
        TabulatedFunction target = ensureEditableFunction(sourceFunction);
        if (target == null) {
            Notification.show("Сначала выберите или создайте функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            if (target.indexOfX(newPoint.getX()) != -1) {
                throw new IllegalArgumentException("Точка с таким x уже существует");
            }
            if (target instanceof Insertable) {
                ((Insertable) target).insert(newPoint.getX(), newPoint.getY());
                sourceFunction = target;
                sourceGrid.setFunction(sourceFunction);
            } else {
                Notification.show("Эта функция не поддерживает добавление точек", 4000, Notification.Position.MIDDLE);
            }
        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage(), 4000, Notification.Position.MIDDLE);
        } catch (Exception ex) {
            Notification.show("Ошибка при добавлении точки: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private void handlePointDelete(int panelNumber, Double xValue) {
        TabulatedFunction target = ensureEditableFunction(sourceFunction);
        if (target == null) {
            Notification.show("Сначала выберите или создайте функцию", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            int index = target.indexOfX(xValue);
            if (index == -1) {
                Notification.show("Точка не найдена", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (target instanceof Removable) {
                ((Removable) target).remove(index);
                sourceFunction = target;
                sourceGrid.setFunction(sourceFunction);
            } else {
                Notification.show("Эта функция не поддерживает удаление точек", 4000, Notification.Position.MIDDLE);
            }
        } catch (Exception ex) {
            Notification.show("Ошибка при удалении точки: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private TabulatedFunction ensureEditableFunction(TabulatedFunction function) {
        if (function == null) {
            return null;
        }
        if (function instanceof Insertable && function instanceof Removable) {
            return function;
        }

        double[] xValues = new double[function.getCount()];
        double[] yValues = new double[function.getCount()];
        for (int i = 0; i < function.getCount(); i++) {
            xValues[i] = function.getX(i);
            yValues[i] = function.getY(i);
        }

        TabulatedFunction editableCopy = new ArrayTabulatedFunction(xValues, yValues);
        sourceFunction = editableCopy;
        sourceGrid.setFunction(sourceFunction);
        return editableCopy;
    }
}