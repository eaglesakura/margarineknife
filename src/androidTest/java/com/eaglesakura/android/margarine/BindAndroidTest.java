package com.eaglesakura.android.margarine;

import com.eaglesakura.android.devicetest.ModuleTestCase;
import com.eaglesakura.util.LogUtil;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

@SuppressLint("all")
public class BindAndroidTest extends ModuleTestCase {

    public void test_Inject対象フィールド一覧を取得できる() throws Exception {
        InjectionClass target = new InjectionClass(BindTarget.class);
        List<FieldBinder> fields = target.listBindFields(getContext());
        assertNotNull(fields);
        assertEquals(fields.size(), 6);
    }

    public void test_Inject対象メソッド一覧を取得できる() throws Exception {
        InjectionClass target = new InjectionClass(BindTarget.class);
        List<MethodBinder> binders = target.listBindMethods(getContext());

        assertNotNull(binders);
        assertEquals(binders.size(), 3);
    }

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

        @BindInt(resName = "BindTest.Value.Integer")
        int mInt;

        @OnClick(resName = "BindTest.View")
        void clickView(View view) {
            LogUtil.log("clickView(View view)");
            mClickedView = true;
        }

        @OnLongClick(resName = "BindTest.View")
        boolean longClickView(View view) {
            LogUtil.log("longClickView(View view)");
            mLongClicked = true;
            return true;
        }

        @OnCheckedChanged(resName = "BindTest.CheckBox")
        void checkedChange(CompoundButton button, boolean isChecked) {
            LogUtil.log("checkedChange(CompoundButton button, boolean isChecked)");
            mChecked = isChecked;
        }

    }
}
