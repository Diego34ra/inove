package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.domain.service.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailServiceImpl implements EmailService {

    private final Resend resend;
    private final String fromEmail;
    private final String replyTo;

    public ResendEmailServiceImpl(
            @Value("${resend.apiKey}") String apiKey,
            @Value("${resend.from}") String fromEmail,
            @Value("${resend.replyTo:}") String replyTo
    ) {
        this.resend = new Resend(apiKey);
        this.fromEmail = fromEmail;
        this.replyTo = replyTo;
    }

    @Override
    public void send(String toEmail, String subject, String textBody) {
        CreateEmailOptions.Builder builder = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(toEmail)
                .subject(subject)
                .text(textBody);

        if (!replyTo.isEmpty()) builder.replyTo(replyTo);

        try {
            resend.emails().send(builder.build());
        } catch (ResendException e) {
            throw new RuntimeException("Falha ao enviar e-mail via Resend", e);
        }
    }

    @Override
    public void sendHtml(String toEmail, String subject, String htmlBody) {
        String textBody = htmlBody
                .replaceAll("<style[^>]*>.*?</style>", "")
                .replaceAll("<[^>]+>", "")
                .replaceAll("\\s+", " ")
                .trim();

        CreateEmailOptions.Builder builder = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(toEmail)
                .subject(subject)
                .html(htmlBody)
                .text(textBody);

        if (!replyTo.isEmpty()) builder.replyTo(replyTo);

        try {
            resend.emails().send(builder.build());
        } catch (ResendException e) {
            throw new RuntimeException("Falha ao enviar e-mail (HTML) via Resend", e);
        }
    }
}
