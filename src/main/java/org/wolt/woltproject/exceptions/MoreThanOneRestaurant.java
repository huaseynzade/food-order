package org.wolt.woltproject.exceptions;

public class MoreThanOneRestaurant extends RuntimeException{
    public MoreThanOneRestaurant(String message) {
        super(message);
    }
}
