package com.eaglesakura.android.margarine;

import org.junit.Test;

import android.support.annotation.Nullable;

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

    @Test
    public void ファクトリを動的に切り替えられる() throws Exception {
        FactoryInstanceCase testInstance = new FactoryInstanceCase();
        assertNull(testInstance.obj);

        MargarineKnife.override(SayFactory.class, SayFactory2.class);   // 動作クラスを上書きする
        MargarineKnife.bind(testInstance, this);
        MargarineKnife.override(SayFactory.class, SayFactory.class);    // 動作クラスを元に戻す

        assertNotNull(testInstance.obj);
        assertNotEquals(testInstance.obj.getClass(), SayObject.class);
        assertEquals(testInstance.obj.hello(), "Bonjour");
    }

    @Test
    public void ファクトリがnullを返却することを許容する() throws Exception {
        NullFactoryInstanceCase testInstance = new NullFactoryInstanceCase();
        assertNull(testInstance.obj);

        MargarineKnife.bind(testInstance, this);
        assertNull(testInstance.obj);
    }

    @Test(expected = InstanceCreateFailedException.class)
    public void ファクトリがnullを返却することを許容しない() throws Exception {
        NonNullFactoryInstanceCase testInstance = new NonNullFactoryInstanceCase();
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

    public static class SayNullFactory implements InjectionFactory<SayObject> {
        @Override
        public SayObject newInstance(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst, Field field) {
            return null;
        }
    }

    public static class SayFactory2 implements InjectionFactory<SayObject> {
        @Override
        public SayObject newInstance(InjectionClass srcClass, Object src, InjectionClass dstClass, Object dst, Field field) {
            return new SayObject() {
                @Override
                public String hello() {
                    return "Bonjour";
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

    public static class NullFactoryInstanceCase {
        @BindInstance(factory = SayNullFactory.class, nullable = true)
        SayObject obj;
    }

    public static class NonNullFactoryInstanceCase {
        @BindInstance(factory = SayNullFactory.class)
        SayObject obj;
    }

    public static class ErrorInstanceCase {
        @BindInstance(Say2Object.class)
        SayObject obj;
    }
}
