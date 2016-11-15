package com.eaglesakura.android.margarine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InjectionClass {
    final Class mClass;

    List<FieldBinder> mBindFields;

    List<MethodBinder> mMethodBinders;

    List<MenuBinder> mMenuBinders;

    static final Set<Class<? extends Annotation>> sFieldAnnotations = new HashSet<>();

    static final Set<Class<? extends Annotation>> sMethodAnnotations = new HashSet<>();

    static final Set<Class<? extends Annotation>> sMenuAnnotations = new HashSet<>();

    static {
        sFieldAnnotations.add(Bind.class);
        sFieldAnnotations.add(BindInt.class);
        sFieldAnnotations.add(BindString.class);
        sFieldAnnotations.add(BindStringArray.class);

        sMethodAnnotations.add(OnClick.class);
        sMethodAnnotations.add(OnCheckedChanged.class);
        sMethodAnnotations.add(OnLongClick.class);

        sMenuAnnotations.add(OnMenuClick.class);
    }

    /**
     * バインドに使用するAnnotationを追加する
     */
    public static void addFieldAnnotation(Class<? extends Annotation> clazz) {
        sFieldAnnotations.add(clazz);
    }

    /**
     * バインドに使用するAnnotationを追加する
     */
    public static void addMethodAnnotation(Class<? extends Annotation> clazz) {
        sMethodAnnotations.add(clazz);
    }

    public InjectionClass(Class clazz) {
        mClass = clazz;
    }

    private void addBindFields(Context context, Class<? extends Annotation> annotationClass) throws Exception {
        List<Field> fields = InternalUtils.listAnnotationFields(mClass, annotationClass);
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            Class binderClass = (Class) annotationClass.getMethod("binder").invoke(annotation);
            binderClass = InternalUtils.getClass(binderClass);
            Constructor constructor = binderClass.getDeclaredConstructor(Context.class, Field.class, Class.class);

            FieldBinder fieldBinder = (FieldBinder) constructor.newInstance(context, field, annotationClass);
            mBindFields.add(fieldBinder);
        }
    }

    /**
     * Injection対象のField一覧を取得する
     */
    List<FieldBinder> listBindFields(Context context) {
        if (mBindFields == null) {
            mBindFields = new ArrayList<>();
            try {
                for (Class<? extends Annotation> clazz : sFieldAnnotations) {
                    addBindFields(context, clazz);
                }
            } catch (Exception e) {
                throw new ResourceBindError(e);
            }
        }

        return mBindFields;
    }

    List<MenuBinder> listBindMenues(Context context) {
        if (mMenuBinders == null) {
            mMenuBinders = new ArrayList<>();
            try {
                for (Class<? extends Annotation> clazz : sMenuAnnotations) {
                    addBindMenuMethods(context, clazz);
                }
            } catch (Exception e) {
                throw new ResourceBindError(e);
            }
        }

        return mMenuBinders;
    }


    private void addBindMethods(Context context, Class<? extends Annotation> annotationClass) throws Exception {
        List<Method> methodList = InternalUtils.listAnnotationMethods(mClass, annotationClass);
        for (Method method : methodList) {
            Annotation annotation = method.getAnnotation(annotationClass);
            Class binderClass = (Class) annotationClass.getMethod("binder").invoke(annotation);
            binderClass = InternalUtils.getClass(binderClass);
            Constructor constructor;
            try {
                constructor = binderClass.getDeclaredConstructor(Context.class, Method.class, Class.class);
            } catch (Exception e) {
                throw new Exception("FieldBinder class[" + binderClass.getName() + "]", e);
            }
            mMethodBinders.add((MethodBinder) constructor.newInstance(context, method, annotationClass));
        }
    }

    private void addBindMenuMethods(Context context, Class<? extends Annotation> annotationClass) throws Exception {
        List<Method> methodList = InternalUtils.listAnnotationMethods(mClass, annotationClass);
        for (Method method : methodList) {
            Annotation annotation = method.getAnnotation(annotationClass);
            Class binderClass = (Class) annotationClass.getMethod("binder").invoke(annotation);
            binderClass = InternalUtils.getClass(binderClass);
            Constructor constructor;
            try {
                constructor = binderClass.getDeclaredConstructor(Context.class, Method.class, Class.class);
            } catch (Exception e) {
                throw new Exception("MenuBinder class[" + binderClass.getName() + "]", e);
            }
            mMenuBinders.add((MenuBinder) constructor.newInstance(context, method, annotationClass));
        }
    }

    List<MethodBinder> listBindMethods(Context context) {
        if (mMethodBinders == null) {
            mMethodBinders = new ArrayList<>();

            try {
                for (Class<? extends Annotation> clazz : sMethodAnnotations) {
                    addBindMethods(context, clazz);
                }
            } catch (Exception e) {
                throw new MethodBindError(e);
            }
        }

        return mMethodBinders;
    }

    private Method mGetContext;

    /**
     * Contextを得る
     *
     * Androidのシステムオブジェクト、もしくは引数なし"getContext()"メソッドを持っていれば値を取得できる。
     */
    public Context getContext(Object src) {
        if (src instanceof Context) {
            return (Context) src;
        } else if (src instanceof Fragment) {
            return ((Fragment) src).getContext();
        } else if ((src instanceof View)) {
            return ((View) src).getContext();
        } else if (src instanceof Dialog) {
            return ((Dialog) src).getContext();
        }

        try {
            if (mGetContext == null) {
                mGetContext = mClass.getMethod("getContext");
            }

            if (mGetContext != null) {
                return (Context) mGetContext.invoke(src);
            }
        } catch (Exception e) {

        }

        return null;
    }

    public String getStringRes(Object src, int resId) {
        return getContext(src).getString(resId);
    }

    public String[] getStringArrayRes(Object src, int resId) {
        return getContext(src).getResources().getStringArray(resId);
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

        throw new ViewNotFoundError("resId :: " + resId);
    }

    static final Map<Class, InjectionClass> sCaches = new HashMap<>();

    /**
     * Injection用クラスをキャッシュから取得する
     */
    public static InjectionClass get(Class clazz) {
        synchronized (sCaches) {
            InjectionClass aClass = sCaches.get(clazz);
            if (aClass == null) {
                aClass = new InjectionClass(clazz);
                sCaches.put(clazz, aClass);
            }
            return aClass;
        }
    }

    /**
     * Injection用クラスのキャッシュを削除する
     */
    public synchronized static void clearCaches() {
        synchronized (sCaches) {
            sCaches.clear();
        }
    }
}
