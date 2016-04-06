package com.eaglesakura.android.garnet;

import com.eaglesakura.android.garnet.error.DefaultConstructorNotFoundError;
import com.eaglesakura.android.garnet.error.InjectTargetError;
import com.eaglesakura.android.garnet.error.InstanceCreateError;
import com.eaglesakura.android.margarine.Lazy;
import com.eaglesakura.android.margarine.UnitTestCase;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class GarnetTest extends UnitTestCase {

    @Test
    public void インスタンスがnewされることを確認する() throws Exception {
        SimpleInstanceCase testInstance = new SimpleInstanceCase();
        assertNull(testInstance.obj);

        Garnet.inject(testInstance);

        assertNotNull(testInstance.obj);
        assertEquals(testInstance.obj.getClass(), SayObject.class);
        assertEquals(testInstance.obj.hello(), "hello");
    }

    @Test
    public void インスタンスがFactory経由でnewされることを確認する() throws Exception {
        FactoryInstanceCase testInstance = new FactoryInstanceCase();
        assertNull(testInstance.obj);

        Garnet.inject(testInstance);

        assertNotNull(testInstance.obj);
        assertNotEquals(testInstance.obj.getClass(), SayObject.class);
        assertEquals(testInstance.obj.hello(), "こんにちは");
    }

    @Test(expected = DefaultConstructorNotFoundError.class)
    public void コンストラクタを持つインスタンスは生成できない() throws Exception {
        ErrorInstanceCase testInstance = new ErrorInstanceCase();
        assertNull(testInstance.obj);

        Garnet.inject(testInstance);

        fail();
    }

    @Test
    public void ファクトリを動的に切り替えられる() throws Exception {
        FactoryInstanceCase testInstance = new FactoryInstanceCase();
        assertNull(testInstance.obj);

        Garnet.override(SayFactory.class, SayFactory2.class);   // 動作クラスを上書きする
        Garnet.inject(testInstance);
        Garnet.override(SayFactory.class, SayFactory.class);    // 動作クラスを元に戻す

        assertNotNull(testInstance.obj);
        assertNotEquals(testInstance.obj.getClass(), SayObject.class);
        assertEquals(testInstance.obj.hello(), "Bonjour");
    }

    @Test
    public void ファクトリがnullを返却することを許容する() throws Exception {
        NullFactoryInstanceCase testInstance = new NullFactoryInstanceCase();
        assertNull(testInstance.obj);

        Garnet.inject(testInstance);
        assertNull(testInstance.obj);
    }

    @Test
    public void 遅延生成が行われる() throws Exception {
        LazyInstanceCase testInstance = new LazyInstanceCase();
        assertNull(testInstance.lazyObj);
        assertNull(testInstance.lazyObj2);

        Garnet.inject(testInstance);

        assertNotNull(testInstance.lazyObj);
        assertNotNull(testInstance.lazyObj2);

        assertEquals(testInstance.lazyObj.get().hello(), "hello");
        assertEquals(testInstance.lazyObj2.get().hello(), "こんにちは");
    }

    @Test(expected = DefaultConstructorNotFoundError.class)
    public void Lazyの場合でもコンストラクタミスの場合はバインド時点で失敗する() {
        LazyErrorInstanceCase testInstance = new LazyErrorInstanceCase();
        assertNull(testInstance.lazyObj);

        Garnet.inject(testInstance);
        fail(); // 成功してはならない
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

    public static class SayFactory implements ComponentFactory<SayObject, Object> {
        @Override
        public SayObject newInstance(Field field, Object o) {
            return new SayObject() {
                @Override
                public String hello() {
                    return "こんにちは";
                }
            };
        }

        @Override
        public void initialize(Field field, Object o, SayObject obj) {

        }
    }

    public static class SayNullFactory implements ComponentFactory<SayObject, Object> {
        @Override
        public SayObject newInstance(Field field, Object o) {
            return null;
        }

        @Override
        public void initialize(Field field, Object o, SayObject obj) {

        }
    }

    public static class SayFactory2 implements ComponentFactory<SayObject, Object> {
        @Override
        public SayObject newInstance(Field field, Object o) {
            return new SayObject() {
                @Override
                public String hello() {
                    return "Bonjour";
                }
            };
        }

        @Override
        public void initialize(Field field, Object o, SayObject obj) {

        }
    }

    public static class LazyInstanceCase {
        @Inject(instance = SayObject.class)
        Lazy<SayObject> lazyObj;

        @Inject(factory = SayFactory.class)
        Lazy<SayObject> lazyObj2;
    }

    public static class LazyErrorInstanceCase {
        @Inject(instance = Say2Object.class)
        Lazy<SayObject> lazyObj;
    }

    public static class SimpleInstanceCase {
        @Inject(instance = SayObject.class)
        SayObject obj;
    }

    public static class FactoryInstanceCase {
        @Inject(factory = SayFactory.class)
        SayObject obj;
    }

    public static class NullFactoryInstanceCase {
        @Inject(factory = SayNullFactory.class)
        SayObject obj;
    }


    public static class ErrorInstanceCase {
        @Inject(instance = Say2Object.class)
        SayObject obj;
    }
}
