package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.repositories.SimpleFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcSimpleFunctionRepository implements SimpleFunctionRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcSimpleFunctionRepository.class);

    public void createTable(){
        LOGGER.info("Начинаем создание таблицы простых ф-ций");
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\simple_functions_table_creation.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            statement.execute(sql);
            LOGGER.info("Таблица создана");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при создании таблицы");
            throw new RuntimeException(e);
        }
    }

    public List<SimpleFunctions> findAllSimpleFunctions() {

        LOGGER.info("Начинаем выбор всех ф-ций и вернём их как лист");
        List<SimpleFunctions> result = new ArrayList<>();

        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\select_all_simple_functions.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);

            SimpleFunctions boof;

            while(resultSet.next()){
                boof = new SimpleFunctions(resultSet.getString(1));

                result.add(boof);
            }
            if (result.isEmpty()){
                LOGGER.warn("Таблица пуста");
                throw new DataDoesNotExistException();
            }
            LOGGER.info("Все простые функции успешно получены");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех простых функций");
            throw new RuntimeException(e);
        }
    }

    public List<SimpleFunctions> findAllSimpleFunctionsSortedByLocalName() {
        LOGGER.info("Начинаем выбор всех сортированных ф-ций и вернём их как лист");
        List<SimpleFunctions> result = new ArrayList<>();

        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\select_all_s_f_ordered_by_name.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            SimpleFunctions boof;
            while(resultSet.next()){
                boof = new SimpleFunctions(resultSet.getString(1));

                result.add(boof);
            }
            if (result.isEmpty()){
                LOGGER.warn("Таблица простых ф-ций пуста");
                throw new DataDoesNotExistException();
            }
            LOGGER.info("Все отсортированные простые функции успешно получены");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех отсортированных простых функций");
            throw new RuntimeException(e);
        }
    }

    public void updateSimpleFunctionName(String oldName, String newName){
        LOGGER.info("Начинаем обновление локального имени для функции с именем: {}", oldName);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\local_name_update.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, newName);
            statement.setString(2, oldName);

            statement.executeUpdate();
            LOGGER.info("Локальное имя для функции со старым именем {} успешно обновлено", oldName);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении локального имени для функции с именем: {}", oldName);
            throw new RuntimeException(e);
        }
    }

    public void deleteSimpleFunctionByName(String localName){
        LOGGER.info("Начинаем удаление простой функции с кодом: {}", localName);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\delete_simple_function.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setString(1, localName);
            statement.execute();
            LOGGER.info("Функция с кодом {} успешно удалена", localName);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении функции с кодом: {}", localName);
            throw new RuntimeException(e);
        }
    }

    public void deleteAllFunctions(){
        LOGGER.info("Начинаем удаление всех простых функций");
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\truncate_table_simple_functions.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {
            statement.execute();
            LOGGER.info("Все простые функции успешно удалены");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении всех простых функций");
            throw new RuntimeException(e);
        }
        createTable();
    }

    public void createSimpleFunction(String localName){
        LOGGER.info("Начинаем добавление простой функции с именем: {}", localName);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\insert_simple_function.sql");
        try (var connection = connectionManager.open();var statement = connection.prepareStatement(sql)){
            statement.setString(1, localName);
            statement.execute();
            LOGGER.info("Простая функция с именем {} успешно добавлена", localName);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при добавлении простой функции с именем: {}", localName);
            throw new RuntimeException(e);
        }
    }

    public boolean existSimpleFunction(String localName) {
        LOGGER.info("Начинаем проверку на существование ф-ции с именем{}", localName);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\does_simple_function_exists.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {
            statement.setString(1, localName);
            var resultSet = statement.executeQuery();
            resultSet.next();
            boolean value = resultSet.getBoolean(1);
            if (value) LOGGER.info("Ф-ция существует");
            else LOGGER.info("Ф-ции не существует");
            return value;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при проверке существования простой функций");
            throw new RuntimeException(e);
        }
    }
}