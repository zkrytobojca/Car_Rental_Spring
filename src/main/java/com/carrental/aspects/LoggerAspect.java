package com.carrental.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Aspect
@Configuration
public class LoggerAspect {

    private Logger logger;
    private FileHandler fileHandler;

    public LoggerAspect() {
        logger = Logger.getLogger("LoggerAspect");
        try {
            fileHandler = new FileHandler("/usr/src/mymaven/logs/customLogs.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.FINEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Pointcut("within(@com.carrental.aspects.CustomLogger *)")
    public void inClassWithCustomLogger() {}

    @Pointcut("execution(@com.carrental.aspects.LogFinest * *(..))")
    public void inLogFinestMethod() {}

    @Pointcut("execution(@com.carrental.aspects.LogIgnore * *(..))")
    public void inLogIgnoreMethod() {}

    @Pointcut("inClassWithCustomLogger() && inLogFinestMethod() && !(inLogIgnoreMethod())")
    public void inFinestLogMode() {}

    @Pointcut("inClassWithCustomLogger() && !(inLogFinestMethod()) && !(inLogIgnoreMethod())")
    public void inNormalLogMode() {}

    @Before("com.carrental.aspects.LoggerAspect.inFinestLogMode()")
    public void LogAnnotationFinest(JoinPoint thisJoinPoint) {
        logger.finest(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append(thisJoinPoint.getSignature()).append(", args:");
            for (Object o:thisJoinPoint.getArgs()) {
                sb.append(o).append(" ");
            }
            return "Method called: " + sb.toString();
        });
    }

    @AfterThrowing("com.carrental.aspects.LoggerAspect.inFinestLogMode()")
    public void LogAnnotationFinestAfterThrowing(JoinPoint thisJoinPoint) {
        logger.finest(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append(thisJoinPoint.getSignature()).append(", args:");
            for (Object o:thisJoinPoint.getArgs()) {
                sb.append(o).append(" ");
            }
            return "Exception caught for method: " + sb.toString();
        });
    }

    @Before("com.carrental.aspects.LoggerAspect.inNormalLogMode()")
    public void LogAnnotationNormal(JoinPoint thisJoinPoint) {
        logger.info(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append(thisJoinPoint.getSignature()).append(", args:");
            for (Object o:thisJoinPoint.getArgs()) {
                sb.append(o).append(" ");
            }
            return "Method called: " + sb.toString();
        });
    }

    @AfterThrowing("com.carrental.aspects.LoggerAspect.inNormalLogMode()")
    public void LogAnnotationNormalAfterThrowing(JoinPoint thisJoinPoint) {
        logger.info(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append(thisJoinPoint.getSignature()).append(", args:");
            for (Object o:thisJoinPoint.getArgs()) {
                sb.append(o).append(" ");
            }
            return "Exception caught for method: " + sb.toString();
        });
    }
}

