package com.glmall.utils.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {SortConstraintValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SortValue {
    String message() default "{com.glmall.utils.validate.SortValue.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String [] value() default {};

}
