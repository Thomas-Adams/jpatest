package com.javatechnics.jpa.dao;

import javax.persistence.Query;
import java.util.List;

public interface EntityManagerService
{
    Query createNativeQuery(String sqlString);

    Query createQuery(String qlString);

    Object getSingleResult(String qlString, List<String> parameterNames, Object[] parameters);

    List<?> getResultList(String qlString, List<String> parameterNames, Object[] parameters);
}
