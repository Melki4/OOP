package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.DTOMapper;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.PointDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPointRepository implements PointRepository{
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcPointRepository.class);

    public void createTable(){
        LOGGER.info("Приступаем к созданию таблицы точек");
        String sql = loaderSQL.loadSQL("scripts\\points\\points_table_creation.sql");
        try (var connection = connectionManager.open(); var statement = connection.createStatement()){
            statement.execute(sql);
            LOGGER.info("Таблица создана");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка на этапе создания таблицы точек");
            throw new RuntimeException(e);
        }
    }

    public List<String> selectAllPoints(){
        LOGGER.info("Начинаем выбор всех точек");
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

            if (list.isEmpty()){
                LOGGER.warn("Таблица пуста");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("Всё корректно обработалось, возвращаем список");
            return list;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка с выбором информации о точках");
            throw new RuntimeException(e);
        }
    }

    public List<String> selectPointsByFunctionId(int id){
        LOGGER.info("Начинаем поиск точек по айди функции которой они принадлежат {}", id);
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

            if (list.isEmpty()){
                LOGGER.warn("В ф-ции не были заданы точки");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("Точки корректно нашлись");
            return list;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе точек по айди ф-ции {}", id);
            throw new RuntimeException(e);
        }
    }

    public String selectPointByPointId(int id) {
        LOGGER.info("Начинаем поиск точки по её айди {}", id);
        String sql = loaderSQL.loadSQL("scripts\\points\\select_point_by_point_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            StringBuilder boof;

            if (!resultSet.next()){
                LOGGER.warn("Точки с таким айди нет");
                throw new DataDoesNotExistException();
            }

            boof = new StringBuilder();
            boof.append(resultSet.getInt(1)).append(" ");
            boof.append(resultSet.getDouble(2)).append(" ");
            boof.append(resultSet.getDouble(3)).append(" ");
            boof.append(resultSet.getInt(4)).append(" ");
            boof.append(resultSet.getString(5)).append(" ");

            LOGGER.info("Точка корректно нашлась");
            return boof.toString();
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе точки по айди {}", id);
            throw new RuntimeException(e);
        }
    }

    public List<PointDTO> selectAllPointsAsDTO() {
        LOGGER.info("Начинаем выбор всех точек и вернём их как лист DTO");
        List<String> rawData = selectAllPoints();
        List<PointDTO> result = new ArrayList<>();

        for (String data : rawData) {
            result.add(DTOMapper.toPointDTO(data));
        }
        LOGGER.info("Возвращаем список точек как лист DTO");
        return result;
    }

    public List<PointDTO> selectPointsByFunctionIdAsDTO(int id) {
        LOGGER.info("Начинаем выбор точек по айди ф-ции и вернём их как лист DTO, айди{}", id);
        List<String> rawData = selectPointsByFunctionId(id);
        List<PointDTO> result = new ArrayList<>();

        for (String data : rawData) {
            result.add(DTOMapper.toPointDTO(data));
        }
        LOGGER.info("Возвращаем список точек ф-ции как лист DTO");
        return result;
    }

    @Override
    public PointDTO selectPointByPointIdAsDTO(int id) {
        LOGGER.info("Начинаем поиск точки и вернём её как DTO");
        String rawData = selectPointByPointId(id);
        PointDTO result = DTOMapper.toPointDTO(rawData);

        LOGGER.info("Возвращаем точку как DTO");
        return result;
    }

    public int selectPointIdByXValueAndFunctionId(double x, int function_id){
        LOGGER.info("Приступаем к поиску айди по значению икса и айди ф-ции");
        String sql = loaderSQL.loadSQL("scripts\\points\\select_point_by_x_value_and_point_id.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setDouble(1, x);
            statement.setInt(2, function_id);

            var resultSet = statement.executeQuery();

            if(!resultSet.next()){
                LOGGER.warn("Такой точки нет");
                throw new DataDoesNotExistException();
            };

            LOGGER.info("Айди точки был найден");
            return resultSet.getInt(1);
        } catch (SQLException e) {
            LOGGER.info("Айди не был найден");
            return -1;
        }
    }

    public void updateXValueById(Double x_value, int id){
        LOGGER.info("Обновить x для точки с айди {}", id);
        String sql = loaderSQL.loadSQL("scripts\\points\\x_value_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setDouble(1, x_value);
            statement.setInt(2, id);

            statement.executeUpdate();
            LOGGER.info("Обновление для X прошло успешно");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении значения X");
            throw new RuntimeException(e);
        }
    }

    public void updateYValueById(Double y_value, int id){
        LOGGER.info("Начинаем обновление Y для точки с айди {}", id);
        String sql = loaderSQL.loadSQL("scripts\\points\\y_value_update_by_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setDouble(1, y_value);
            statement.setInt(2, id);

            statement.executeUpdate();
            LOGGER.info("Обновление прошло успешно");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении Y");
            throw new RuntimeException(e);
        }
    }

    public void deletePointById(int id){
        LOGGER.info("Начинаем удаление точки с айди {}", id);
        String sql = loaderSQL.loadSQL("scripts\\points\\delete_point_by_id.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            statement.execute();
            LOGGER.info("Удаление прошло успешно");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении точки");
            throw new RuntimeException(e);
        }
    }

    public void deleteAllPoints(){
        LOGGER.info("Начинаем удаление всех точек");
        String sql = loaderSQL.loadSQL("scripts\\points\\truncate_table_points.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)) {
            statement.execute();
            LOGGER.info("все точки были удалены дропом таблицы и таблица была создана заново, но в транзакции");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при удалении всех точек, т.к. есть транзакция - значения не были удалены");
            throw new RuntimeException(e);
        }
    }

    public void addPoint(Double x_value, Double y_value, int id){
        LOGGER.info("начинаем добавление одной точки");
        String sql = loaderSQL.loadSQL("scripts\\points\\insert_point.sql");
        try (var connection = connectionManager.open();var statement = connection.prepareStatement(sql)){
            statement.setDouble(1, x_value);
            statement.setDouble(2, y_value);
            statement.setInt(3, id);

            statement.execute();
            LOGGER.info("Успешно добавили");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при добавлении точки");
            throw new RuntimeException(e);
        }
    }

    public void bulkInsertPointsDirect(List<Point> points, int function_id) {
        LOGGER.info("Добавляем в таблицу {} точек", points.size());
        String sql = loaderSQL.loadSQL("scripts\\points\\insert_point.sql");

        try (var connection = connectionManager.open();
             var statement = connection.prepareStatement(sql)) {

            // Отключаем автокоммит для большей производительности
            connection.setAutoCommit(false);

            for (Point point : points) {
                statement.setDouble(1, point.x);
                statement.setDouble(2, point.y);
                statement.setInt(3, function_id);
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit(); // Фиксируем все изменения
            LOGGER.info("Все точки были успешно добавлены");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при добавлении точек");
            throw new RuntimeException(e);
        }
    }
}
