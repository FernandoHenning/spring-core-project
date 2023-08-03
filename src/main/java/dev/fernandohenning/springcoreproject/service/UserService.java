package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.exception.EmailAlreadyExistsException;
import dev.fernandohenning.springcoreproject.exception.PasswordDontMatchException;
import dev.fernandohenning.springcoreproject.exception.UserNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    boolean emailExists(String email) throws UsernameNotFoundException;
    User findUserByEmail(String email) throws UserNotFoundException;
    User saveUser(User user) throws EmailAlreadyExistsException;
    void updatePassword(String email, String password, String confirmPassword)
            throws UserNotFoundException, PasswordDontMatchException;
    User validateCredentials(String email, String password) throws BadCredentialsException;
    void enableUser(String email);
}
