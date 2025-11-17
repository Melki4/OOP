package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface MathFunctionsRepositories extends JpaRepository<MathFunctions, Long> {
    Optional<MathFunctions> findByMathFunctionsID(Long mathFunctionsID);
    Optional<MathFunctions> findByNameOfFunction(String nameOfFunction);
    boolean existsByNameOfFunction(String nameOfFunction);
    List<MathFunctions> findByUsersUserID(Long userID);
    boolean existsByUsersUserID(Long userID);
    List<MathFunctions> findByLeftBoarderBetween(Double minLeftBoarder, Double maxLeftBoarder);
    List<MathFunctions> findByRightBoarderBetween(Double minRightBoarder, Double maxRightBoarder);
    List<MathFunctions> findByAmountOfDotsBetween(Long minDots, Long maxDots);
    List<MathFunctions> findByLeftBoarderGreaterThanEqualAndRightBoarderLessThanEqual(Double minLeftBoarder, Double maxRightBoarder);
    @Transactional
    void deleteByNameOfFunction(String nameOfFunction);

    //для поиска с сортировкой
    List<MathFunctions> findByRightBoarderBetween(Double minRightBoarder, Double maxRightBoarder, Sort sort);
    List<MathFunctions> findByLeftBoarderBetween(Double minLeftBoarder, Double maxLeftBoarder, Sort sort);
    List<MathFunctions> findByUsersUserID(Long userID, Sort sort);
    List<MathFunctions> findByNameOfFunctionContainingIgnoreCase(String name, Sort sort);
}
