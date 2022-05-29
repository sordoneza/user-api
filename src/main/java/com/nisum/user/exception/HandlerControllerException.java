package com.nisum.user.exception;

import io.jsonwebtoken.JwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@ControllerAdvice
public class HandlerControllerException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(
            EntityNotFoundException ex, WebRequest request) {

        ErrorWrapper errorWrapper = new ErrorWrapper(ex.getMessage());

        return handleExceptionInternal(ex, errorWrapper,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(
            UsernameNotFoundException ex) {

        ErrorWrapper errorWrapper = new ErrorWrapper(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorWrapper);

    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleUnauthenticated(
            AuthenticationException ex, WebRequest request) {

        ErrorWrapper errorWrapper = new ErrorWrapper("Wrong user credentials");

        return handleExceptionInternal(ex, errorWrapper,
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwtException(
            JwtException ex) {

        ErrorWrapper errorWrapper = new ErrorWrapper();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorWrapper);
    }


    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> handleApiException(ApiException e, WebRequest request) {
        ErrorWrapper errorWrapper = new ErrorWrapper(e.getMessage());

        return handleExceptionInternal(e, errorWrapper,
                new HttpHeaders(), e.getStatus(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {

        ErrorWrapper errorWrapper = new ErrorWrapper(e.getMessage());

        return handleExceptionInternal(e, errorWrapper,
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid
            (MethodArgumentNotValidException ex,
             HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = extractMessage(ex);
        ErrorWrapper errorWrapper = new ErrorWrapper(message);

        return handleExceptionInternal(ex, errorWrapper,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private String extractMessage(MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                            String field = ((FieldError) error).getField();
                            String message = error.getDefaultMessage();
                            return String.format("%s: %s", field, message);
                        }
                ).collect(Collectors.joining(", "));
    }
}
