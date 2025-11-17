package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserDTO;

import java.util.List;

public interface UserRepository {
    void createTable();

    List<String> selectAllUsers();
    List<String> selectAllUsersSortedByLogin();
    String selectUserById(int id);
    String selectUserByLogin(String Login);

    List<UserDTO> selectAllUsersAsDTO();
    List<UserDTO> selectAllUsersSortedByLoginAsDTO();
    UserDTO selectUserByIdAsDTO(int id);
    UserDTO selectUserByLoginAsDTO(String Login);

    int selectIdByLogin(String login);

    void updateFactoryTypeById(String factoryType, int id);
    void updatePasswordById(String password, int id);
    void updateLoginById(String login, int id);
    void updateRoleById(String role, int id);

    void deleteUserById(int id);
    void deleteAllUsers();

    void addUser(String factoryType, String login, String password, String role);

    boolean existsUser(int id);
    boolean existsUser(String login);
}
