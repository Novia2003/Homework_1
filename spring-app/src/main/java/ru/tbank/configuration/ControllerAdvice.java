package ru.tbank.configuration;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tbank.dto.http.ErrorMessageResponse;
import ru.tbank.exception.*;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessageResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorMessageResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                getMessageWithoutMethodName(e.getMessage())
                        )
                );
    }

    private String getMessageWithoutMethodName(String message) {
        int index = message.indexOf(":");
        return message.substring(index + 2);
    }

    @ExceptionHandler(
            {
                    NonexistentCurrencyException.class,
                    RelatedEntityNotFoundException.class,
                    VerificationCodeException.class,
                    UserAlreadyExistsException.class
            }
    )
    public ResponseEntity<ErrorMessageResponse> handleNonexistentCurrencyException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(
            {
                    CurrencyRateNotFoundException.class,
                    EntityNotFoundException.class,
                    NoSuchElementException.class,
                    UsernameNotFoundException.class
            }
    )
    public ResponseEntity<ErrorMessageResponse> handleCurrencyRateNotFoundException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorMessageResponse> handleServiceUnavailableException(ServiceUnavailableException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Retry-After", "3600")
                .body(new ErrorMessageResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage()));
    }
}
