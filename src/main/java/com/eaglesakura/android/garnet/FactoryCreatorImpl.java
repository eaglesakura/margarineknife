package com.eaglesakura.android.garnet;

import com.eaglesakura.android.garnet.error.InstanceInitializeError;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

class FactoryCreatorImpl implements InstanceCreator {
    @NonNull
    final Class<? extends ComponentFactory> mFactory;

    @NonNull
    final Field mField;

    final static WeakHashMap<Class, ComponentFactory> sFactoryMap = new WeakHashMap<>();

    public FactoryCreatorImpl(@NonNull Class<? extends ComponentFactory> factory, @NonNull Field field) {
        mFactory = factory;
        mField = field;
    }

    private ComponentFactory getFactory() throws Exception {
        synchronized (sFactoryMap) {
            ComponentFactory factory = sFactoryMap.get(mFactory);
            if (factory == null) {
                factory = mFactory.newInstance();
                sFactoryMap.put(mFactory, factory);
            }
            return factory;
        }
    }

    @Override
    public Object newInstance(Object dst) {
        try {
            ComponentFactory factory = getFactory();
            return factory.newInstance(mField, dst);
        } catch (Exception e) {
            throw new InstanceInitializeError(e);
        }
    }

    @Override
    public void initialize(Object instance, Object dst) {
        try {
            ComponentFactory factory = getFactory();
            factory.initialize(mField, dst, instance);
        } catch (Exception e) {
            throw new InstanceInitializeError(e);
        }
    }
}
