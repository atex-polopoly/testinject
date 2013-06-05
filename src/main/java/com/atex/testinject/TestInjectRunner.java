package com.atex.testinject;


import com.google.inject.Injector;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class TestInjectRunner extends BlockJUnit4ClassRunner {

    private static Injector injector;
    private static Object initLock = new Object();

    private static TestHooks hooks;

    public TestInjectRunner(Class<?> klass) throws InitializationError {
        super(klass);
        initInjectorIfNecessary();
    }

    private void initInjectorIfNecessary() {
        synchronized (initLock) {
            if (injector == null) {
                injector = new TestContext().init();
                hooks = injector.getInstance(TestHooks.class);
            }
        }
    }

    @Override
    protected Object createTest() throws Exception {
        Object obj = super.createTest();
        injector.injectMembers(obj);
        return obj;
    }


    @SuppressWarnings("deprecation")
    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        Statement returnStatement = super.withBefores(method, target, statement);
        hooks.before();
        return returnStatement;
    }


    @SuppressWarnings("deprecation")
    @Override
    protected Statement withAfters(FrameworkMethod method, Object target, Statement statement) {
        Statement returnStatement = super.withAfters(method, target, statement);
        hooks.after();
        return returnStatement;
    }

}
