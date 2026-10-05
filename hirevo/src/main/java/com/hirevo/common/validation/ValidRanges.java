// src/main/java/com/hirevo/common/validation/ValidRanges.java
package com.hirevo.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)                          // this label goes on a whole class/record
@Retention(RetentionPolicy.RUNTIME)                // keep it while the app runs, so it can be checked
@Constraint(validatedBy = RangesValidator.class)   // the class that does the actual checking
public @interface ValidRanges {
    // the message the client sees when the rule is broken
    String message() default "min values must not exceed max values (salary, experience)";
    Class<?>[] groups() default {};                // required boilerplate for every rule label
    Class<? extends Payload>[] payload() default {};  // required boilerplate for every rule label
}