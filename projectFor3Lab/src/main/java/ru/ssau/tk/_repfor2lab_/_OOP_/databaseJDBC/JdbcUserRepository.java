package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.DTOMapper;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserRepository implements UserRepository{
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

    public List<String> selectAllUsers(){
        LOGGER.info("Начинаем выбор всех пользователей");
        List<String> list = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\users\\select_all_users.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            StringBuilder boof;
            while(resultSet.next()){
                boof = new StringBuilder();
                boof.append(resultSet.getInt(1)).append(" ");
                boof.append(resultSet.getString(2)).append(" ");
                boof.append(resultSet.getString(3)).append(" ");
                boof.append(resultSet.getString(4)).append(" ");
                boof.append(resultSet.getString(5)).append(" ");
                list.add(boof.toString());
            }
            if (list.isEmpty()){
                LOGGER.warn("Таблица пуста");
                throw new DataDoesNotExistException();
            }
            LOGGER.info("Все пользователи успешно получены");
            return list;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех пользователей");
            throw new RuntimeException(e);
        }
    }

    public String selectUserById(int id){
        LOGGER.info("Начинаем поиск пользователя по ID: {}", id);

        String sql = loaderSQL.loadSQL("scripts\\users\\select_user_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            StringBuilder boof = new StringBuilder();

            if (!resultSet.next()){
                LOGGER.warn("Пользователя с таким айди нет");
                throw new DataDoesNotExistException();
            };

            boof.append(resultSet.getInt(1)).append(" ");
            boof.append(resultSet.getString(2)).append(" ");
            boof.append(resultSet.getString(3)).append(" ");
            boof.append(resultSet.getString(4)).append(" ");
            boof.append(resultSet.getString(5)).append(" ");

            LOGGER.info("Пользователь с ID {} успешно найден", id);
            return boof.toString();
        } catch (SQLException e) {
            LOGGER.warn("Пользователя с таким айди не существует или произошла ошибка при поиске пользователя по ID: {}", id);
            throw new RuntimeException(e);
        }
    }

    public List<UserDTO> selectAllUsersAsDTO() {
        LOGGER.info("Начинаем выбор всех пользователей и вернём их как лист DTO");
        List<String> rawData = selectAllUsers();
        List<UserDTO> result = new ArrayList<>();

        for (String data : rawData) {
            result.add(DTOMapper.toUserDTO(data));
        }
        LOGGER.info("Возвращаем список пользователей как лист DTO");
        return result;
    }

    public UserDTO selectUserByIdAsDTO(int id) {
        LOGGER.info("Начинаем поиск пользователя по айди и вернём его как DTO, айди{}", id);
        String rawData = selectUserById(id);
        return DTOMapper.toUserDTO(rawData);
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
        String sql = loaderSQL.loadSQL("scripts\\users\\delete_user_by_id.sql");
        List<String> all_functions;

        try {
            all_functions = selectAllUsers();
        } catch (DataDoesNotExistException e) {
            LOGGER.info("И так всё удалено");
            return;
        }

        List<Integer> all_codes = new ArrayList<>();
        for (var el : all_functions) all_codes.add(Integer.parseInt(el.split(" ")[0]));

        try (var connection = connectionManager.open();
             var statement = connection.prepareStatement(sql)) {

            // Отключаем автокоммит для большей производительности
            connection.setAutoCommit(false);

            for(var el : all_codes) {
                statement.setInt(1, el);
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit(); // Фиксируем все изменения

            LOGGER.info("Удаление прошло успешно");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении пользователя");
            throw new RuntimeException(e);
        }

        LOGGER.info("Все пользователи успешно удалены");
    }

    public void addUser(String factoryType, String login, String password, String role){
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

    public boolean existsUser(int id) {
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

    public boolean existsUser(String login) {
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