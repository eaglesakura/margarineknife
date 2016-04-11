package com.eaglesakura.android.margarine;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class MenuBinder {

    protected final Method mMethod;

    /**
     * Menuを第1引数として含める場合はtrue
     */
    protected final boolean mMenuParameterRequire;

    /**
     * 戻り値がある場合はtrue
     */
    protected final boolean mHasMenuResult;

    protected int[] mMenuIdList;

    public MenuBinder(Context context, Method method, Class annotationClass) {

        try {
            mMethod = method;
            if (!mMethod.isAccessible()) {
                mMethod.setAccessible(true);
            }

            // 第一引数に MenuItem を含めるか
            {
                Class<?>[] types = mMethod.getParameterTypes();
                if (types == null || types.length == 0 || types[0].isPrimitive()) {
                    mMenuParameterRequire = false;
                } else if (types[0].asSubclass(MenuItem.class) != null) {
                    mMenuParameterRequire = true;
                } else {
                    mMenuParameterRequire = false;
                }
            }

            // 戻り値があるか
            mHasMenuResult = !mMethod.getReturnType().equals(void.class);

            Annotation annotation = mMethod.getAnnotation(annotationClass);
            int[] intResId = (int[]) annotation.getClass().getMethod("value").invoke(annotation);
            String[] resNames = (String[]) annotation.getClass().getMethod("resName").invoke(annotation);
            if (intResId.length > 0) {
                mMenuIdList = intResId;
            } else if (resNames.length > 0) {
                mMenuIdList = new int[resNames.length];
                for (int i = 0; i < resNames.length; ++i) {
                    mMenuIdList[i] = context.getResources().getIdentifier(
                            resNames[i], "id", context.getPackageName()
                    );
                }
            }

            if (mMenuIdList.length == 0) {
                throw new Error();
            } else {
                for (int id : mMenuIdList) {
                    if (id == 0) {
                        throw new Error();
                    }
                }
            }
        } catch (Exception e) {
            throw new MethodBindError(e);
        }
    }

    List<MenuItem> listBindMenues(Menu menu) {
        List<MenuItem> result = new ArrayList<>();
        for (int resId : mMenuIdList) {
            MenuItem item = menu.findItem(resId);
            if (item == null) {
                throw new MenuNotFoundError("ResId :: " + resId);
            }
            result.add(item);
        }
        return result;
    }

    public abstract void bind(Object dst, MenuItem menu);

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
    public static class OnClickBinder extends MenuBinder {
        public OnClickBinder(Context context, Method method, Class<? extends Annotation> annotationClass) {
            super(context, method, annotationClass);
            valid();
        }

        @Override
        public void bind(Object dst, MenuItem menu) {
            menu.setOnMenuItemClickListener((it) -> {
                Object result;
                if (mMenuParameterRequire) {
                    result = invoke(dst, it);
                } else {
                    result = invoke(dst);
                }

                if (mHasMenuResult) {
                    return (boolean) result;
                } else {
                    return true;
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
        if (mMenuParameterRequire) {
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
