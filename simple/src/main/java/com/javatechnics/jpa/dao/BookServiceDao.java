package com.javatechnics.jpa.dao;

import com.javatechnics.jpa.Book;

import java.util.Collection;

public interface BookServiceDao
{
    Collection<Book> getBooks();

    Book getBook(Long id);

    Collection<Book> getBooksByAuthor(String author);

    Collection<Book> getBooksByTitle(String title);

    Book createBook(String title, String author);
}
