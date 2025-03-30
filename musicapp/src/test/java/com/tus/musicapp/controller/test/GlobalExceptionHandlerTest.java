package com.tus.musicapp.controller.test;

import com.tus.musicapp.controller.GlobalExceptionHandler;
import com.tus.musicapp.exceptions.UsernameAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUsernameNotFoundException_ShouldReturnNotFound() {
        UsernameNotFoundException ex = new UsernameNotFoundException("User not found");
        ResponseEntity<String> response = handler.handleUsernameNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("X User not found", response.getBody());
    }

    @Test
    void handleUsernameAlreadyExistsException_ShouldReturnBadRequest() {
        UsernameAlreadyExistsException ex = new UsernameAlreadyExistsException("Username already taken");
        ResponseEntity<String> response = handler.handleUsernameAlreadyExistsException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already taken", response.getBody());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange mock FieldError
        FieldError fieldError = new FieldError("object", "username", "must not be blank");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Wrap in MethodArgumentNotValidException
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleValidationException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("username"));
        assertEquals("must not be blank", response.getBody().get("username"));
    }

    @Test
    void handleDatabaseException_ShouldReturnConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate entry");
        ResponseEntity<String> response = handler.handleDatabaseException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("❌ Data integrity violation: Duplicate or invalid entry.", response.getBody());
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected failure");
        ResponseEntity<Map<String, String>> response = handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("Unexpected failure", response.getBody().get("message"));
    }
}
