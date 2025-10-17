package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.exceptions.ResourceBadRequestException;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.repository.UserRepository;
import br.edu.ifgoiano.inove.domain.service.implementation.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final UserRepository userRepository;
    private final EmailServiceImpl emailServiceImpl;

    private static final Duration CODE_TTL = Duration.ofMinutes(10);
    private static final int MAX_ATTEMPTS = 5;
    private static final String EMAIL_SUBJECT = "Código de recuperação de senha";

    private final Map<String, CodeRecord> codes = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public void requestRecoveryCode(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);

        if (user.isEmpty()) {
            System.out.println("Empty");
            return;
        }

        String code = generateSixDigitCode();

        codes.put(email.toLowerCase(), new CodeRecord(code, Instant.now().plus(CODE_TTL), 0));

        String emailBody = """
                Você solicitou a recuperação de senha.

                Seu código é: %s

                Esse código expira em %d minutos.
                Se você não solicitou, pode ignorar este e-mail.
                """.formatted(code, CODE_TTL.toMinutes());

        emailServiceImpl.sendConfirmationEmail(email, EMAIL_SUBJECT, emailBody);
    }

    public boolean validateCode(String email, String code) {
        CodeRecord record = codes.get(email.toLowerCase());
        if (record == null) return false;
        if (Instant.now().isAfter(record.expiresAt())) return false;
        return record.code().equals(code);
    }

    public boolean consumeCode(String email, String code) {
        String key = email.toLowerCase();
        CodeRecord record = codes.get(key);
        if (record == null) return false;

        if (Instant.now().isAfter(record.expiresAt())) {
            codes.remove(key);
            return false;
        }

        if (record.attempts() >= MAX_ATTEMPTS) {
            codes.remove(key);
            throw new ResourceBadRequestException("Número máximo de tentativas excedido.");
        }

        if (!record.code().equals(code)) {
            codes.put(key, record.withAttempts(record.attempts() + 1));
            return false;
        }

        codes.remove(key);
        return true;
    }

    private String generateSixDigitCode() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    private record CodeRecord(String code, Instant expiresAt, int attempts) {
        CodeRecord withAttempts(int newAttempts) {
            return new CodeRecord(this.code, this.expiresAt, newAttempts);
        }
    }
}
