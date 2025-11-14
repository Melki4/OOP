package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class simpleFunctionInterface {
    public void createTable(){
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\simple_functions_table_creation.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<String> selectAllSimpleFunctions(){
        List<String> list = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\select_all_simple_functions.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            StringBuilder boof;
            while(resultSet.next()){
                boof = new StringBuilder();
                boof.append(resultSet.getString(1)).append(" ");
                boof.append(resultSet.getString(2)).append(" ");
                list.add(boof.toString());
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String selectSimpleFunctionByFunctionCode(String code){

        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\select_local_name_by_f_code.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, code);

            var resultSet = statement.executeQuery();

            resultSet.next();

            return resultSet.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateLocalNameByFunctionCode(String localName, String code){

        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\local_name_update.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, localName);
            statement.setString(2, code);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteSimpleFunctionByFunctionCode(String code){
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\delete_simple_function.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setString(1, code);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addSimpleFunction(String functionCode, String localName){
        String sql = loaderSQL.loadSQL("scripts\\simple_functions\\insert_simple_function.sql");
        try (var connection = connectionManager.open();var statement = connection.prepareStatement(sql)){
            statement.setString(1, functionCode);
            statement.setString(2, localName);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
