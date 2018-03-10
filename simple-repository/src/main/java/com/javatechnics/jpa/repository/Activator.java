package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.dao.EntityManagerService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Dictionary;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Activator
{
    @Reference
    EntityManagerService entityManagerService;

    private ServiceRegistration<TestRepository> testRepositoryServiceRegistration;

    private TestRepository testRepositoryProxy;

    private static final String REPOSITORY_MANIFEST_HEADER = "Repository-Package";

    private static Logger LOG = Logger.getLogger("RepositoryActivator");

    @Activate
    public void activate(final BundleContext bundleContext) throws Exception
    {
        final Dictionary<String, String> headers = bundleContext.getBundle().getHeaders();
        String repositoryPackage = headers.get(REPOSITORY_MANIFEST_HEADER);
        if (repositoryPackage == null || repositoryPackage.isEmpty())
        {
            LOG.log(Level.INFO, "Repository packages not defined.");
        }
        else
        {
            LOG.log(Level.INFO, "Repository package is: " + repositoryPackage);
        }

        final MyInvocationHandler handler = new MyInvocationHandler();
        testRepositoryProxy = (TestRepository) Proxy.newProxyInstance(MyInvocationHandler.class.getClassLoader(),
                new Class[]{TestRepository.class},
                handler);
        testRepositoryServiceRegistration = bundleContext.registerService(TestRepository.class, testRepositoryProxy, null);
    }

    @Deactivate
    public void deactivate(final BundleContext bundleContext) throws Exception
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
            Object returnObject = null;
            switch (method.getName())
            {
                case "getBook":
                    LOG.log(Level.INFO, "getBook() method called.");
                    break;
                case "hashCode":
                    LOG.log(Level.INFO, "hashCode() method called.");
                    returnObject = this.hashCode();
                    break;
                default:
                    LOG.log(Level.INFO, "Unrecognised method name called: " + method.getName());
            }

            return returnObject;
        }
    }
}
