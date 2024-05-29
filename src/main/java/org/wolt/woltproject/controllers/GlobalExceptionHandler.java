package org.wolt.woltproject.controllers;

import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.exceptions.*;
import org.wolt.woltproject.models.PasswordsDoesntMatch;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ExceptionDto handle(HttpRequestMethodNotSupportedException exception) {
        log.error("Method Not Allowed: {}", exception.getMessage());
        return new ExceptionDto("Method Not Allowed: " + exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(MethodArgumentNotValidException exception) {
        log.error("Method Argument Not Valid: {}", exception.getMessage());
        return new ExceptionDto("Method Argument Not Valid: " + exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(HttpMessageNotReadableException exception) {
        log.error("HTTP Message Not Readable: {}", exception.getMessage());
        return new ExceptionDto("HTTP Message Not Readable: " + exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handle(NotFoundException exception){
        log.error("Error -> {} " , exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }


    @ExceptionHandler(WrongOperation.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(WrongOperation exception){
        log.error("error -> {}",exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }


    @ExceptionHandler(Under18Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(Under18Exception exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(NotEnoughBalance.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(NotEnoughBalance exception){
        log.error("error -> {}",exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(CantRate.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(CantRate exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(AlreadyRated.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handle(AlreadyRated exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handle(UserAlreadyExistException exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(NotAccessibleMethod.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(NotAccessibleMethod exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(WrongActivationCode.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(WrongActivationCode exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(ActivationCodeJustSent.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ExceptionDto handle(ActivationCodeJustSent exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(ActivationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(ActivationException exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }


    @ExceptionHandler(CantDoReview.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(CantDoReview exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(KeysDoesntMatch.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(KeysDoesntMatch exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }


    @ExceptionHandler(PasswordsDoesntMatch.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(PasswordsDoesntMatch exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }


    @ExceptionHandler(VerifyYouOwner.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(VerifyYouOwner exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }


    @ExceptionHandler(PdfOnlyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(PdfOnlyException exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }



    @ExceptionHandler(MoreThanOneRestaurant.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(MoreThanOneRestaurant exception){
        log.error("error -> {}", exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }
}

