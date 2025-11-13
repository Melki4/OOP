package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectionManager {

    private final static String URL_KEY = "db.url";
    private final static String LOGIN_KEY = "db.login";
    private final static String PASSWORD_KEY = "db.password";

    public static Connection open(){
        try {
            return DriverManager.getConnection(propertiesUtil.get(URL_KEY),
                    propertiesUtil.get(LOGIN_KEY), propertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
