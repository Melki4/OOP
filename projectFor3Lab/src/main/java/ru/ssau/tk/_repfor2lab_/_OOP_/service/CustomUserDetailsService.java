package ru.ssau.tk._repfor2lab_._OOP_.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.UsersRepositories;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepositories usersRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
        return new CustomUserDetails(user);
    }
}