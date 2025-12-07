package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;

import java.util.List;

public interface UserRepository {
    void createTable();

    List<Users> findAllUsers();
    List<Users> findAllUsersSortedByLogin();

    int selectIdByLogin(String login);

    void updateFactoryTypeById(String factoryType, int id);
    void updatePasswordById(String password, int id);
    void updateLoginById(String login, int id);
    void updateRoleById(String role, int id);

    boolean deleteUserById(int id);
    boolean deleteAllUsers();

    Users createUser(String factoryType, String login, String password, String role);

    boolean existsUserById(int id);
    boolean existsUserByLogin(String login);
}
