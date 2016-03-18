package com.eaglesakura.android.margarine;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Field;

/**
 * 書き込み対象のメンバ変数を構築する
 */
public class FieldBinder {
    /**
     * 書き込み対象メンバ変数
     */
    final Field mField;

    /**
     * バインド対象
     */
    final BindRes mBind;

    /**
     * Res Id
     */
    final int mResourceId;

    enum BindType {
        StringRes {
            @Override
            String getResourceType() {
                return "string";
            }

            @Override
            void setValue(Field field, Object dst, Object value) throws Exception {
                field.set(dst, value);
            }

            @Override
            Object getValue(InjectionClass clazz, Object src, int resId) {
                return clazz.getStringRes(src, resId);
            }
        },
        IntegerRes {
            @Override
            String getResourceType() {
                return "integer";
            }

            @Override
            void setValue(Field field, Object dst, Object value) throws Exception {
                field.setInt(dst, (Integer) value);
            }

            @Override
            Object getValue(InjectionClass clazz, Object src, int resId) {
                return clazz.getIntRes(src, resId);
            }
        },
        View {
            @Override
            String getResourceType() {
                return "id";
            }

            @Override
            void setValue(Field field, Object dst, Object value) throws Exception {
                field.set(dst, value);
            }

            @Override
            Object getValue(InjectionClass clazz, Object src, int resId) {
                return clazz.findView(src, resId);
            }
        };

        abstract String getResourceType();

        abstract void setValue(Field field, Object dst, Object value) throws Exception;

        abstract Object getValue(InjectionClass clazz, Object src, int resId);
    }

    final BindType mType;

    public FieldBinder(Context context, Field field) {
        mField = field;
        mBind = field.getAnnotation(BindRes.class);

        // Classを特定する
        final Class<?> DST_TYPE = field.getType();
        if (DST_TYPE.equals(String.class)) {
            mType = BindType.StringRes;
        } else if (DST_TYPE.equals(int.class)) {
            mType = BindType.IntegerRes;
        } else if (DST_TYPE.asSubclass(View.class) != null) {
            mType = BindType.View;
        } else {
            throw new Error("Bind Error :: " + DST_TYPE);
        }

        // ResourceIdを特定する
        if (mBind.value() != 0) {
            mResourceId = mBind.value();
        } else {
            mResourceId = context.getResources().getIdentifier(mBind.resName(), mType.getResourceType(), context.getPackageName());
            if (mResourceId == 0) {
                throw new Error("ResourceName Error :: " + mBind.resName());
            }
        }
    }

    /**
     * Viewを書き込む
     */
    public void apply(InjectionClass srcClass, Object src, Object dst) {
        if (!mField.isAccessible()) {
            mField.setAccessible(true);
        }

        try {
            Object value = mType.getValue(srcClass, src, mResourceId);
            mType.setValue(mField, dst, value);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
