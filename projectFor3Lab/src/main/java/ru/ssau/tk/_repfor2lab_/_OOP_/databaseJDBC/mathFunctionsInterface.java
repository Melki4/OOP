package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class mathFunctionsInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(mathFunctionsInterface.class);

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
            LOGGER.info("Данные успешно получены");
            return list;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе данных");
            throw new RuntimeException(e);
        }
    }
    public List<String> selectMathFunctionByUserId(int id){

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
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateFunctionNameByFunctionId(String name, int id){

        String sql = loaderSQL.loadSQL("scripts\\math_functions\\math_function_name_update.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, name);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteMathFunctionByFunctionId(int id){
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\delete_math_function.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addMathFunction(String function_name, int amount_of_dots, double left_boarder,
                                double right_boarder, int owner_id, String function_type){
        String sql = loaderSQL.loadSQL("scripts\\math_functions\\insert_math_function.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, function_name);
            statement.setInt(2, amount_of_dots);
            statement.setDouble(3, left_boarder);
            statement.setDouble(4, right_boarder);
            statement.setInt(5, owner_id);
            statement.setString(6, function_type);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
