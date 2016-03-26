package com.eaglesakura.android.margarine;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class InjectionClassTest extends UnitTestCase {
    @Test
    public void メソッド名からContextが得られる() {
        assertNotNull(new InjectionClass(getClass()).getContext(this));
    }

}
