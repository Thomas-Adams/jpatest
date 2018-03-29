package com.javatechnics.jpa.repository.testrepos;

import com.javatechnics.jpa.repository.annotation.Repository;

import java.util.List;

@Repository
public interface NoAnnotatedMethods
{
    public List<Long> getAllByName(String name);
}
