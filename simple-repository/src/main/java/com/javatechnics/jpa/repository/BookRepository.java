package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.Book;
import com.javatechnics.jpa.annotation.Query;
import com.javatechnics.jpa.annotation.Repository;

import java.util.List;

@Repository
public interface BookRepository
{
    @Query(value = "select book from Book book where book.id = :id")
    List<Book> getBook(Long id);

    @Query(value = "select book from Book book where book.name = :author and book.title = :title")
    List<Book> getBookByAuthorAndTitle(String author, String title);

    @Query(value = "select book from Book book")
    List<Book> getAllBooks();
}
