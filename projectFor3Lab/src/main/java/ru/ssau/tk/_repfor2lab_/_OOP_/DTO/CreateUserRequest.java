package ru.ssau.tk._repfor2lab_._OOP_.DTO;

public class CreateUserRequest {
    private String login;
    private String password; // ← только при создании
    private String role;
    private String factoryType;

    public CreateUserRequest(){}

    public CreateUserRequest(String login, String password, String role, String factoryType){
        this.login = login;
        this.password = password;
        this.role = role;
        this.factoryType = factoryType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }
}
