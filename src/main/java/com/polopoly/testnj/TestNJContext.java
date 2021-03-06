package com.polopoly.testnj;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.junit.rules.TestRule;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class TestNJContext {


    private static Set<TestCallbacks> callbacks =
            new HashSet<TestCallbacks>();

    protected Injector init() {
        List<Module> modules = new ArrayList<Module>();
        initModulesFromServiceLoader(modules);

        modules.add(new Module() {
            @Override
            public void configure(Binder binder) {
                Multibinder<TestCallbacks> testHooksBindings =
                        Multibinder.newSetBinder(binder, TestCallbacks.class);
                for (TestCallbacks callback : callbacks) {
                    testHooksBindings.addBinding().toInstance(callback);
                }

                binder.bind(TestCallbacks.class).to(CompositeTestCallbacks.class);

                // Make sure there is at least an empty set of rules
                Multibinder.newSetBinder(binder, TestRule.class);
            }
        });
        return Guice.createInjector(modules);
    }

    private void initModulesFromServiceLoader(List<Module> modules) {
        ServiceLoader<Module> serviceLoader = ServiceLoader.load(Module.class);
        for (Module module : serviceLoader) {
            modules.add(module);
        }
    }

    public static void addCallback(TestCallbacks callback) {
        callbacks.add(callback);
    }


}
