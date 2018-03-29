package com.javatechnics.jpa.repository.testrepos;

import com.javatechnics.jpa.repository.annotation.Param;
import com.javatechnics.jpa.repository.annotation.Query;
import com.javatechnics.jpa.repository.annotation.Repository;

import java.util.List;

@Repository
public interface OneAnnotatedMethodAndOneAnnotatedParam
{
    @Query("select * from Objects object where object.name = :name")
    List<Object> getObjectsBySingleName(Object randomObject, @Param("name") String name, List<Object> randomObjects);

    List<Object> randomUnannotatedMethod(Long id, String description);

    List<Object> randomUnannotatedMethodWithAnnotatedParameter(Long id, @Param("name") String name);
}
