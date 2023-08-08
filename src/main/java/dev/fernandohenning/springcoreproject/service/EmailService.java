package dev.fernandohenning.springcoreproject.service;

public interface EmailService {
    void sendEmailWithTemplate(String email, String firstName, String subject, String url, String template);
    void sendActivationLink(String email, String firstName, String activationLink);
    void sendResetPasswordRequest(String email, String firstName, String resetPasswordLink);
}
