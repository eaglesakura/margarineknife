package com.eaglesakura.android.garnet;

import com.eaglesakura.android.garnet.error.DefaultConstructorNotFoundError;
import com.eaglesakura.android.garnet.error.InstanceCreateError;

import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;

class InstanceCreatorImpl implements InstanceCreator {
    @NonNull
    Class mClass;

    @NonNull
    Constructor mConstructor;

    public InstanceCreatorImpl(@NonNull Class aClass) throws Error {
        mClass = aClass;
        try {
            mConstructor = mClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new DefaultConstructorNotFoundError(e);
        }
    }

    @Override
    public Object newInstance(Object dst) {
        try {
            return mClass.newInstance();
        } catch (Exception e) {
            throw new InstanceCreateError(e);
        }
    }

    @Override
    public void initialize(Object instance, Object dst) {

    }
}
