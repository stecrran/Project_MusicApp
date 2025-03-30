package com.tus.musicapp.exceptions.test;

import org.junit.jupiter.api.Test;

import com.tus.musicapp.exceptions.UsernameAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;

class UsernameAlreadyExistsExceptionTest {

    @Test
    void constructor_ShouldSetMessageCorrectly() {
        // Arrange
        String expectedMessage = "Username 'testuser' already exists.";

        // Act
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}

