package ir.maktab.forthphase.controller;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import ir.maktab.forthphase.config.MessageSourceConfiguration;
import ir.maktab.forthphase.exceptions.*;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.nio.file.InvalidPathException;

@ControllerAdvice
public class CentralExceptionHandler {

    final MessageSourceConfiguration messageSource;

    public CentralExceptionHandler(MessageSourceConfiguration messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<?> handleDuplicateEmailException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.duplicate_Email_added"));
    }

    @ExceptionHandler(NoSuchUserFound.class)
    public ResponseEntity<?> handleNoSuchUserFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                messageSource.getMessage("errors.message.no_such_user_found"));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<?> handleInvalidEmailException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.invalid_email"));
    }

    @ExceptionHandler(InvalidNationalCodeException.class)
    public ResponseEntity<?> handleInvalidNationalCodeException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.invalid_national_code"));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> handleInvalidPasswordException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.invalid_password"));
    }

    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<?> handleInvalidPriceException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.invalid_price"));
    }

    @ExceptionHandler(InvalidRequiredDateException.class)
    public ResponseEntity<?> handleInvalidRequiredDateException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.invalid_required_date"));
    }

    @ExceptionHandler(OrderCodeNotFoundException.class)
    public ResponseEntity<?> handleOrderCodeNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                messageSource.getMessage("errors.message.order_code_not_found"));
    }

    @ExceptionHandler(ServiceNameNotFoundException.class)
    public ResponseEntity<?> handleServiceNameNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                messageSource.getMessage("errors.message.service_name_not_found"));
    }

    @ExceptionHandler(SubServiceNameNotFoundException.class)
    public ResponseEntity<?> handleSubServiceNameNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                messageSource.getMessage("errors.message.sub_service_name_not_found"));
    }

    @ExceptionHandler(UnequalPasswordsException.class)
    public ResponseEntity<?> handleUnequalPasswordsException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(messageSource
                        .getMessage("errors.message.unequal_passwords"));
    }

    @ExceptionHandler(DeActiveAccountException.class)
    public ResponseEntity<?> handleDeActiveAccountException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageSource
                        .getMessage("errors.message.de_active_account_exception"));
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    private ResponseEntity<?> handleUnexpectedTypeException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageSource
                        .getMessage("errors.message.unexpected_type_exception"));
    }

    @ExceptionHandler(MismatchedInputException.class)
    ResponseEntity<?> handleException(MismatchedInputException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<?> handleException(HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<?> handleException(HttpMediaTypeNotSupportedException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> handleException(HttpMessageNotReadableException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    ResponseEntity<?> handleException(NoHandlerFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    ResponseEntity<?> handleException(ServletRequestBindingException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    private ResponseEntity<?> internalServerError(HttpServerErrorException.InternalServerError exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    private ResponseEntity<?> handleNullPointerException(NullPointerException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    private ResponseEntity<?> handleIOException(IOException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(InvalidPathException.class)
    private ResponseEntity<?> handleInvalidPathException(InvalidPathException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<?> handleInvalidPathException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageSource
                        .getMessage("errors.message.unexpected_type_exception"));
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    private ResponseEntity<?> handleInvalidOrderStatusException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageSource
                        .getMessage("errors.message.invalid_order_status"));
    }

    @ExceptionHandler(InvalidTimeException.class)
    private ResponseEntity<?> handleInvalidTimeException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageSource
                        .getMessage("errors.message.invalid_time_format"));
    }

    @ExceptionHandler(IllegalStateException.class)
    private ResponseEntity<?> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
