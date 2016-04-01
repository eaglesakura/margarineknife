package com.eaglesakura.android.margarine;

import android.support.annotation.IntegerRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BindInt {
    /**
     * R.integer.**
     */
    @IntegerRes int value() default 0;

    /**
     * R.integer."resName"
     */
    String resName() default "";

    /**
     * BinderClass
     */
    Class<? extends FieldBinder> binder() default FieldResourceBinder.FieldBinderInteger.class;
}
