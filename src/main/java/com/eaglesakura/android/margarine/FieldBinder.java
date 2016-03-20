package com.eaglesakura.android.margarine;

import android.content.Context;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 書き込み対象のメンバ変数を構築する
 */
public abstract class FieldBinder {
    /**
     * 書き込み対象メンバ変数
     */
    final Field mField;

    /**
     * Res Id
     */
    final int mResourceId;

    public FieldBinder(Context context, Field field, Class annotationClass, String identifierType) {
        mField = field;
        if (!mField.isAccessible()) {
            mField.setAccessible(true);
        }

        try {
            Annotation annotation = field.getAnnotation(annotationClass);

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
        } catch (Exception e) {
            throw new ResourceBindError(e);
        }
    }

    /**
     * 値を書き込む
     */
    public void apply(InjectionClass srcClass, Object src, Object dst) {

        try {
            onApply(srcClass, src, dst);
        } catch (Exception e) {
            throw new ResourceBindError(e);
        }
    }

    /**
     * 値を実際に書き込む
     */
    protected abstract void onApply(InjectionClass srcClass, Object src, Object dst) throws Exception;


    public static class ViewFieldBinder extends FieldBinder {
        public ViewFieldBinder(Context context, Field field, Class annotationClass) {
            super(context, field, annotationClass, "id");
            valid(View.class);
        }

        @Override
        protected void onApply(InjectionClass srcClass, Object src, Object dst) throws Exception {
            mField.set(dst, srcClass.findView(src, mResourceId));
        }
    }

    public static class StringFieldBinder extends FieldBinder {
        public StringFieldBinder(Context context, Field field, Class annotationClass) {
            super(context, field, annotationClass, "string");
            valid(String.class);
        }

        @Override
        protected void onApply(InjectionClass srcClass, Object src, Object dst) throws Exception {
            mField.set(dst, srcClass.getStringRes(src, mResourceId));
        }
    }

    public static class IntegerFieldBinder extends FieldBinder {
        public IntegerFieldBinder(Context context, Field field, Class annotationClass) {
            super(context, field, annotationClass, "integer");
            valid(int.class);
        }

        @Override
        protected void onApply(InjectionClass srcClass, Object src, Object dst) throws Exception {
            mField.setInt(dst, srcClass.getIntRes(src, mResourceId));
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
