package com.tus.musicapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ContextLoadsTest {

    @Test
    void contextLoads() {
        // This starts the Spring Boot application on port 9091
        // and keeps it up for the duration of the test run
    }
}
