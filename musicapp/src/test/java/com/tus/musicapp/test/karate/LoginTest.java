package com.tus.musicapp.test.karate;

import com.intuit.karate.junit5.Karate;

class LoginTest {
    
    @Karate.Test
    Karate testLogin() {
        return Karate.run("classpath:features/login.feature");
    }
}
