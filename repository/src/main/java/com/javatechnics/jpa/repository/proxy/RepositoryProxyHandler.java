package com.javatechnics.jpa.repository.proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositoryProxyHandler implements InvocationHandler
{
    private static final Logger LOG = Logger.getLogger("RepositoryProxyHandler");

    private final EntityManager entityManager;

    private final Map<Method, String> methodQueries;

    private final Map<Method, List<String>> methodQueryParameters;

    public RepositoryProxyHandler(final EntityManager entityManagerService, final Map<Method, String> methodQueries, final Map<Method, List<String>> methodQueryParameters)
    {
        this.entityManager = entityManagerService;
        this.methodQueries = methodQueries;
        this.methodQueryParameters = methodQueryParameters;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        Object returnObject = null;
        if (methodQueries.containsKey(method))
        {
            returnObject = this.getResultList(methodQueries.get(method), methodQueryParameters.get(method), args);
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

    private Object getSingleResult(final String qlString, final List<String> parameterNames, final Object[] parameters)
    {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Query query = entityManager.createQuery(qlString);
        if (parameters != null)
        {
            for (int parameterCount = 0; parameterCount < parameters.length; parameterCount++)
            {
                query.setParameter(parameterNames.get(parameterCount), parameters[parameterCount]);
            }
        }
        Object result = query.getSingleResult();
        transaction.commit();
        return result;
    }

    private List<?> getResultList(final String qlString, final List<String> parameterNames, final Object[] parameters)
    {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Query query = entityManager.createQuery(qlString);
        if (parameters != null)
        {
            for (int parameterCount = 0; parameterCount < parameters.length; parameterCount++)
            {
                query.setParameter(parameterNames.get(parameterCount), parameters[parameterCount]);
            }
        }
        List<?> result = query.getResultList();
        transaction.commit();
        return result;
    }
}
