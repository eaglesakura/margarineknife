package com.eaglesakura.android.margarine;

import android.support.annotation.IdRes;
import android.support.annotation.Keep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method set
 */
@Keep
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OnCheckedChanged {
    /**
     * R.id.**
     */
    @Keep
    @IdRes int[] value() default {};

    /**
     * R.id."resName"
     */
    @Keep
    String[] resName() default {};

    /**
     * BinderClass
     */
    @Keep
    Class binder() default MethodBinder.OnCheckedChangeBinder.class;

    @Keep
    boolean nullable() default false;
}
