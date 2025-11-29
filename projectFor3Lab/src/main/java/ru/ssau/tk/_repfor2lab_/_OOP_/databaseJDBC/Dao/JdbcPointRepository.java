package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.PointsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Points;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.repositories.PointRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.connectionManager;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils.loaderSQL;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPointRepository implements PointRepository {
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

    public List<Points> findPointsByFunctionId(int id) {
        LOGGER.info("Начинаем выбор точек по айди ф-ции и вернём их как лист, айди{}", id);
        List<Points> result = new ArrayList<>();

        String sql = loaderSQL.loadSQL("scripts\\points\\select_points_by_function_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            Points boof;

            while(resultSet.next()){
                boof = new Points(resultSet.getInt(1),
                        resultSet.getDouble(2),
                        resultSet.getDouble(3),
                        resultSet.getInt(4));

                result.add(boof);
            }

            if (result.isEmpty()){
                LOGGER.warn("В ф-ции не были заданы точки");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("Точки корректно нашлись");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе точек по айди ф-ции {}", id);
            throw new RuntimeException(e);
        }
    }

    public List<PointsDTO> findPointsByFunctionIdAsDTO(int id) {
        LOGGER.info("Начинаем выбор точек по айди ф-ции и вернём их как лист dto, айди{}", id);
        List<PointsDTO> result = new ArrayList<>();

        String sql = loaderSQL.loadSQL("scripts\\points\\select_points_by_function_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            PointsDTO boof;

            while(resultSet.next()){
                boof = new PointsDTO(resultSet.getDouble(2),
                        resultSet.getDouble(3),
                        resultSet.getInt(4));

                result.add(boof);
            }

            if (result.isEmpty()){
                LOGGER.warn("В функции не были заданы точки");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("Точки корректно нашлись ");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе точек по айди функции {}", id);
            throw new RuntimeException(e);
        }
    }

    public List<Points> findPointsByFunctionIdSorted(int id) {
        LOGGER.info("Начинаем выбор сортированных точек по айди ф-ции и вернём их как лист, айди{}", id);
        List<Points> result = new ArrayList<>();

        String sql = loaderSQL.loadSQL("scripts\\points\\select_sorted_points_by_function_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            Points boof;

            while(resultSet.next()){
                boof = new Points(resultSet.getInt(1),
                        resultSet.getDouble(2),
                        resultSet.getDouble(3),
                        resultSet.getInt(4));

                result.add(boof);
            }

            if (result.isEmpty()){
                LOGGER.warn("Для функции не были заданы точки");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("Точки корректно нашлись и будет возвращены отсортированными списком строк");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе сортированных точек по айди ф-ции {}", id);
            throw new RuntimeException(e);
        }
    }

    public List<PointsDTO> findPointsByFunctionIdSortedAsDTO(int id) {
        LOGGER.info("Начинаем выбор сортированных точек по айди ф-ции и вернём их как лист dto, айди{}", id);
        List<PointsDTO> result = new ArrayList<>();

        String sql = loaderSQL.loadSQL("scripts\\points\\select_sorted_points_by_function_id.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            var resultSet = statement.executeQuery();

            PointsDTO boof;

            while(resultSet.next()){
                boof = new PointsDTO(resultSet.getDouble(2),
                        resultSet.getDouble(3),
                        resultSet.getInt(4));

                result.add(boof);
            }

            if (result.isEmpty()){
                LOGGER.warn("Для ф-ции не были заданы точки");
                throw new DataDoesNotExistException();
            }

            LOGGER.info("Точки корректно нашлись и будет возвращены отсортированными списком dto строк");
            return result;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при выборе сортированных точек по айди функции {}", id);
            throw new RuntimeException(e);
        }
    }

    public void updateXValueByFunctionIdAndOldX(Double x_value_old, int id, Double x_value_new){
        LOGGER.info("Обновить x для точки с айди {}", id);
        String sql = loaderSQL.loadSQL("scripts\\points\\x_value_update_by_function_id_and_old_x_value.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setDouble(1, x_value_new);
            statement.setInt(2, id);
            statement.setDouble(3, x_value_old);

            statement.executeUpdate();
            LOGGER.info("Обновление для X прошло успешно");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении значения X");
            throw new RuntimeException(e);
        }
    }

    public void updateYValueByFunctionIdAndOldY(Double y_value_old, int id, Double y_value_new){
        LOGGER.info("Начинаем обновление Y для точки с айди {}", id);
        String sql = loaderSQL.loadSQL("scripts\\points\\y_value_update_by_function_id_and_old_y_value.sql");

        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){

            statement.setDouble(1, y_value_new);
            statement.setInt(2, id);
            statement.setDouble(3, y_value_old);

            statement.executeUpdate();
            LOGGER.info("Обновление прошло успешно");
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при обновлении Y");
            throw new RuntimeException(e);
        }
    }

    public void deletePointsByFunctionId(int functionId){
        LOGGER.info("Начинаем удаление точки с айди функции {}", functionId);
        String sql = loaderSQL.loadSQL("scripts\\points\\delete_point_by_id.sql");
        try (var connection = connectionManager.open(); var statement = connection.prepareStatement(sql)){
            statement.setInt(1, functionId);
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

    public void createPoint(Double x_value, Double y_value, int id){
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

    public void addManyPoints(List<Point> points, int function_id) {
        LOGGER.info("Добавляем в таблицу {} точек", points.size());
        String sql = loaderSQL.loadSQL("scripts\\points\\insert_point.sql");

        try (var connection = connectionManager.open();
             var statement = connection.prepareStatement(sql)) {

            // Отключаем автокоммит для большей производительности
            connection.setAutoCommit(false);

            for (ru.ssau.tk._repfor2lab_._OOP_.functions.Point point : points) {
                statement.setDouble(1, point.x);
                statement.setDouble(2, point.y);
                statement.setLong(3, function_id);
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
