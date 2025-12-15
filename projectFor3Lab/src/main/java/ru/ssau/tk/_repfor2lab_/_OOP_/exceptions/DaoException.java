package ru.ssau.tk._repfor2lab_._OOP_.exceptions;

import java.sql.SQLException;

public class DaoException extends RuntimeException{

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable e) {
        super(e);
    }

    public DaoException(String message, Throwable e) {
        super(message, e);
    }

    public DaoException() {
        super();
    }


}
