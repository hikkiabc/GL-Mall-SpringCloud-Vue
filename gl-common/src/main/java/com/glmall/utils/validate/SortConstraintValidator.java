package com.glmall.utils.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class SortConstraintValidator implements ConstraintValidator<SortValue, String> {
    Set valueSet = new HashSet<>();

    @Override
    public void initialize(SortValue constraintAnnotation) {
        String[] value = constraintAnnotation.value();
        for (String i : value) {
            valueSet.add(i);
        }
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        return valueSet.contains(s);
    }
}
