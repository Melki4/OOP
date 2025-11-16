package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.DTOMapper;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctionDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcMathFunctionRepository implements MathFunctionRepository{
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcMathFunctionRepository.class);

    public void createTable(){
        LOGGER.info("Приступаем к созданию таблицы");

        String sql = loaderSQL.loadSQL("scripts\\math_functions\\math_functions_table_creation.sql");
        String sql1 = loaderSQL.loadSQL("scripts\\math_functions\\alter_math_function_table.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            if (statement.execute(sql)){
                statement.execute(sql1);
                LOGGER.info("Таблица создана");
            }
            else {
                LOGGER.info("Таблица и так существует");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> selectAllMathFunctions(){
        LOGGER.info("Выбор всех данных в таблице");
        List<String> list = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\select_all_math_functions.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            StringBuilder boof;
            while(resultSet.next()){
                boof = new StringBuilder();

                boof.append(resultSet.getInt(1)).append(" ");
                boof.append(resultSet.getString(2)).append(" ");
                boof.append(resultSet.getInt(3)).append(" ");
                boof.append(resultSet.getDouble(4)).append(" ");
                boof.append(resultSet.getDouble(5)).append(" ");
                boof.append(resultSet.getInt(6)).append(" ");
                boof.append(resultSet.getString(7)).append(" ");
                boof.append(resultSet.getString(8)).append(" ");
                boof.append(resultSet.getString(9)).append(" ");

                list.add(boof.toString());
            }

            if (list.isEmpty()){
                LOGGER.warn("Таблица пуста");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("Данные успешно получены");
            return list;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе всех данных");
            throw new RuntimeException(e);
        }
    }

    public List<String> selectMathFunctionsByUserId(int id){
        LOGGER.info("Начинаем поиск ф-ций пользователя по его айди");
        List<String> list = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\select_math_functions_by_user.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            StringBuilder boof;
            while(resultSet.next()){
                boof = new StringBuilder();

                boof.append(resultSet.getInt(1)).append(" ");
                boof.append(resultSet.getString(2)).append(" ");
                boof.append(resultSet.getInt(3)).append(" ");
                boof.append(resultSet.getDouble(4)).append(" ");
                boof.append(resultSet.getDouble(5)).append(" ");
                boof.append(resultSet.getInt(6)).append(" ");
                boof.append(resultSet.getString(7)).append(" ");
                boof.append(resultSet.getString(8)).append(" ");
                boof.append(resultSet.getString(9)).append(" ");

                list.add(boof.toString());
            }

            if (list.isEmpty()){
                LOGGER.warn("У пользователя нет ф-ций");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("Возвращаем список ф-ций");
            return list;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при поиске ф-ций пользователя с айди {}", id);
            throw new RuntimeException(e);
        }
    }

    public String selectMathFunctionsByName(String name) {
        LOGGER.info("Начинаем поиск ф-ций пользователя по её имени");
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\select_math_functions_by_name.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, name);

            var resultSet = statement.executeQuery();

            StringBuilder boof;

            if (!resultSet.next()){
                LOGGER.warn("Такой ф-ции в бд нет");
                throw new DataDoesNotExistException();
            };

            boof = new StringBuilder();

            boof.append(resultSet.getInt(1)).append(" ");
            boof.append(resultSet.getString(2)).append(" ");
            boof.append(resultSet.getInt(3)).append(" ");
            boof.append(resultSet.getDouble(4)).append(" ");
            boof.append(resultSet.getDouble(5)).append(" ");
            boof.append(resultSet.getInt(6)).append(" ");
            boof.append(resultSet.getString(7)).append(" ");
            boof.append(resultSet.getString(8)).append(" ");
            boof.append(resultSet.getString(9)).append(" ");

            LOGGER.info("Возвращаем ф-цию");
            return boof.toString();
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при поиске ф-ций пользователя с именем {}", name);
            throw new RuntimeException(e);
        }
    }

    public List<MathFunctionDTO> selectAllMathFunctionsAsDTO() {
        LOGGER.info("Начинаем выбор всех ф-ций и вернём их как лист DTO");
        List<String> rawData = selectAllMathFunctions();
        List<MathFunctionDTO> result = new ArrayList<>();

        for (String data : rawData) {
            result.add(DTOMapper.toMathFunctionDTO(data));
        }
        LOGGER.info("Возвращаем список ф-ций как лист DTO");
        return result;
    }

    public List<MathFunctionDTO> selectMathFunctionsByUserIdAsDTO(int id) {
        LOGGER.info("Начинаем выбор ф-ций по айди владельца и вернём их как лист DTO, айди{}", id);
        List<String> rawData = selectMathFunctionsByUserId(id);
        List<MathFunctionDTO> result = new ArrayList<>();

        for (String data : rawData) {
            result.add(DTOMapper.toMathFunctionDTO(data));
        }
        LOGGER.info("Возвращаем список ф-ций владельца как лист DTO");
        return result;
    }

    public MathFunctionDTO selectMathFunctionsByNameAsDTO(String name) {
        LOGGER.info("Начинаем поиск ф-ции по имени и вернём её как  DTO, имя{}", name);
        String rawData = selectMathFunctionsByName(name);
        MathFunctionDTO result = DTOMapper.toMathFunctionDTO(rawData);
        LOGGER.info("Возвращаем ф-цию как DTO");
        return result;
    }

    public void updateFunctionNameByFunctionId(String name, int id){
        LOGGER.info("Начинаем обновление имени ф-ции по её айди {}", id);
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\math_function_name_update.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, name);
            statement.setInt(2, id);

            statement.executeUpdate();
            LOGGER.info("Успешно обновили");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении имени ф-ции по айди{}", id);
            throw new RuntimeException(e);
        }
    }

    public void deleteMathFunctionByFunctionId(int id){
        LOGGER.info("Начинаем удаление мат. ф-ции по айди");
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\delete_math_function.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            statement.execute();
            LOGGER.info("Успешно удалили");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении мат. ф-ции по айди{}", id);
            throw new RuntimeException(e);
        }
    }

    public void deleteAllFunctions(){
        LOGGER.info("Пробуем удалить все мат. ф-ции");
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\delete_math_function.sql");

        List<String> all_functions;

        try {
            all_functions = selectAllMathFunctions();
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
            LOGGER.warn("Произошла ошибка при обработке запроса по удалению какой-то мат ф.ции");
            throw new RuntimeException(e);
        }
    }

    public void addMathFunction(String function_name, int amount_of_dots, double left_boarder,
                                double right_boarder, int owner_id, String function_type){
        LOGGER.info("Начинаем добавление мат. ф-ции");

        String sql = loaderSQL.loadSQL("scripts\\math_functions\\insert_math_function.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, function_name);
            statement.setInt(2, amount_of_dots);
            statement.setDouble(3, left_boarder);
            statement.setDouble(4, right_boarder);
            statement.setInt(5, owner_id);
            statement.setString(6, function_type);

            statement.execute();
            LOGGER.info("Всё прошло успешно, ф-ция занесена в бд");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка с добавлением ф-ции в бд");
            throw new RuntimeException(e);
        }
    }

    public boolean existsFunction(String name) {
        LOGGER.info("Начинаем проверку на существование ф-ции с именем {}", name);
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\does_user_exists.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);

            var resultSet = statement.executeQuery();
            resultSet.next();
            boolean value = resultSet.getBoolean(1);

            if (value) LOGGER.info("Ф-ция существует");
            else LOGGER.info("Ф-ция не существует");
            return value;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при проверке существования ф-ции");
            throw new RuntimeException(e);
        }
    }
}