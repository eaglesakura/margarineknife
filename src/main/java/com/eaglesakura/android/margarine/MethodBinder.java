package com.eaglesakura.android.margarine;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;
import android.widget.CompoundButton;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Keep
public abstract class MethodBinder {

    protected final Method mMethod;

    /**
     * Viewを第1引数として含める場合はtrue
     */
    protected final boolean mViewParameterRequire;

    protected int[] mViewIdList;


    protected final boolean mBindRequire;

    @Keep
    public MethodBinder(Context context, Method method, Class annotationClass) {
        try {
            mMethod = method;
            if (!mMethod.isAccessible()) {
                mMethod.setAccessible(true);
            }

            // メソッドの第1引数にViewを含めるか確認する
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
            mBindRequire = !(boolean) annotation.getClass().getMethod("nullable").invoke(annotation);
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
                    if (id == 0 && mBindRequire) {
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
    @Keep
    public static class OnClickBinder extends MethodBinder {
        @Keep
        public OnClickBinder(Context context, Method method, Class<? extends Annotation> annotationClass) {
            super(context, method, annotationClass);
            valid();
        }

        @Override
        public void bind(Object dst, View view) {
            // 必須ではないバインドは無視する
            if (view == null && !mBindRequire) {
                return;
            }

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
    @Keep
    public static class OnLongClickBinder extends MethodBinder {
        @Keep
        public OnLongClickBinder(Context context, Method method, Class annotationClass) {
            super(context, method, annotationClass);
            valid();
        }

        @Override
        public void bind(Object dst, View view) {
            // 必須ではないバインドは無視する
            if (view == null && !mBindRequire) {
                return;
            }

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
    @Keep
    public static class OnCheckedChangeBinder extends MethodBinder {
        @Keep
        public OnCheckedChangeBinder(Context context, Method method, Class<? extends Annotation> annotationClass) {
            super(context, method, annotationClass);
            valid(boolean.class);
        }

        @Override
        public void bind(Object dst, View view) {
            // 必須ではないバインドは無視する
            if (view == null && !mBindRequire) {
                return;
            }

            ((CompoundButton) view).setOnCheckedChangeListener((it, isChecked) -> {
                if (mViewParameterRequire) {
                    invoke(dst, it, isChecked);
                } else {
                    invoke(dst, isChecked);
                }
            });
        }
    }

    String toArgMessages(Object[] args) {
        String result = " ";
        for (Object arg : args) {
            result += (" " + arg.toString());
        }
        return result;
    }

    void valid(Object... args) {
        Class<?>[] parameterTypes = mMethod.getParameterTypes();

        int paramIndex;
        if (mViewParameterRequire) {
            if ((parameterTypes.length - 1) != args.length) {
                throw new MethodBindError("parameter req(" + toArgMessages(args) + " ) method(" + toArgMessages(parameterTypes) + " )");
            }
            paramIndex = 1;
        } else {
            if (parameterTypes.length != args.length) {
                throw new MethodBindError("parameter req(" + toArgMessages(args) + " ) method(" + toArgMessages(parameterTypes) + " )");
            }
            paramIndex = 0;
        }

        for (int i = 0; i < args.length; ++i) {
            Object reqArg = args[i];
            Object param = parameterTypes[paramIndex];

            if (!param.equals(reqArg)) {
                throw new MethodBindError("Param Error :: " + reqArg.toString() + " != " + param.toString());
            }

            ++paramIndex;
        }
    }
}
