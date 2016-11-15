package com.eaglesakura.android.margarine;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 書き込み対象のメンバ変数を構築する
 */
@Keep
public abstract class FieldResourceBinder implements FieldBinder {
    /**
     * 書き込み対象メンバ変数
     */
    final Field mField;

    /**
     * Res Id
     */
    final int mResourceId;


    /**
     * NULLを認める場合はtrue
     */
    protected boolean mNullable;

    @Keep
    public FieldResourceBinder(Context context, Field field, Class annotationClass, String identifierType) {
        mField = field;
        if (!mField.isAccessible()) {
            mField.setAccessible(true);
        }

        try {
            Annotation annotation = field.getAnnotation(annotationClass);
            try {
                mNullable = (Boolean) annotation.getClass().getMethod("nullable").invoke(annotation);
            } catch (Exception e) {
            }

            final int intResId = (Integer) annotation.getClass().getMethod("value").invoke(annotation);
            final String resName = (String) annotation.getClass().getMethod("resName").invoke(annotation);

            if (intResId != 0) {
                mResourceId = intResId;
            } else {
                mResourceId = context.getResources().getIdentifier(resName, identifierType, context.getPackageName());
                if (mResourceId == 0) {
                    throw new ResourceBindError("ResourceName Error :: " + resName);
                }
            }
        } catch (ResourceBindError e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceBindError(e);
        }
    }

    /**
     * 値を書き込む
     */
    @Override
    public void apply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) {

        try {
            onApply(srcClass, src, dstClass, dst);
        } catch (Exception e) {
            throw new ResourceBindError(e);
        }
    }

    /**
     * 値を実際に書き込む
     */
    protected abstract void onApply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) throws Exception;


    @Keep
    public static class FieldBinderView extends FieldResourceBinder {
        @Keep
        public FieldBinderView(Context context, Field field, Class annotationClass) {
            super(context, field, annotationClass, "id");
            valid(View.class);
        }

        @Override
        protected void onApply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) throws Exception {
            mField.set(dst, lazy(srcClass, src, dstClass, dst));
        }

        public Object lazy(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) {
            try {
                return srcClass.findView(src, mResourceId);
            } catch (Throwable e) {
                if (mNullable) {
                    // Nullを認めるため、何もしない
                    return null;
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * IDE対策のため、命名を変えておく
     */
    @Keep
    public static class FieldBinderString extends FieldResourceBinder {
        @Keep
        public FieldBinderString(Context context, Field field, Class annotationClass) {
            super(context, field, annotationClass, "string");
        }

        @Override
        protected void onApply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) throws Exception {
            mField.set(dst, srcClass.getStringRes(src, mResourceId));
            valid(String.class);
        }
    }

    @Keep
    public static class FieldBinderInteger extends FieldResourceBinder {
        @Keep
        public FieldBinderInteger(Context context, Field field, Class annotationClass) {
            super(context, field, annotationClass, "integer");
        }

        @Override
        protected void onApply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) throws Exception {
            mField.setInt(dst, srcClass.getIntRes(src, mResourceId));
            valid(int.class);
        }
    }

    @Keep
    public static class FieldBinderStringArray extends FieldResourceBinder {
        @Keep
        public FieldBinderStringArray(Context context, Field field, Class annotationClass) {
            super(context, field, annotationClass, "array");
            valid(String[].class);
        }

        @Override
        protected void onApply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) throws Exception {
            mField.set(dst, srcClass.getStringArrayRes(src, mResourceId));
        }
    }

    void valid(Class checkType) {
        Class<?> fieldType = mField.getType();
        if (fieldType.equals(checkType)) {
            return;
        }

        if (fieldType.asSubclass(checkType) != null) {
            return;
        }

        throw new ResourceBindError(checkType.getSimpleName() + " != " + fieldType.getSimpleName());
    }
}
