package br.edu.ifgoiano.inove.domain.service;

public interface EmailService {

    void send(String toEmail, String subject, String textBody);
    default void sendHtml(String toEmail, String subject, String htmlBody) {
        send(toEmail, subject, htmlBody.replaceAll("<[^>]+>", ""));
    }

}
