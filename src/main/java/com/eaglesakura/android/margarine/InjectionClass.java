package com.eaglesakura.android.margarine;

import com.eaglesakura.util.CollectionUtil;
import com.eaglesakura.util.ReflectionUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InjectionClass {
    final Class mClass;

    List<FieldBinder> mBindFields;

    List<MethodBinder> mMethodBinders;

    public InjectionClass(Class clazz) {
        mClass = clazz;
    }

    /**
     * Injection対象のField一覧を取得する
     */
    public List<FieldBinder> listBindFields(Context context) {
        if (mBindFields == null) {
            mBindFields = CollectionUtil.asOtherList(ReflectionUtil.listAnnotationFields(mClass, BindRes.class),
                    it -> new FieldBinder(context, it)
            );
        }

        return mBindFields;
    }

    private void addBindMethods(Context context, Class<? extends Annotation> annotationClass) throws Exception {
        List<Method> methodList = ReflectionUtil.listAnnotationMethods(mClass, annotationClass);
        for (Method method : methodList) {
            Annotation annotation = method.getAnnotation(annotationClass);
            Class binderClass = (Class) annotationClass.getMethod("binder").invoke(annotation);
            Constructor constructor = binderClass.getDeclaredConstructor(Context.class, Method.class, Class.class);
            mMethodBinders.add((MethodBinder) constructor.newInstance(context, method, annotationClass));
        }
    }

    public List<MethodBinder> listBindMethods(Context context) {
        if (mMethodBinders == null) {
            mMethodBinders = new ArrayList<>();

            try {
                addBindMethods(context, OnClick.class);
                addBindMethods(context, OnCheckedChange.class);
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        return mMethodBinders;
    }

    public Context getContext(Object src) {
        if (src instanceof Activity) {
            return (Activity) src;
        } else if (src instanceof Fragment) {
            return ((Fragment) src).getContext();
        } else if ((src instanceof View)) {
            return ((View) src).getContext();
        } else if (src instanceof Dialog) {
            return ((Dialog) src).getContext();
        }

        return null;
    }

    public String getStringRes(Object src, int resId) {
        return getContext(src).getString(resId);
    }

    public int getIntRes(Object src, int resId) {
        return getContext(src).getResources().getInteger(resId);
    }

    @SuppressLint("NewApi")
    public View findView(Object src, int resId) {
        if (src instanceof View) {
            return ((View) src).findViewById(resId);
        } else if (src instanceof Fragment) {
            return ((Fragment) src).getView().findViewById(resId);
        } else if (src instanceof Activity) {
            return ((Activity) src).findViewById(resId);
        } else if (src instanceof Dialog) {
            return ((Dialog) src).findViewById(resId);
        }

        throw new Error("resId :: " + resId);
    }

    static final Map<Class, InjectionClass> sCaches = new HashMap<>();

    public synchronized static InjectionClass get(Class clazz) {
        InjectionClass aClass = sCaches.get(clazz);
        if (aClass == null) {
            aClass = new InjectionClass(clazz);
            sCaches.put(clazz, aClass);
        }

        return aClass;
    }
}
