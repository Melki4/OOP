package ru.ssau.tk._repfor2lab_._OOP_.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userID;

    @Column(name = "factory_type", nullable = false, length = 16)
    private String factoryType = "array";

    @Column(name = "login", nullable = false, length = 32, unique = true)
    private String login;

    @Column(name = "password", nullable = false, length = 256)
    private String password;

    @Column(name = "role", nullable = false, length = 8)
    private String role;

    @OneToMany(mappedBy = "users")
    private List<MathFunctions> mathFunctions;

    public Users(){}
    public Users(String login, String password, String role){
        this.login = login;
        this.password = password;
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

    public List<MathFunctions> getMathFunctions() {
        return mathFunctions;
    }

    public void setMathFunctions(List<MathFunctions> mathFunctions) {
        this.mathFunctions = mathFunctions;
    }
}
