package com.javatechnics.jpa.repository;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Activator implements BundleActivator
{
    private ServiceRegistration<TestRepository> testRepositoryServiceRegistration;

    private TestRepository testRepositoryProxy;

    public void start(final BundleContext bundleContext) throws Exception
    {
        final MyInvocationHandler handler = new MyInvocationHandler();
        testRepositoryProxy = (TestRepository) Proxy.newProxyInstance(MyInvocationHandler.class.getClassLoader(),
                new Class[]{TestRepository.class},
                handler);
        testRepositoryServiceRegistration = bundleContext.registerService(TestRepository.class, testRepositoryProxy, null);
    }

    public void stop(final BundleContext bundleContext) throws Exception
    {
        if (testRepositoryServiceRegistration != null)
        {
            testRepositoryServiceRegistration.unregister();
        }
    }

    private class MyInvocationHandler implements InvocationHandler
    {
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
        {
            //TODO: implement handling of the three methods, hashCode, equals & clone(?)
            System.out.println("This is the Proxy Handler being called. Method: " + method.getName());

            return this.hashCode();
        }
    }
}
