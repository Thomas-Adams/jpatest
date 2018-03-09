package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.repository.annotation.Repository;

@Repository
public interface TestRepository
{
    void getBook(Long id);
}
