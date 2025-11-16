package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;

import java.util.Optional;

@Repository
public interface UsersRepositories extends JpaRepository<Users, Long> {
    Optional<Users> findByLogin(String login);
    boolean existsByLogin(String login);
}
