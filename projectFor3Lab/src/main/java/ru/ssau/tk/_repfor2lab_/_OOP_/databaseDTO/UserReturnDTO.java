package ru.ssau.tk._repfor2lab_._OOP_.databaseDTO;

public class UserReturnDTO {
    private Integer userId;
    private String factoryType;
    private String login;
    private String role;

    // Конструкторы
    public UserReturnDTO() {}

    public UserReturnDTO(Integer userId, String factoryType, String login, String role) {
        this.userId = userId;
        this.factoryType = factoryType;
        this.login = login;
        this.role = role;
    }

    // Геттеры и сеттеры
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFactoryType() { return factoryType; }
    public void setFactoryType(String factoryType) { this.factoryType = factoryType; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
