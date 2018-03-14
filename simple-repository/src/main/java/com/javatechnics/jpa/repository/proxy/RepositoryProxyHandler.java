package com.javatechnics.jpa.repository.proxy;

import com.javatechnics.jpa.dao.EntityManagerService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositoryProxyHandler implements InvocationHandler
{
    private static final Logger LOG = Logger.getLogger("RepositoryProxyHandler");

    final EntityManagerService entityManagerService;

    final Map<Method, String> methodQueries;

    final Map<Method, List<String>> methodQueryParameters;

    public RepositoryProxyHandler(final EntityManagerService entityManagerService, final Map<Method, String> methodQueries, final Map<Method, List<String>> methodQueryParameters)
    {
        this.entityManagerService = entityManagerService;
        this.methodQueries = methodQueries;
        this.methodQueryParameters = methodQueryParameters;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        Object returnObject = null;
        if (methodQueries.containsKey(method))
        {
            returnObject = entityManagerService.getResultList(methodQueries.get(method), methodQueryParameters.get(method), args);
        }
        else
        {
            switch (method.getName())
            {
                case "hashCode":
                    LOG.log(Level.INFO, "hashCode() method called.");
                    returnObject = this.hashCode();
                    break;
                case "toString":
                    LOG.log(Level.INFO, "toString() method called.");
                    returnObject = this.toString();
                    break;
                case "equals":
                    LOG.log(Level.INFO, "equals() method called.");
                    returnObject = this.equals(args);
                    break;
                default:
                    LOG.log(Level.INFO, "Unrecognised method name called: " + method.getName());
            }
        }

        return returnObject;
    }
}
