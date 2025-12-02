package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepositories extends JpaRepository<Users, Long> {
    Optional<Users> findByLogin(String login);
    List<Users> findAll();
    boolean existsByLogin(String login);
    boolean existsByUserID(Long userID);
    @Transactional
    void deleteByLogin(String login);

    //для поиска с сортировкой
    List<Users> findByLoginContainingIgnoreCase(String login, Sort sort);
    List<Users> findAllByOrderByLoginAsc();

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM MathFunctions f WHERE f.mathFunctionsID = :functionId AND f.users.userID = :userId")
    boolean isFunctionOwnedByUser(@Param("functionId") Long functionId, @Param("userId") Long userId);
}