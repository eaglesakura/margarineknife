package com.eaglesakura.android.margarine;

import android.support.annotation.ArrayRes;
import android.support.annotation.Keep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injection
 */
@Keep
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BindStringArray {
    /**
     * R.string.**
     */
    @Keep
    @ArrayRes int value() default 0;

    /**
     * R.string."resName"
     */
    @Keep
    String resName() default "";

    /**
     * BinderClass
     */
    @Keep
    Class<? extends FieldBinder> binder() default FieldResourceBinder.FieldBinderStringArray.class;
}
