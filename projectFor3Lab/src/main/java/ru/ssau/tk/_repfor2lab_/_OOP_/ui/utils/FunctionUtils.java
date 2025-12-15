package ru.ssau.tk._repfor2lab_._OOP_.ui.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.DTO.UserDTO;
import com.vaadin.flow.server.VaadinSession;
import java.io.IOException;
import java.net.URISyntaxException;

public class FunctionUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static TabulatedFunctionFactory getUserFunctionFactory(String login) {
        String factoryType = "array";

        try {
            var response = BasicAuthClient.sendGet("/users/get/" + login);
            if (response.statusCode() == 200) {
                UserDTO user = mapper.readValue(response.body(), UserDTO.class);
                factoryType = user.getFactoryType();
            }
        } catch (Exception e) {
            // Используем фабрику по умолчанию при ошибке
        }

        return "list".equals(factoryType) ?
                new LinkedListTabulatedFunctionFactory() :
                new ArrayTabulatedFunctionFactory();
    }

    public static int getUserIdByLogin(String login) throws IOException, InterruptedException, URISyntaxException {
        var response = BasicAuthClient.sendGet("/users/get-id-by-login/" + login);
        return mapper.readValue(response.body(), Integer.class);
    }
}