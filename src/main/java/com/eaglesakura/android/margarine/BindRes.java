package com.eaglesakura.android.margarine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * View Injection
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BindRes {
    /**
     * R.id.**
     */
    int value() default 0;

    /**
     * R.id."resName"
     */
    String resName() default "";
}
