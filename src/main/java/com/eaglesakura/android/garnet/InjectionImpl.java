package com.eaglesakura.android.garnet;

import com.eaglesakura.android.garnet.error.InjectTargetError;
import com.eaglesakura.android.margarine.Lazy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class InjectionImpl {
    /**
     * 対象のClassファイル
     */
    @NonNull
    final Class mClass;

    Method mGetContext;

    List<Pair<Field, InstanceCreator>> mCreators = new ArrayList<>();

    public InjectionImpl(@NonNull Class aClass) {
        mClass = aClass;
        initInjectors();
    }

    void initInjectors() {
        try {
            for (Field field : InternalUtils.listAnnotationFields(mClass, Inject.class)) {

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                InstanceCreator creator;
                Inject inject = field.getAnnotation(Inject.class);
                if (!inject.instance().equals(Object.class)) {
                    // インスタンスを直接生成する
                    creator = new InstanceCreatorImpl(InternalUtils.getClass(inject.instance()));
                } else if (!inject.factory().equals(ComponentFactory.class)) {
                    // ファクトリを経由して生成する
                    creator = new FactoryCreatorImpl(InternalUtils.getClass(inject.factory()), field);
                } else {
                    throw new InjectTargetError();
                }
                // 遅延実行する
                if (field.getType().equals(Lazy.class)) {
                    mCreators.add(new Pair<>(field, new LazyCreatorImpl(creator)));
                } else {
                    mCreators.add(new Pair<>(field, creator));
                }
            }
        } catch (Error e) {
            throw e;
        } catch (Exception e) {
            throw new InjectTargetError(e);
        }

        try {
            mGetContext = mClass.getMethod("getContext");
        } catch (Exception e) {
        }
    }

    public Context getContext(Object dst) {
        try {
            if (mGetContext != null) {
                return (Context) mGetContext.invoke(dst);
            }

            if (dst instanceof Context) {
                return (Context) dst;
            }
        } catch (Throwable e) {
        }
        return null;
    }

    public void inject(Object dst) {
        try {
            for (Pair<Field, InstanceCreator> creator : mCreators) {
                Object instance = creator.second.newInstance(dst);
                creator.first.set(dst, instance);
            }

            for (Pair<Field, InstanceCreator> creator : mCreators) {
                Object instance = creator.first.get(dst);
                creator.second.initialize(instance, dst);
            }
        } catch (Exception e) {
            throw new Error(e);
        }

    }
}
