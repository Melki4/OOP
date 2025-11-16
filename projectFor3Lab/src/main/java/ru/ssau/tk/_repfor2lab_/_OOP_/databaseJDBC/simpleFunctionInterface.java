package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.DTOMapper;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunctionDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class simpleFunctionInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(simpleFunctionInterface.class);

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

    public List<String> selectAllSimpleFunctions(){
        LOGGER.info("Начинаем выбор всех простых функций");
        List<String> list = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\select_all_simple_functions.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            StringBuilder boof;
            while(resultSet.next()){
                boof = new StringBuilder();
                boof.append(resultSet.getInt(1)).append(" ");
                boof.append(resultSet.getString(2)).append(" ");
                boof.append(resultSet.getString(3)).append(" ");
                list.add(boof.toString());
            }
            LOGGER.info("Все простые функции успешно получены");
            return list;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех простых функций");
            throw new RuntimeException(e);
        }
    }

    public String selectSimpleFunctionByFunctionCode(String code){
        LOGGER.info("Начинаем поиск простой функции по коду: {}", code);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\select_simple_function_by_f_code.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, code);

            var resultSet = statement.executeQuery();

            StringBuilder boof = new StringBuilder();

            resultSet.next();

            boof.append(resultSet.getInt(1)).append(" ");
            boof.append(resultSet.getString(2)).append(" ");
            boof.append(resultSet.getString(3)).append(" ");

            LOGGER.info("Функция с кодом {} успешно найдена", code);
            return boof.toString();
        } catch (SQLException e) {
            LOGGER.warn("Ф-ции с таким кодом нет или произошла ошибка при поиске функции по коду: {}", code);
            throw new RuntimeException(e);
        }
    }

    public String selectSimpleFunctionNameByFunctionCode(String code){
        LOGGER.info("Начинаем поиск имени простой функции по коду: {}", code);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\select_simple_function_name_by_f_code.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, code);

            var resultSet = statement.executeQuery();

            resultSet.next();

            LOGGER.info("Имя функции с кодом {} успешно найдена", code);
            return resultSet.getString(1);
        } catch (SQLException e) {
            LOGGER.warn("Ф-ции с таким кодом нет или произошла ошибка при поиске имени функции по коду: {}", code);
            throw new RuntimeException(e);
        }
    }

    public List<SimpleFunctionDTO> selectAllSimpleFunctionsAsDTO() {
        LOGGER.info("Начинаем выбор всех ф-ций и вернём их как лист DTO");
        List<String> rawData = selectAllSimpleFunctions();
        List<SimpleFunctionDTO> result = new ArrayList<>();

        for (String data : rawData) {
            result.add(DTOMapper.toSimpleFunctionDTO(data));
        }
        LOGGER.info("Возвращаем список ф-ций как лист DTO");
        return result;
    }

    public SimpleFunctionDTO selectSimpleFunctionByFunctionCodeAsDTO(String code) {
        LOGGER.info("Начинаем поиск ф-ции по коду и вернём её как DTO, код{}", code);
        String rawData = selectSimpleFunctionByFunctionCode(code);
        return DTOMapper.toSimpleFunctionDTO(rawData);
    }

    public void updateLocalNameByFunctionCode(String localName, String code){
        LOGGER.info("Начинаем обновление локального имени для функции с кодом: {}", code);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\local_name_update.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, localName);
            statement.setString(2, code);

            statement.executeUpdate();
            LOGGER.info("Локальное имя для функции с кодом {} успешно обновлено", code);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении локального имени для функции с кодом: {}", code);
            throw new RuntimeException(e);
        }
    }

    public void deleteSimpleFunctionByFunctionCode(String code){
        LOGGER.info("Начинаем удаление простой функции с кодом: {}", code);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\delete_simple_function.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setString(1, code);
            statement.execute();
            LOGGER.info("Функция с кодом {} успешно удалена", code);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении функции с кодом: {}", code);
            throw new RuntimeException(e);
        }
    }

    public void deleteAllFunctions(){
        LOGGER.info("Начинаем удаление всех простых функций");
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\drop_table_simple_functions.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {
            statement.execute();
            LOGGER.info("Все простые функции успешно удалены, таблица пересоздана");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении всех простых функций");
            throw new RuntimeException(e);
        }
        createTable();
    }

    public void addSimpleFunction(String functionCode, String localName){
        LOGGER.info("Начинаем добавление простой функции с кодом: {}", functionCode);
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\insert_simple_function.sql");
        try (var connection = connectionManager.open();var statement = connection.prepareStatement(sql)){
            statement.setString(1, functionCode);
            statement.setString(2, localName);

            statement.execute();
            LOGGER.info("Простая функция с кодом {} успешно добавлена", functionCode);
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при добавлении простой функции с кодом: {}", functionCode);
            throw new RuntimeException(e);
        }
    }
}