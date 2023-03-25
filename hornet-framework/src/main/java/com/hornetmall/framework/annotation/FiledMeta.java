package com.hornetmall.framework.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
@Inherited
@Documented
public @interface FiledMeta {
    boolean dto() default true;
    boolean view() default true;
    boolean create() default  true;
    boolean update() default  true;
    boolean patch() default true;
}
