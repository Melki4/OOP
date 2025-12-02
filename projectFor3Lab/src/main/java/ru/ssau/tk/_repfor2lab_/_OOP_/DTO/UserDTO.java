package ru.ssau.tk._repfor2lab_._OOP_.DTO;

import org.springframework.expression.spel.ast.StringLiteral;

public class UserDTO {
    private Long userID;
    private String login;
    private String factoryType;
    private String role;

    public UserDTO(){}

    public UserDTO(Long userID, String login, String factoryType, String role) {
        this.userID = userID;
        this.login = login;
        this.factoryType = factoryType;
        this.role = role;
    }

    public Long getUserID() {
        return userID;
    }
    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getFactoryType() {
        return factoryType;
    }
    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
