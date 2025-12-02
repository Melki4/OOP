package ru.ssau.tk._repfor2lab_._OOP_.service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.UsersRepositories;

@Service
public class AuthorizationService {

    private final UsersRepositories usersRepository;

    public AuthorizationService(UsersRepositories usersRepository) {
        this.usersRepository = usersRepository;
    }

    public boolean isOwnerOfFunction(Long functionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentLogin = auth.getName();

        return usersRepository.findByLogin(currentLogin)
                .map(user -> usersRepository.isFunctionOwnedByUser(functionId, user.getUserID()))
                .orElse(false);
    }
}
