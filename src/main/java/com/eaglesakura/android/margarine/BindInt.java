package com.eaglesakura.android.margarine;

import android.support.annotation.IntegerRes;
import android.support.annotation.Keep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Keep
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BindInt {
    /**
     * R.integer.**
     */
    @Keep
    @IntegerRes int value() default 0;

    /**
     * R.integer."resName"
     */
    @Keep
    String resName() default "";

    /**
     * BinderClass
     */
    @Keep
    Class<? extends FieldBinder> binder() default FieldResourceBinder.FieldBinderInteger.class;
}
