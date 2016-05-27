package com.eaglesakura.android.margarine;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method set
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OnCheckedChanged {
    /**
     * R.id.**
     */
    @IdRes int[] value() default {};

    /**
     * R.id."resName"
     */
    String[] resName() default {};

    /**
     * BinderClass
     */
    Class binder() default MethodBinder.OnCheckedChangeBinder.class;

    boolean nullable() default true;
}
