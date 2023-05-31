package com.carrental.service;

import com.carrental.aspects.LogEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    @LogEmail
    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage mail = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
        helper.setTo(to);
//        helper.setReplyTo("car.rental.buissness@interia.pl");
//        helper.setFrom("car.rental.buissness@interia.pl");
        helper.setReplyTo("car.rental.buissness.3@interia.pl");
        helper.setFrom("car.rental.buissness.3@interia.pl");
        helper.setSubject(subject);
        helper.setText(content, true);
        try {
            emailSender.send(mail);
        }catch (Exception e){
            System.out.println("Found exception!: " + e.toString());
        }
    }
}
