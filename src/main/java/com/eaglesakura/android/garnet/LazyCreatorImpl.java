package com.eaglesakura.android.garnet;

import com.eaglesakura.android.margarine.Lazy;

import android.support.annotation.NonNull;

class LazyCreatorImpl implements InstanceCreator {

    @NonNull
    final InstanceCreator mCreator;

    public LazyCreatorImpl(@NonNull InstanceCreator injector) {
        mCreator = injector;
    }

    @Override
    public Object newInstance(Object dst) {
        return new LazyImpl(dst);
    }

    @Override
    public void initialize(Object instance, Object dst) {

    }

    class LazyImpl implements Lazy {
        Object mItem;

        boolean mInitialized;

        final Object mDst;

        public LazyImpl(Object dst) {
            mDst = dst;
        }

        @Override
        public Object get() {
            if (!mInitialized) {
                synchronized (this) {
                    if (!mInitialized) {
                        mItem = mCreator.newInstance(mDst);
                        mCreator.initialize(mItem, mDst);
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
