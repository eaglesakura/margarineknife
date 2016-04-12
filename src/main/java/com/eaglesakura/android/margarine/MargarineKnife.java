package com.eaglesakura.android.margarine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
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

    /**
     * self to self
     */
    public static void bindSelf(Object srcDst) {
        new Builder().from(srcDst).to(srcDst).bind();
    }

    public static void bind(Object dst, Object src) {
        new Builder().from(src).to(dst).bind();
    }

    /**
     * Menuメソッドを関連付ける
     */
    public static void bindMenu(Menu menu, Object dst) {
        new MenuBuilder().from(menu).to(dst).bind();
    }

    public static Builder from(Object src) {
        return new Builder().from(src);
    }

    static class MenuBuilder {
        private Menu mMenu;

        private Object mDst;

        private InjectionClass mDstClass;

        private MenuBuilder() {
        }

        public MenuBuilder from(Menu menu) {
            mMenu = menu;
            return this;
        }

        public MenuBuilder to(Object dst) {
            mDst = dst;
            mDstClass = InjectionClass.get(dst.getClass());
            return this;
        }

        public void bind() {
            Context context = null;

            for (MenuBinder binder : mDstClass.listBindMenues(context)) {
                for (MenuItem item : binder.listBindMenues(mMenu)) {
                    binder.bind(mDst, item);
                }
            }
        }
    }

    public static class Builder {
        private Object mSrc;

        private InjectionClass mSrcClass;

        private Object mDst;

        private InjectionClass mDstClass;

        private Builder() {
        }

        public Builder from(Object src) {
            mSrc = src;
            mSrcClass = InjectionClass.get(src.getClass());
            return this;
        }

        public Builder to(Object dst) {
            mDst = dst;
            mDstClass = InjectionClass.get(dst.getClass());
            return this;
        }

        public void bind() {
            Context context = mSrcClass.getContext(mSrc);

            for (FieldBinder field : mDstClass.listBindFields(context)) {
                field.apply(mSrcClass, mSrc, mDstClass, mDst);
            }

            for (MethodBinder method : mDstClass.listBindMethods(context)) {
                List<View> views = method.listBindViews(mSrcClass, mSrc);
                for (View view : views) {
                    method.bind(mDst, view);
                }
            }
        }
    }
}
