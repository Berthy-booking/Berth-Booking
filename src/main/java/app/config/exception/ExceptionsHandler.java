package app.config.exception;

import app.config.exception.impl.AccessException;
import app.config.exception.impl.NotFoundException;
import app.config.exception.impl.ServiceException;
import app.config.exception.impl.UnauthorizedException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@Slf4j
@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleUnexpectedException(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return createResponse(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServiceException.class)
    public final ResponseEntity<ErrorDetails> handleServiceException(ServiceException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return createResponse(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<ErrorDetails> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return createResponse(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessException.class)
    public final ResponseEntity<ErrorDetails> handleAccessException(AccessException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return createResponse(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleNotFoundException(NotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return createResponse(errorDetails, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorDetails> createResponse(ErrorDetails errorDetails, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<>(errorDetails, headers, status);
    }

    @Getter
    private static class ErrorDetails {

        private Date dateTime;
        private String message;
        private String details;

        ErrorDetails(String message, String details) {
            this.dateTime = new Date();
            this.message = message;
            this.details = details;
        }
    }
}