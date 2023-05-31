package com.carrental.aspects;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public aspect OldLoggerAspect pertypewithin(@CustomLogger *) {
    private Logger logger;
    private FileHandler fileHandler;

    after() returning: staticinitialization(*) {
        logger = Logger.getLogger(thisJoinPointStaticPart.getSignature().getDeclaringType().getName());
        try {
            fileHandler = new FileHandler("logs\\customLogs.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.FINEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    pointcut finest() : execution(@LogFinest * *.*(..));

    before() : finest() {
        logger.finest(() -> {
                StringBuilder sb = new StringBuilder();
        sb.append(thisJoinPointStaticPart.getSignature()).append(", args:");
        for (Object o:thisJoinPoint.getArgs()) {
            sb.append(o).append(" ");
        }
        return "Method called: " + sb.toString();
        });
    }

    after() throwing : finest() {
        logger.finest(() -> {
                StringBuilder sb = new StringBuilder();
        sb.append(thisJoinPointStaticPart.getSignature()).append(", args:");
        for (Object o:thisJoinPoint.getArgs()) {
            sb.append(o).append(" ");
        }
        return "Exception caught for method: " + sb.toString();
        });
    }

    pointcut normal() : !(execution(@LogFinest * *.*(..)));

    before() : normal() {
        logger.info(() -> {
                StringBuilder sb = new StringBuilder();
        sb.append(thisJoinPointStaticPart.getSignature()).append(", args:");
        for (Object o:thisJoinPoint.getArgs()) {
            sb.append(o).append(" ");
        }
        return "Method called: " + sb.toString();
        });
    }

    after() throwing : normal() {
        logger.info(() -> {
                StringBuilder sb = new StringBuilder();
        sb.append(thisJoinPointStaticPart.getSignature()).append(", args:");
        for (Object o:thisJoinPoint.getArgs()) {
            sb.append(o).append(" ");
        }
        return "Exception caught for method: " + sb.toString();
        });
    }
}