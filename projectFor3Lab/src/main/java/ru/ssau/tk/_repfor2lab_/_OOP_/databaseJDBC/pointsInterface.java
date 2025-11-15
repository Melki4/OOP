package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class pointsInterface {
    public void createTable(){
        String sql = loaderSQL.loadSQL("scripts\\points\\points_table_creation.sql");
        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<String> selectAllPoints(){
        List<String> list = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\points\\select_all_points.sql");

        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            StringBuilder boof;
            while(resultSet.next()){
                boof = new StringBuilder();
                boof.append(resultSet.getInt(1)).append(" ");
                boof.append(resultSet.getDouble(2)).append(" ");
                boof.append(resultSet.getDouble(3)).append(" ");
                boof.append(resultSet.getInt(4)).append(" ");
                boof.append(resultSet.getString(5)).append(" ");
                list.add(boof.toString());
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<String> selectPointsById(int id){
        List<String> list = new ArrayList<>();
        String sql = loaderSQL.loadSQL("scripts\\points\\select_points_by_function_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            StringBuilder boof;
            while(resultSet.next()){
                boof = new StringBuilder();
                boof.append(resultSet.getInt(1)).append(" ");
                boof.append(resultSet.getDouble(2)).append(" ");
                boof.append(resultSet.getDouble(3)).append(" ");
                boof.append(resultSet.getInt(4)).append(" ");
                boof.append(resultSet.getString(5)).append(" ");
                list.add(boof.toString());
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateXValueById(Double x_value, int id){

        String sql = loaderSQL.loadSQL("scripts\\points\\x_value_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setDouble(1, x_value);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public void updateYValueById(Double y_value, int id){

        String sql = loaderSQL.loadSQL("scripts\\points\\y_value_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setDouble(1, y_value);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public void deletePointById(int id){
        String sql = loaderSQL.loadSQL("scripts\\points\\delete_point_by_id.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAllFunctions(){
        String sql = loaderSQL.loadSQL("scripts\\points\\drop_table_points.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        createTable();
    }
    public void addPoint(Double x_value, Double y_value, int id){
        String sql = loaderSQL.loadSQL("scripts\\points\\insert_point.sql");
        try (var connection = connectionManager.open();var statement = connection.prepareStatement(sql)){
            statement.setDouble(1, x_value);
            statement.setDouble(2, y_value);
            statement.setInt(3, id);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
