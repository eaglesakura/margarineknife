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
        assertEquals(binders.size(), 2);
    }

    public void test_アノテーションにバインドを行う() {
        View view = View.inflate(getContext(), com.eaglesakura.android.margarine.test.R.layout.view_bindtest, null);
        BindTarget dst = new BindTarget();
        MargarineKnife.bind(view, dst);

        // Bindを確認
        assertNotNull(dst.mView);
        assertEquals(dst.mView.getId(), com.eaglesakura.android.margarine.test.R.id.BindTest_View);

        assertNotNull(dst.mTextView);
        assertEquals(dst.mTextView.getId(), com.eaglesakura.android.margarine.test.R.id.BindTest_TextView);

        assertNotNull(dst.mFrameLayout);
        assertEquals(dst.mFrameLayout.getId(), com.eaglesakura.android.margarine.test.R.id.BindTest_FrameLayout);

        assertNotNull(dst.mCheckBox);
        assertEquals(dst.mCheckBox.getId(), com.eaglesakura.android.margarine.test.R.id.BindTest_CheckBox);

        assertEquals(dst.mString, getContext().getString(R.string.BindTest_Value_String));
        assertEquals(dst.mInt, getContext().getResources().getInteger(R.integer.BindTest_Value_Integer));

        // コールバックを確認
        assertFalse(dst.mClickedView);
        dst.mView.callOnClick();
        assertTrue(dst.mClickedView);

        assertFalse(dst.mChecked);
        dst.mCheckBox.setChecked(true);
        assertTrue(dst.mChecked);
    }

    class BindTarget {
        boolean mClickedView;

        boolean mChecked;

        @BindRes(resName = "BindTest.View")
        View mView;

        @BindRes(resName = "BindTest.TextView")
        TextView mTextView;

        @BindRes(resName = "BindTest.FrameLayout")
        FrameLayout mFrameLayout;

        @BindRes(resName = "BindTest.CheckBox")
        CheckBox mCheckBox;

        @BindRes(resName = "BindTest.Value.String")
        String mString;

        @BindRes(resName = "BindTest.Value.Integer")
        int mInt;

        @OnClick(resName = "BindTest.View")
        void clickView(View view) {
            LogUtil.log("clickView(View view)");
            mClickedView = true;
        }

        @OnCheckedChange(resName = "BindTest.CheckBox")
        void checkedChange(CompoundButton button, boolean isChecked) {
            LogUtil.log("checkedChange(CompoundButton button, boolean isChecked)");
            mChecked = isChecked;
        }
    }
}
