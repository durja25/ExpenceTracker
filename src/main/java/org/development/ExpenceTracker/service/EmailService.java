package org.development.ExpenceTracker.service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

@Service
@Slf4j
//@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.properties.mail.smtp.from}") // this is not fill at constructor initialization its populate at init method || your can use constructor injection
    public String fromEmail;

    @Value("${spring.mail.api.key}")
    public String apiKey;

    private WebClient webClient;

    private final WebClient.Builder builder;

    public EmailService(WebClient.Builder builder) {
        this.builder = builder;
    }

    // init method with @PostConstruct annotation
    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Brevo API key is missing");
        }

        this.webClient = builder
            .baseUrl("https://api.brevo.com/v3")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("api-key", apiKey)
            .build();

        log.info("EmailService initialized with fromEmail = {}", fromEmail);
    }

    public void sendEmail(String to, String subject, String html) {

        webClient.post()
                 .uri("/smtp/email")
                 .bodyValue(Map.of(
                     "sender", Map.of(
                         "email", fromEmail
                     ),
                     "to", List.of(
                         Map.of("email", to)
                     ),
                     "subject", subject,
                     "htmlContent", html
                 ))
                 .retrieve()
                 .bodyToMono(String.class)
                 .block();

        log.info("Email sent to {}", to);
    }

    // Not usable on cloud servers for Some SMTP is blocked
/*
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
*/

// Constructor Injection example
    /*@Service
@Slf4j
public class EmailService {

    private final String fromEmail;
    private final String apiKey;
    private final WebClient webClient;

    public EmailService(
        WebClient.Builder builder,
        @Value("${spring.mail.properties.mail.smtp.from}") String fromEmail,
        @Value("${spring.mail.api.key}") String apiKey
    ) {
        this.fromEmail = fromEmail;
        this.apiKey = apiKey;

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Brevo API key is missing");
        }

        this.webClient = builder
            .baseUrl("https://api.brevo.com/v3")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("api-key", apiKey)
            .build();

        log.info("EmailService initialized with fromEmail = {}", fromEmail);
    }
}
*/


}
