package org.wolt.woltproject.exceptions;

public class AlreadyRated extends RuntimeException{
    public AlreadyRated(String message) {
        super(message);
    }
}
