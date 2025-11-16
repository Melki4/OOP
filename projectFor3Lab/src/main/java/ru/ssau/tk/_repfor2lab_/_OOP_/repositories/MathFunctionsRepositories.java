package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;

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
    void deleteByNameOfFunction(String nameOfFunction);
}
