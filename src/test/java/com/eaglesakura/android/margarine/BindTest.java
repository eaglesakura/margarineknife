package com.eaglesakura.android.margarine;

import org.junit.Test;

import android.view.View;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BindTest extends UnitTestCase {

    @Test(expected = ResourceBindError.class)
    public void 間違ったID指定でフィールドのバインドに失敗する() throws Exception {
        MargarineKnife.bind(new FieldBindObject(), new View(getContext()));
        fail();
    }

    @Test(expected = ResourceBindError.class)
    public void 間違ったアノテーション指定でStringバインドに失敗する() throws Exception {
        MargarineKnife.bind(new ErrorStringBindObject(), new View(getContext()));
        fail();
    }

    @Test(expected = ResourceBindError.class)
    public void 間違ったアノテーション指定でintバインドに失敗する() throws Exception {
        MargarineKnife.bind(new ErrorIntBindObject(), new View(getContext()));
        fail();
    }


    @Test(expected = MethodBindError.class)
    public void メソッドのバインドに失敗する() throws Exception {
        MargarineKnife.bind(new MethodBindObject(), new View(getContext()));
        fail();
    }


    public static class ErrorViewBindObject {
        @BindString(123)
        View obj;
    }

    public static class ErrorStringBindObject {
        @Bind(123)
        String obj;
    }

    public static class ErrorIntBindObject {
        @Bind(123)
        int obj;
    }


    public static class FieldBindObject {
        @BindString(123)
        String stringResInt;

        @BindString(resName = "Error")
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
