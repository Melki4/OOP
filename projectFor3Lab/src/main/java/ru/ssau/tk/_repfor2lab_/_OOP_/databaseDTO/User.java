package ru.ssau.tk._repfor2lab_._OOP_.databaseDTO;

public class User {
    private Long userId;
    private String factoryType;
    private String login;
    private String password;
    private String role;

    // Конструкторы
    public User() {}

    public User(Long userId, String factoryType, String login, String password, String role) {
        this.userId = userId;
        this.factoryType = factoryType;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    // Геттеры и сеттеры
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFactoryType() { return factoryType; }
    public void setFactoryType(String factoryType) { this.factoryType = factoryType; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
