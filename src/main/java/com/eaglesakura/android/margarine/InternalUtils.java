package com.eaglesakura.android.margarine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class InternalUtils {

    /**
     * 指定したAnnotationが含まれたフィールド(public以外を含む)一覧を返す
     *
     * AnnotationにはRuntime属性が付与されてなければならない
     */
    static <T extends Annotation> List<Field> listAnnotationFields(Class srcClass, Class<T> annotationClass) {
        List<Field> result = new ArrayList<>();

        while (!srcClass.equals(Object.class)) {
            for (Field field : srcClass.getDeclaredFields()) {
                T annotation = field.getAnnotation(annotationClass);
                if (annotation != null) {
                    result.add(field);
                }
            }

            srcClass = srcClass.getSuperclass();
        }

        return result;
    }

    /**
     * 指定したAnnotationが含まれたメソッド(public以外を含む)一覧を返す
     *
     * AnnotationにはRuntime属性が付与されてなければならない
     * オーバーライドされたメソッドは1つにまとめて扱う
     */
    static <T extends Annotation> List<Method> listAnnotationMethods(Class srcClass, Class<T> annotationClass) {
        Map<String, Method> result = new HashMap<>();

        while (!srcClass.equals(Object.class)) {
            for (Method method : srcClass.getDeclaredMethods()) {
                T annotation = method.getAnnotation(annotationClass);
                if (annotation != null && !result.containsKey(method.getName())) {
                    result.put(method.getName(), method);
                }
            }

            srcClass = srcClass.getSuperclass();
        }

        return new ArrayList<>(result.values());
    }

    /**
     * 動的に実装を切り替える場合のファクトリ
     */
    private static final Map<Class, Class> sOverrideClasses = new HashMap<>();

    static void override(Class origin, Class stead) {
        synchronized (sOverrideClasses) {
            sOverrideClasses.put(origin, stead);
        }
        // ロード済みのClassを一旦削除する
        InjectionClass.clearCaches();
    }

    static Class getClass(Class origin) {
        synchronized (sOverrideClasses) {
            Class aClass = sOverrideClasses.get(origin);
            if (aClass != null) {
                return aClass;
            } else {
                return origin;
            }
        }
    }
}
