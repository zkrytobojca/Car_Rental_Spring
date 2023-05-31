package com.carrental.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Aspect
@Configuration
public class EmailLoggerAspect {

    private Logger logger;
    private FileHandler fileHandler;

    public EmailLoggerAspect() {
        logger = Logger.getLogger("EmailLoggerAspect");
        try {
            fileHandler = new FileHandler("/usr/src/mymaven/logs/emailLogs.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.FINEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Around("@annotation(com.carrental.aspects.LogEmail)")
    public Object LogEmailSend(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = null;
        String subject = null;
        if(joinPoint.getArgs().length > 0) {
            if(joinPoint.getArgs()[0].getClass() == String.class) {
                email = (String)joinPoint.getArgs()[0];
                subject = (String)joinPoint.getArgs()[1];
            }
        }

        Object proceed = joinPoint.proceed();

        logger.finest("Email send to address: " + email + " with subject: " + subject);

        System.out.println("!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-");
        System.out.println("Email send to address: " + email + " with subject: " + subject);
        System.out.println("!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-");

        return proceed;
    }
}
