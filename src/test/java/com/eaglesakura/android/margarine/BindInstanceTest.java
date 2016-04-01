package com.eaglesakura.android.margarine;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class BindInstanceTest extends UnitTestCase {

    @Test
    public void インスタンスがnewされることを確認する() throws Exception {
        SimpleInstanceCase testInstance = new SimpleInstanceCase();
        assertNull(testInstance.obj);

        MargarineKnife.bind(testInstance, this);

        assertNotNull(testInstance.obj);
        assertEquals(testInstance.obj.getClass(), SayObject.class);
        assertEquals(testInstance.obj.hello(), "hello");
    }

    @Test
    public void インスタンスがFactory経由でnewされることを確認する() throws Exception {
        FactoryInstanceCase testInstance = new FactoryInstanceCase();
        assertNull(testInstance.obj);

        MargarineKnife.bind(testInstance, this);

        assertNotNull(testInstance.obj);
        assertNotEquals(testInstance.obj.getClass(), SayObject.class);
        assertEquals(testInstance.obj.hello(), "こんにちは");
    }

    @Test(expected = ResourceBindError.class)
    public void コンストラクタを持つインスタンスは生成できない() throws Exception {
        ErrorInstanceCase testInstance = new ErrorInstanceCase();
        assertNull(testInstance.obj);

        MargarineKnife.bind(testInstance, this);

        fail();
    }

    public static class SayObject {
        public String hello() {
            return "hello";
        }
    }

    public static class Say2Object extends SayObject {
        String say;

        public Say2Object(String say) {
            this.say = say;
        }

        @Override
        public String hello() {
            return say;
        }
    }

    public static class SayFactory implements InjectionFactory<SayObject> {
        @Override
        public SayObject newInstance(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst, Field field) {
            return new SayObject() {
                @Override
                public String hello() {
                    return "こんにちは";
                }
            };
        }
    }

    public static class SimpleInstanceCase {
        @BindInstance(SayObject.class)
        SayObject obj;
    }

    public static class FactoryInstanceCase {
        @BindInstance(factory = SayFactory.class)
        SayObject obj;
    }

    public static class ErrorInstanceCase {
        @BindInstance(Say2Object.class)
        SayObject obj;
    }
}
