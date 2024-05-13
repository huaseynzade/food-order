package org.wolt.woltproject.controllers;

import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.exceptions.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handle(NotFoundException exception){
        log.error("Error -> {} " , exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(UnexpectedTypeException exception){
        log.error("Error -> {}",exception.getMessage());
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(PSQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handle(PSQLException exception){
        log.error("error -> {}",exception.getMessage());
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
}
