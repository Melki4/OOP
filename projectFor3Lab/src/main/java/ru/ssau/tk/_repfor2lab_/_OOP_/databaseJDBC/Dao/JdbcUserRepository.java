package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserReturnDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.repositories.UserRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserRepository implements UserRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUserRepository.class);

    public void createTable(){
        LOGGER.info("Начинаем создание таблицы пользователей");
        String sql = loaderSQL.loadSQL("scripts\\users\\users_table_creation.sql");

        try (var connection = connectionManager.open();var statement = connection.createStatement()){
            statement.execute(sql);
            LOGGER.info("Таблица пользователей успешно создана");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при создании таблицы пользователей");
            throw new RuntimeException(e);
        }
    }

    public List<Users> findAllUsers() {
        LOGGER.info("Начинаем возврат списка всех пользователей и вернём их как лист DTO");
        List<Users> result = new ArrayList<>();

        String sql = loaderSQL.loadSQL("scripts\\users\\select_all_users.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);

            Users boof;
            while(resultSet.next()){
                boof = new Users(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5));

                result.add(boof);
            }
            if (result.isEmpty()){
                LOGGER.warn("Таблица пуста");
                throw new DataDoesNotExistException();
            }
            LOGGER.info("Все пользователи успешно получены");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех пользователей");
            throw new RuntimeException(e);
        }
    }

    public List<UserReturnDTO> findAllUsersAsDTO() {
        LOGGER.info("Начинаем возврат списка всех пользователей и вернём их как лист DTO");
        List<UserReturnDTO> result = new ArrayList<>();

        String sql = loaderSQL.loadSQL("scripts\\users\\select_all_users.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);

            UserReturnDTO boof;
            while(resultSet.next()){
                boof = new UserReturnDTO(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(5));

                result.add(boof);
            }
            if (result.isEmpty()){
                LOGGER.warn("Таблица пуста");
                throw new DataDoesNotExistException();
            }
            LOGGER.info("Все пользователи успешно получены");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех пользователей");
            throw new RuntimeException(e);
        }
    }

    public List<Users> findAllUsersSortedByLogin() {
        LOGGER.info("Начинаем возврат списка всех пользователей отсортированных по логинам");

        List<Users> result = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\users\\select_all_users_sorted_by_login.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            Users boof;

            while(resultSet.next()){
                boof = new Users(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5));

                result.add(boof);
            }
            if (result.isEmpty()){
                LOGGER.warn("Таблица с пользователями пуста");
                throw new DataDoesNotExistException();
            }
            LOGGER.info("Все сортированные пользователи успешно получены");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех отсортированных пользователей");
            throw new RuntimeException(e);
        }
    }

    public List<UserReturnDTO> findAllUsersSortedByLoginAsDTO() {
        LOGGER.info("Начинаем возврат списка dto всех пользователей отсортированных по логинам");

        List<UserReturnDTO> result = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\users\\select_all_users_sorted_by_login.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            UserReturnDTO boof;

            while(resultSet.next()){
                boof = new UserReturnDTO(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(5));

                result.add(boof);
            }
            if (result.isEmpty()){
                LOGGER.warn("Таблица с пользователями пуста ");
                throw new DataDoesNotExistException();
            }
            LOGGER.info("Все сортированные пользователи успешно получены ");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех отсортированных пользователей ");
            throw new RuntimeException(e);
        }
    }

    public int selectIdByLogin(String login){
        LOGGER.info("Начинаем поиск ID пользователя по логину: {}", login);

        String sql = loaderSQL.loadSQL("scripts\\users\\select_id_by_login.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, login);

            var resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                LOGGER.warn("Пользователя с таким логином нет");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("ID пользователя с логином {} успешно найден", login);
            return resultSet.getInt(1);
        } catch (SQLException e) {
            LOGGER.warn("Пользователь с логином не найден или произошла непредвиденная ошибка");
            return -1;
        }
    }

    public void updateFactoryTypeById(String factoryType, int id){
        LOGGER.info("Начинаем обновление типа фабрики для пользователя с ID: {}", id);
        String sql = loaderSQL.loadSQL("scripts\\users\\factory_type_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, factoryType);
            statement.setInt(2, id);

            statement.executeUpdate();
            LOGGER.info("Тип фабрики для пользователя с ID {} успешно обновлен", id);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении типа фабрики для пользователя с ID: {}", id);
            throw new RuntimeException(e);
        }
    }

    public void updatePasswordById(String password, int id){
        LOGGER.info("Начинаем обновление пароля для пользователя с ID: {}", id);
        String sql = loaderSQL.loadSQL("scripts\\users\\password_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, password);
            statement.setInt(2, id);

            statement.executeUpdate();
            LOGGER.info("Пароль для пользователя с ID {} успешно обновлен", id);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении пароля для пользователя с ID: {}", id);
            throw new RuntimeException(e);
        }
    }

    public void updateLoginById(String login, int id){
        LOGGER.info("Начинаем обновление логина для пользователя с ID: {}", id);
        String sql = loaderSQL.loadSQL("scripts\\users\\login_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, login);
            statement.setInt(2, id);

            statement.executeUpdate();
            LOGGER.info("Логин для пользователя с ID {} успешно обновлен", id);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении логина для пользователя с ID: {}", id);
            throw new RuntimeException(e);
        }
    }

    public void updateRoleById(String role, int id){
        LOGGER.info("Начинаем обновление роли для пользователя с ID: {}", id);
        String sql = loaderSQL.loadSQL("scripts\\users\\role_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, role);
            statement.setInt(2, id);

            statement.executeUpdate();
            LOGGER.info("Роль для пользователя с ID {} успешно обновлена", id);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении роли для пользователя с ID: {}", id);
            throw new RuntimeException(e);
        }
    }

    public void deleteUserById(int id){
        LOGGER.info("Начинаем удаление пользователя с ID: {}", id);
        String sql = loaderSQL.loadSQL("scripts\\users\\delete_user_by_id.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            statement.execute();
            LOGGER.info("Пользователь с ID {} успешно удален", id);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении пользователя с ID: {}", id);
            throw new RuntimeException(e);
        }
    }

    public void deleteAllUsers(){
        LOGGER.info("Начинаем удаление всех пользователей");

        String sql = loaderSQL.loadSQL("scripts\\users\\truncate_all_users.sql");

        try (var connection = connectionManager.open();
             var statement = connection.createStatement()) {
            statement.execute(sql);
            LOGGER.info("Все пользователи успешно удалены");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении пользователей");
            throw new RuntimeException(e);
        }
    }

    public void createUser(String factoryType, String login, String password, String role){
        LOGGER.info("Начинаем добавление нового пользователя с логином: {}", login);
        String sql = loaderSQL.loadSQL("scripts\\users\\insert_user.sql");
        try (var connection = connectionManager.open();var statement = connection.prepareStatement(sql)){
            statement.setString(1, factoryType);
            statement.setString(2, login);
            statement.setString(3, password);
            statement.setString(4, role);

            statement.execute();
            LOGGER.info("Пользователь с логином {} успешно добавлен", login);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при добавлении пользователя с логином: {}", login);
            throw new RuntimeException(e);
        }
    }

    public boolean existsUserById(int id) {
        LOGGER.info("Начинаем проверку на существование пользователя с айди {}", id);
        String sql = loaderSQL.loadSQL("scripts\\users\\does_user_exists.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            resultSet.next();
            boolean value = resultSet.getBoolean(1);
            if (value) LOGGER.info("Пользователь существует");
            else LOGGER.info("Пользователя не существует");
            return value;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при проверке существования пользователя");
            throw new RuntimeException(e);
        }
    }

    public boolean authenticateUser(String login, String password) {
        String sql = loaderSQL.loadSQL("scripts\\users\\select_password_where_login_equals.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                // Сравниваем пароли (в реальном приложении - хэши!)
                return password.equals(storedPassword);
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Authentication error", e);
        }
    }

    public Users findByLogin(String login) {
        String sql = loaderSQL.loadSQL("scripts\\users\\select_user_by_login.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Users user = new Users();
                user.setUserId(resultSet.getInt("user_id"));
                user.setLogin(resultSet.getString("login"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
                user.setFactoryType(resultSet.getString("factory_type"));
                return user;
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by login", e);
        }
    }

    public boolean existsUserByLogin(String login) {
        LOGGER.info("Начинаем проверку на существование пользователя с логином {}", login);
        String sql = loaderSQL.loadSQL("scripts\\users\\does_user_exists_by_login.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);

            var resultSet = statement.executeQuery();
            resultSet.next();
            boolean value = resultSet.getBoolean(1);

            if (value) LOGGER.info("Пользователь с таким логином существует");
            else LOGGER.info("Пользователя с таким логином не существует");
            return value;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при проверке существования пользователя с логином");
            throw new RuntimeException(e);
        }
    }
}