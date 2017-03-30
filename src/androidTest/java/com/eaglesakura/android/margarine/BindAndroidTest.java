package com.eaglesakura.android.margarine;

import com.eaglesakura.android.devicetest.DeviceTestCase;

import org.junit.Test;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

@SuppressLint("all")
public class BindAndroidTest extends DeviceTestCase {

    @Test
    public void test_Inject対象フィールド一覧を取得できる() throws Exception {
        InjectionClass target = new InjectionClass(BindTarget.class);
        List<FieldBinder> fields = target.listBindFields(getContext());
        assertNotNull(fields);
        assertEquals(fields.size(), 7);
    }

    @Test
    public void test_Inject対象メソッド一覧を取得できる() throws Exception {
        InjectionClass target = new InjectionClass(BindTarget.class);
        List<MethodBinder> binders = target.listBindMethods(getContext());

        assertNotNull(binders);
        assertEquals(binders.size(), 6);
    }

    @Test
    public void test_Nullable制約を加えられる() {
        View view = View.inflate(getContext(), com.eaglesakura.android.margarine.test.R.layout.view_bindtest, null);
        NullableBindTarget dst = new NullableBindTarget();
        assertNull(dst.mView);
        MargarineKnife.bind(dst, view);
        assertNull(dst.mView);
    }

    @Test
    public void test_Nullable制約無しではエラーになる() {
        View view = View.inflate(getContext(), com.eaglesakura.android.margarine.test.R.layout.view_bindtest, null);
        NullableBindTarget dst = new NullableBindTarget();
        assertNull(dst.mView);
        try {
            MargarineKnife.bind(dst, view);
            fail();
        } catch (Error e) {
        }
    }

    @Test
    public void test_アノテーションにバインドを行う() {
        View view = View.inflate(getContext(), com.eaglesakura.android.margarine.test.R.layout.view_bindtest, null);
        BindTarget dst = new BindTarget();
        MargarineKnife.bind(dst, view);

        // Bindを確認
        assertNotNull(dst.mView);
        assertEquals(dst.mView.getId(), com.eaglesakura.android.margarine.test.R.id.BindTest_View);

        assertNotNull(dst.mTextView);
        assertEquals(dst.mTextView.getId(), com.eaglesakura.android.margarine.test.R.id.BindTest_TextView);

        assertNotNull(dst.mFrameLayout);
        assertEquals(dst.mFrameLayout.getId(), com.eaglesakura.android.margarine.test.R.id.BindTest_FrameLayout);

        assertNotNull(dst.mCheckBox);
        assertEquals(dst.mCheckBox.getId(), com.eaglesakura.android.margarine.test.R.id.BindTest_CheckBox);

        assertEquals(dst.mString, getContext().getString(com.eaglesakura.android.margarine.test.R.string.BindTest_Value_String));
        assertEquals(dst.mInt, getContext().getResources().getInteger(com.eaglesakura.android.margarine.test.R.integer.BindTest_Value_Integer));
        assertTrue(Arrays.equals(dst.mStrings, getContext().getResources().getStringArray(com.eaglesakura.android.margarine.test.R.array.BindTest_Array_Strings)));

        // コールバックを確認
        assertFalse(dst.mClickedView);
        dst.mView.callOnClick();
        dst.mView.performLongClick();
        assertTrue(dst.mClickedView);
        assertTrue(dst.mLongClicked);

        assertFalse(dst.mChecked);
        dst.mCheckBox.setChecked(true);
        assertTrue(dst.mChecked);
    }

    @Test
    public void test_Viewバインドに失敗する() throws Exception {
        try {
            View view = View.inflate(getContext(), com.eaglesakura.android.margarine.test.R.layout.view_bindtest, null);
            MargarineKnife.bind(new ViewBindError(), view);

            fail();
        } catch (MethodBindError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_CheckedChangeバインドに失敗する() throws Exception {
        try {
            View view = View.inflate(getContext(), com.eaglesakura.android.margarine.test.R.layout.view_bindtest, null);
            MargarineKnife.bind(new CheckedChangeBindError(), view);

            fail();
        } catch (MethodBindError e) {
            e.printStackTrace();
        }
    }

    class ViewBindError {
        @OnClick(resName = "BindTest.View")
        void clickView(int dummy) {
        }
    }

    class CheckedChangeBindError {
        @OnCheckedChanged(resName = "BindTest.CheckBox")
        void clickView(CompoundButton view) {
        }
    }

    class NullableBindTarget {
        @Bind(resName = "not_use", nullable = true)
        View mView;
    }

    class NullableBindErrorTarget {
        @Bind(resName = "not_use")
        View mView;
    }

    class BindTarget {
        boolean mClickedView;

        boolean mChecked;

        boolean mLongClicked;

        @Bind(resName = "BindTest.View")
        View mView;

        @Bind(resName = "BindTest.TextView")
        TextView mTextView;

        @Bind(resName = "BindTest.FrameLayout")
        FrameLayout mFrameLayout;

        @Bind(resName = "BindTest.CheckBox")
        CheckBox mCheckBox;

        @BindString(resName = "BindTest.Value.String")
        String mString;

        @BindStringArray(resName = "BindTest_Array_Strings")
        String[] mStrings;

        @BindInt(resName = "BindTest.Value.Integer")
        int mInt;

        @OnClick(resName = "BindTest.View")
        void clickView(View view) {
            Log.d(getClass().getSimpleName(), "clickView(View view)");
            mClickedView = true;
        }

        @OnLongClick(resName = "BindTest.View")
        boolean longClickView(View view) {
            Log.d(getClass().getSimpleName(), "longClickView(View view)");
            mLongClicked = true;
            return true;
        }

        @OnCheckedChanged(resName = "BindTest.CheckBox")
        void checkedChange(boolean isChecked) {
            Log.d(getClass().getSimpleName(), "checkedChange(CompoundButton button, boolean isChecked)");
            mChecked = isChecked;
        }

        @OnClick(resName = "DummyRes", nullable = true)
        void nullableClickMethod() {
            fail();
        }

        @OnLongClick(resName = "DummyRes2", nullable = true)
        void nullableLongClickMethod() {
            fail();
        }

        @OnCheckedChanged(resName = "DummyRes", nullable = true)
        void nullableCheckedkMethod(boolean check) {
            fail();
        }
    }
}
