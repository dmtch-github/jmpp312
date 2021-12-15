package jm.spring.bootstrap.entity.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SizeNotBlankValidator implements ConstraintValidator<SizeNotBlank, String> {

    private int min;
    private int max;

    @Override
    public void initialize(SizeNotBlank sizeNotBlank) {
        min = sizeNotBlank.min();
        max = sizeNotBlank.max();
    }

    @Override
    public boolean isValid(String enteredValue, ConstraintValidatorContext constraintValidatorContext) {
        if (enteredValue != null) {
            int size = enteredValue.trim().length();
            return (size != 0 && size >= min && size <= max);
        }
        return false;
    }

}
