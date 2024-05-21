package org.wolt.woltproject.models;

public class PasswordsDoesntMatch extends RuntimeException{
    public PasswordsDoesntMatch(String message) {
        super(message);
    }
}
