package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._repfor2lab_._OOP_.entities.SimpleFunctions;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimpleFunctionsRepositories extends JpaRepository<SimpleFunctions, String> {
    Optional<SimpleFunctions> findByLocalName(String localName);
    boolean existsByLocalName(String localName);
    @Transactional
    void deleteByLocalName(String localName);

    //для поиска с сортировкой
    List<SimpleFunctions> findByLocalNameContainingIgnoreCase(String localName, Sort sort);
}
