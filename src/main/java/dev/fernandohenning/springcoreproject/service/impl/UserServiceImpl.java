package dev.fernandohenning.springcoreproject.service.impl;

import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.exception.EmailAlreadyExistsException;
import dev.fernandohenning.springcoreproject.exception.PasswordDontMatchException;
import dev.fernandohenning.springcoreproject.exception.UserNotFoundException;
import dev.fernandohenning.springcoreproject.model.UserDetailsImpl;
import dev.fernandohenning.springcoreproject.repository.UserRepository;
import dev.fernandohenning.springcoreproject.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return new UserDetailsImpl(user);
    }

    public boolean emailExists(String email){
        return userRepository.existsByEmail(email);
    }

    public User findUserByEmail(String email){
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user found with email: " + email));
    }

    public User saveUser(User user){
        String email = user.getEmail();
        if(emailExists(email)) throw new EmailAlreadyExistsException();

        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public void updatePassword(String email, String password, String confirmPassword) {
        if (!emailExists(email)) throw new UserNotFoundException("No user found with email:" + email);

        User user = findUserByEmail(email);

        if (password.equals(confirmPassword)) {
            user.setPassword(passwordEncoder.encode(confirmPassword));
            userRepository.save(user);
        } else {
            throw new PasswordDontMatchException();
        }
    }

    public User validateCredentials(String email, String password) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new BadCredentialsException("Invalid credentials")
                );

        // check if the password matches
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new BadCredentialsException("Invalid credentials");

        return user;
    }

    public void enableUser(String email) {
        // get the user by email
        User user = findUserByEmail(email);
        // enable the user
        user.setEnabled(true);
        // save the user
        userRepository.save(user);
    }
}
