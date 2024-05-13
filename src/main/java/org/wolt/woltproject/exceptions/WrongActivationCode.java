package org.wolt.woltproject.exceptions;

public class WrongActivationCode extends RuntimeException{
    public WrongActivationCode(String message) {
        super(message);
    }
}
