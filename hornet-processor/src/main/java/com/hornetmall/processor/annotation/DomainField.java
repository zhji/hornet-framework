package com.hornetmall.processor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(value={FIELD})
public @interface DomainField {
    boolean dto() default true;
    boolean view() default true;
    boolean create() default true;
    boolean update() default true;
    boolean patch() default true;
}
