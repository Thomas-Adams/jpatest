package com.javatechnics.jpa.repository;

import javax.persistence.EntityManager;

public class QueryScanner
{
    private final EntityManager entityManager;

    public QueryScanner(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public void scanForQueries()
    {

    }
}
