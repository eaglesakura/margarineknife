package com.eaglesakura.android.garnet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Inject {
    /**
     * 直接インスタンスを指定する場合はtrue
     */
    Class instance() default Object.class;

    /**
     * ファクトリを使用する場合はClassを指定する
     */
    Class<? extends ComponentFactory> factory() default ComponentFactory.class;
}
