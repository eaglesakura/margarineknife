package com.eaglesakura.android.margarine;

import android.content.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Instance生成を行わせる
 */
public class FieldInstanceBinder implements FieldBinder {
    /**
     * 書き込み対象メンバ変数
     */
    final Field mField;

    /**
     * ファクトリを経由する場合の生成器
     */
    Class<? extends InjectionFactory> mFactory;

    /**
     * 直接オブジェクトを生成する場合のコンストラクタ
     */
    Constructor mConstructor;

    /**
     * nullを認める場合はtrue
     */
    boolean mNullable;

    public FieldInstanceBinder(Context context, Field field, Class annotationClass) {
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

            Class instanceClass = InternalUtils.getClass((Class) annotation.getClass().getMethod("value").invoke(annotation));
            Class factoryClass = InternalUtils.getClass((Class) annotation.getClass().getMethod("factory").invoke(annotation));

            if (instanceClass != null && !instanceClass.equals(Object.class)) {
                // 直接インスタンスを生成させる
                mConstructor = instanceClass.getConstructor();
            } else if (factoryClass != null && !factoryClass.equals(InjectionFactory.class)) {
                mFactory = factoryClass;
            } else {
                throw new ResourceBindError("Instance Error :: " + mField.getName());
            }
        } catch (ResourceBindError e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceBindError(e);
        }
    }

    public boolean isNonNull() {
        return !mNullable;
    }

    public boolean isNullable() {
        return mNullable;
    }

    @Override
    public void apply(InjectionClass srcClass, Object src, Object dst) {
        try {
            if (mConstructor != null) {
                mField.set(dst, mConstructor.newInstance());
            } else {
                InjectionClass dstClass = InjectionClass.get(dst.getClass());
                InjectionFactory factory = mFactory.newInstance();
                Object instance = factory.newInstance(srcClass, src, dstClass, dst, mField);
                if (instance == null && isNonNull()) {
                    // NonNull制約でインスタンス生成に失敗した
                    throw new InstanceCreateFailedException();
                }
                mField.set(dst, instance);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InstanceCreateFailedException(e);
        }
    }

    @Override
    public Object lazy(InjectionClass srcClass, Object src, Object dst) {
        try {
            if (mConstructor != null) {
                return mConstructor.newInstance();
            } else {
                InjectionClass dstClass = InjectionClass.get(dst.getClass());
                InjectionFactory factory = mFactory.newInstance();
                Object instance = factory.newInstance(srcClass, src, dstClass, dst, mField);
                if (instance == null && isNonNull()) {
                    // NonNull制約でインスタンス生成に失敗した
                    throw new InstanceCreateFailedException();
                }
                return instance;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InstanceCreateFailedException(e);
        }
    }
}
