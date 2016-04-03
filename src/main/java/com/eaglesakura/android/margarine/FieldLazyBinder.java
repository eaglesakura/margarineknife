package com.eaglesakura.android.margarine;

import java.lang.reflect.Field;

/**
 * 遅延実行を行う
 */
class FieldLazyBinder implements FieldBinder {
    /**
     *
     */
    final FieldBinder mFieldBinder;

    /**
     * 書き込み対象Field
     */
    final Field mField;

    public FieldLazyBinder(Field field, FieldBinder fieldBinder) {
        mField = field;
        mField.setAccessible(true);
        mFieldBinder = fieldBinder;
    }

    @Override
    public void apply(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) {
        try {
            mField.set(dst, new LazyImpl(srcClass, src, dstClass, dst));
        } catch (Exception e) {
            throw new ResourceBindError(e);
        }
    }

    /**
     * 呼びだされてはならない
     */
    @Override
    public Object lazy(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) {
        throw new UnsupportedOperationException();
    }

    class LazyImpl implements Lazy {
        Object mItem;

        boolean mInitialized;

        final InjectionClass mInjectionSrcClass;

        final Object mSrc;

        final InjectionClass mInjectionDstClass;

        final Object mDst;

        public LazyImpl(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) {
            mInjectionSrcClass = srcClass;
            mSrc = src;
            mInjectionDstClass = dstClass;
            mDst = dst;
        }

        @Override
        public Object get() {
            if (!mInitialized) {
                synchronized (this) {
                    if (!mInitialized) {
                        mItem = mFieldBinder.lazy(mInjectionSrcClass, mSrc, mInjectionDstClass, mDst);
                        mInitialized = true;
                    }
                }
            }
            return mItem;
        }

        @Override
        public void clear() {
            synchronized (this) {
                mItem = null;
                mInitialized = false;
            }
        }
    }
}
