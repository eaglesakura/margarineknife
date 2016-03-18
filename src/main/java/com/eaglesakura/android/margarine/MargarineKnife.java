package com.eaglesakura.android.margarine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.List;

public class MargarineKnife {

    public static void bind(Activity activity) {
        bind(activity, activity);
    }

    public static void bind(Fragment fragment) {
        bind(fragment, fragment);
    }

    public static void bind(View view) {
        bind(view, view);
    }

    public static void bind(Dialog dialog) {
        bind(dialog, dialog);
    }

    public static void bind(Object src, Object dst) {
        bind(InjectionClass.get(src.getClass()), src, InjectionClass.get(dst.getClass()), dst);
    }

    /**
     * データを注入する
     */
    public static void bind(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst) {
        Context context = srcClass.getContext(src);

        for (FieldBinder field : dstClass.listBindFields(context)) {
            field.apply(srcClass, src, dst);
        }

        for (MethodBinder method : dstClass.listBindMethods(context)) {
            List<View> views = method.listBindViews(srcClass, src);
            for (View view : views) {
                method.bind(dst, view);
            }
        }
    }
}
