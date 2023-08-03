package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.entity.User;

public interface TokenService {
    void revokeToken(String token);
    boolean isTokenValid(String token);
    void saveUserToken(User user, String encodedToken);
    void revokeAllUserTokens(User user);
}
