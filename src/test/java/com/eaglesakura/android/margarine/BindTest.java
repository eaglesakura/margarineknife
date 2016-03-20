package com.eaglesakura.android.margarine;

import org.junit.Test;

import android.view.View;

public class BindTest extends UnitTestCase {

    @Test(expected = ResourceBindError.class)
    public void フィールドのバインドに失敗する() throws Exception {
        MargarineKnife.bind(new View(getContext()), new FieldBindObject());
    }

    @Test(expected = MethodBindError.class)
    public void メソッドのバインドに失敗する() throws Exception {
        MargarineKnife.bind(new View(getContext()), new MethodBindObject());
    }

    public static class FieldBindObject {
        @BindRes(123)
        String stringResInt;

        @BindRes(resName = "Error")
        String stringResName;
    }

    public static class MethodBindObject {
        @OnClick(128)
        void clickMethod(View view) {

        }

        @OnClick(resName = "Error")
        void clickMethod2(View view) {

        }
    }
}
