package com.tus.musicapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.logging.LogManager;

import javax.annotation.PostConstruct;

import org.slf4j.bridge.SLF4JBridgeHandler;

@SpringBootApplication
@ComponentScan(basePackages = "com.tus.musicapp") 
public class MusicApplication {

    @Autowired
    private Environment environment;
    
    public static void main(String[] args) {
    	// remove HTTP wire logging
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        
        // run application
        SpringApplication.run(MusicApplication.class, args);
        System.out.println("Logger: " + java.util.logging.Logger.getLogger("org.apache.hc.client5.http.wire").getLevel());

    }
    
    @PostConstruct
    public void logActiveProfiles() {
        System.out.println("✅ ACTIVE PROFILES: " + Arrays.toString(environment.getActiveProfiles()));
    }

}


