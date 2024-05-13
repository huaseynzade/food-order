package org.wolt.woltproject.exceptions;

public class NotEnoughBalance extends RuntimeException{
    public NotEnoughBalance(String message) {
        super(message);
    }
}
