package com.tus.musicapp.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {
    private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

    @PostConstruct
    public void init() {
        logger.info("✅ Using correct application.properties file");
    }
}

