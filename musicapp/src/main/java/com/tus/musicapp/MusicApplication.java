package com.tus.musicapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tus.musicapp") 
public class MusicApplication {
    
    public static void main(String[] args) {
        
        // run application
        SpringApplication.run(MusicApplication.class, args);

    }

}


