package org.wolt.woltproject.config.myannotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.wolt.woltproject.exceptions.Under18Exception;

import java.time.LocalDate;
import java.time.Period;

public class Under18Validator implements ConstraintValidator<Under18, LocalDate> {

    @Override
    public void initialize(Under18 constraintAnnotation) {

    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null){
            return true;
        }
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(localDate,currentDate);
        if (period.getYears() < 18){
            throw new Under18Exception("You Have to be over 18");
        }
        return true;
    }
}
