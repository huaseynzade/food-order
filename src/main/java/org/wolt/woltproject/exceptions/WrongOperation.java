package org.wolt.woltproject.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class WrongOperation extends RuntimeException{
    public WrongOperation(String message) {
        super(message);
    }
}
