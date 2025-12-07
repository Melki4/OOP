package service;

import ru.ssau.tk._repfor2lab_._OOP_.Dao.JdbcUserRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserReturnDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;

import java.util.List;

public class UserService {
    private final JdbcUserRepository userRepository;

    {
        userRepository = new JdbcUserRepository();
    }

    public List<UserReturnDTO> findAllUsers(){
        return userRepository.findAllUsersAsDTO();
    }

    public List<UserReturnDTO> findAllUsersSorted(){
        return userRepository.findAllUsersSortedByLoginAsDTO();
    }

    public UserDTO findByLogin(String login){
        Users users = userRepository.findByLogin(login);
        return new UserDTO(users.getUserId(), users.getFactoryType(), users.getLogin(), users.getRole());
    }

    public Integer findIdByLogin(String login){
        return userRepository.selectIdByLogin(login);
    }

    public boolean existsById(Integer id){
        return userRepository.existsUserById(id);
    }

    public boolean existsByLogin(String login){
        return userRepository.existsUserByLogin(login);
    }

    public void updateFactory(String factoryType, Integer id){
        userRepository.updateFactoryTypeById(factoryType, id);
    }

    public void updateLogin(String login, Integer id){
        userRepository.updateLoginById(login, id);
    }

    public void updatePassword(String password, Integer id){
        userRepository.updatePasswordById(password, id);
    }

    public void updateRole(String role, Integer id){
        userRepository.updateRoleById(role, id);
    }

    public void deleteAllUsers(){
        userRepository.deleteAllUsers();
    }

    public void deleteUser(Integer id){
        userRepository.deleteUserById(id);
    }
}
