package com.eaglesakura.android.margarine;

import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injection
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BindString {
    /**
     * R.string.**
     */
    @StringRes int value() default 0;

    /**
     * R.string."resName"
     */
    String resName() default "";

    /**
     * BinderClass
     */
    Class<? extends FieldBinder> binder() default FieldResourceBinder.FieldBinderString.class;
}
