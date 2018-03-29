package com.javatechnics.jpa.repository.testrepos;

import com.javatechnics.jpa.repository.annotation.Query;
import com.javatechnics.jpa.repository.annotation.Repository;

@Repository
public interface NoAnnotatedMethodParameters
{
    @Query("")
    int getObjectusingUmltipleParams(Long id, String name);

    @Query("")
    Object getObjectBySingleParameter(Long id);
}
