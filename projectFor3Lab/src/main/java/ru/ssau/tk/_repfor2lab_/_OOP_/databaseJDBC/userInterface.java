package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class userInterface {
    public void createTable(){
        String sql = loaderSQL.loadSQL("scripts\\users\\users_table_creation.sql");

        try (var connection = connectionManager.open();var statement = connection.createStatement()){
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<String> selectAllUsers(){
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
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String selectUserById(int id){

        String sql = loaderSQL.loadSQL("scripts\\users\\select_user_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            StringBuilder boof = new StringBuilder();

            resultSet.next();

            boof.append(resultSet.getInt(1)).append(" ");
            boof.append(resultSet.getString(2)).append(" ");
            boof.append(resultSet.getString(3)).append(" ");
            boof.append(resultSet.getString(4)).append(" ");
            boof.append(resultSet.getString(5)).append(" ");

            return boof.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int selectIdByLogin(String login){

        String sql = loaderSQL.loadSQL("scripts\\users\\select_id_by_login.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, login);

            var resultSet = statement.executeQuery();

            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException e) {
//            throw new RuntimeException(e);
            return -1;
        }
    }
    public void updateFactoryTypeById(String factoryType, int id){

        String sql = loaderSQL.loadSQL("scripts\\users\\factory_type_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, factoryType);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public void updatePasswordById(String password, int id){

        String sql = loaderSQL.loadSQL("scripts\\users\\password_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, password);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public void updateLoginById(String login, int id){

        String sql = loaderSQL.loadSQL("scripts\\users\\login_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, login);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public void updateRoleById(String role, int id){

        String sql = loaderSQL.loadSQL("scripts\\users\\role_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setString(1, role);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    public void deleteUserById(int id){
        String sql = loaderSQL.loadSQL("scripts\\users\\delete_user_by_id.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addUser(String factoryType, String login, String password, String role){
        String sql = loaderSQL.loadSQL("scripts\\users\\insert_user.sql");
        try (var connection = connectionManager.open();var statement = connection.prepareStatement(sql)){
            statement.setString(1, factoryType);
            statement.setString(2, login);
            statement.setString(3, password);
            statement.setString(4, role);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
