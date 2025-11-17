package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Points;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointsRepositories extends JpaRepository<Points, Long> {
    List<Points> findByMathFunctionsMathFunctionsID(Long functionID);
    boolean existsByMathFunctionsMathFunctionsID(Long functionID);
    List<Points> findByxValue(Double xValue);
    boolean existsByxValue(Double xValue);
    List<Points> findByyValue(Double xValue);
    boolean existsByyValue(Double xValue);
    List<Points> findByxValueBetween(Double minX, Double maxX);
    List<Points> findByyValueBetween(Double minY, Double maxY);
    @Transactional
    void deleteByMathFunctionsMathFunctionsID(Long functionID);

    //для поиска с сортировкой
    List<Points> findByMathFunctionsMathFunctionsID(Long functionID, Sort sort);
    List<Points> findByxValueBetween(Double minX, Double maxX, Sort sort);
    List<Points> findByyValueBetween(Double minY, Double maxY, Sort sort);
}
