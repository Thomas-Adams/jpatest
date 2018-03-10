package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.annotation.Query;
import com.javatechnics.jpa.annotation.Repository;

@Repository
public interface TestRepository
{
    @Query
    void getBook(Long id);
}
