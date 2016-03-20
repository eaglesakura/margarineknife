package com.eaglesakura.android.margarine;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class MethodBinder {

    protected final Method mMethod;

    /**
     * Viewを第1引数として含める場合はtrue
     */
    protected final boolean mViewParameterRequire;

    protected int[] mViewIdList;

    public MethodBinder(Context context, Method method, Class annotationClass) {
        try {
            mMethod = method;
            if (!mMethod.isAccessible()) {
                mMethod.setAccessible(true);
            }

            {
                Class<?>[] types = mMethod.getParameterTypes();
                if (types == null || types.length == 0 || types[0].isPrimitive()) {
                    mViewParameterRequire = false;
                } else if (types[0].asSubclass(View.class) != null) {
                    mViewParameterRequire = true;
                } else {
                    mViewParameterRequire = false;
                }
            }

            Annotation annotation = mMethod.getAnnotation(annotationClass);
            int[] intResId = (int[]) annotation.getClass().getMethod("value").invoke(annotation);
            String[] resNames = (String[]) annotation.getClass().getMethod("resName").invoke(annotation);
            if (intResId.length > 0) {
                mViewIdList = intResId;
            } else if (resNames.length > 0) {
                mViewIdList = new int[resNames.length];
                for (int i = 0; i < resNames.length; ++i) {
                    mViewIdList[i] = context.getResources().getIdentifier(
                            resNames[i], "id", context.getPackageName()
                    );
                }
            }

            if (mViewIdList.length == 0) {
                throw new Error();
            } else {
                for (int id : mViewIdList) {
                    if (id == 0) {
                        throw new Error();
                    }
                }
            }
        } catch (Exception e) {
            throw new MethodBindError(e);
        }
    }

    public List<View> listBindViews(InjectionClass srcClass, Object src) {
        List<View> result = new ArrayList<>();
        for (int resId : mViewIdList) {
            result.add(srcClass.findView(src, resId));
        }

        return result;
    }

    public abstract void bind(Object dst, View view);

    protected Object invoke(Object dst, Object... args) {
        try {
            return mMethod.invoke(dst, args);
        } catch (InvocationTargetException e) {
            throw new Error(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * setOnClickListener
     */
    public static class OnClickBinder extends MethodBinder {
        public OnClickBinder(Context context, Method method, Class<? extends Annotation> annotationClass) {
            super(context, method, annotationClass);
        }

        @Override
        public void bind(Object dst, View view) {
            view.setOnClickListener(it -> {
                if (mViewParameterRequire) {
                    invoke(dst, view);
                } else {
                    invoke(dst);
                }
            });
        }
    }

    /**
     *
     */
    public static class OnLongClickBinder extends MethodBinder {
        public OnLongClickBinder(Context context, Method method, Class annotationClass) {
            super(context, method, annotationClass);
        }

        @Override
        public void bind(Object dst, View view) {
            view.setOnLongClickListener(it -> {
                if (mViewParameterRequire) {
                    return (Boolean) invoke(dst, it);
                } else {
                    return (Boolean) invoke(dst);
                }
            });
        }
    }

    /**
     * setOnCheckedChangeListener
     */
    public static class OnCheckedChangeBinder extends MethodBinder {
        public OnCheckedChangeBinder(Context context, Method method, Class<? extends Annotation> annotationClass) {
            super(context, method, annotationClass);
        }

        @Override
        public void bind(Object dst, View view) {
            ((CompoundButton) view).setOnCheckedChangeListener((it, isChecked) -> {
                if (mViewParameterRequire) {
                    invoke(dst, it, isChecked);
                } else {
                    invoke(dst, isChecked);
                }
            });
        }
    }
}
