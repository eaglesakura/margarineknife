package com.eaglesakura.android.margarine;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * View Injection
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Bind {
    /**
     * R.id.**
     */
    @IdRes int value() default 0;

    /**
     * R.id."resName"
     */
    String resName() default "";

    /**
     * BinderClass
     */
    Class binder() default FieldResourceBinder.FieldBinderView.class;

    /**
     * Null許容する場合true
     */
    boolean nullable() default false;
}
