package com.javatechnics.jpa;

import com.javatechnics.jpa.dao.EntityManagerService;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class EntityManagerServiceImpl implements EntityManagerService
{
    @PersistenceContext(unitName = "test")
    private EntityManager entityManager;


    @Override
    public Query createNativeQuery(final String sqlString)
    {
        return entityManager.createNativeQuery(sqlString);
    }

    @Override
    public Query createQuery(final String qlString)
    {
        return entityManager.createQuery(qlString);
    }

    @Override
    public Object getSingleResult(final String qlString, final List<String> parameterNames, final Object[] parameters)
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

    @Override
    public List<?> getResultList(final String qlString, final List<String> parameterNames, final Object[] parameters)
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
