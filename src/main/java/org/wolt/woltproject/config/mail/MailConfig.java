package org.wolt.woltproject.config.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class MailConfig{

    private final JavaMailSender mailSender;
    private final TemplateEngine engine;

    public void sendEmail(String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("rebbin-no-reply@example.com", "Rebbin Huseynzade");
        helper.setTo(email);

        helper.setSubject(subject);

        Context context = new Context();
        context.setVariable("content",content);
        String processedString = engine.process("template",context);



        helper.setText(processedString, true);

        mailSender.send(message);
    }

}
