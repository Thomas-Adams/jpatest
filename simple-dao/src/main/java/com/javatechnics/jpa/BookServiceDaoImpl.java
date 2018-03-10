package com.javatechnics.jpa;

import com.javatechnics.jpa.dao.BookServiceDao;
import com.javatechnics.jpa.dao.EntityManagerService;

import javax.persistence.*;
import java.util.Collection;

public class BookServiceDaoImpl implements BookServiceDao, EntityManagerService
{
    @PersistenceContext(unitName = "test")
    private EntityManager manager;

    public Collection<Book> getBooks()
    {
        final EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        Collection<Book> books = manager.createQuery("select book from Book book", Book.class).getResultList();
        transaction.commit();
        return books;
    }

    public Book getBook(final Long id)
    {
        final EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        final Book book = manager.createQuery("select book from Book book where book.id = :id", Book.class).getSingleResult();
        transaction.commit();
        return book;
    }

    public Collection<Book> getBooksByAuthor(final String author)
    {
        final EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        final Collection<Book> books = manager.createQuery("select book from Book book where book.author = :author", Book.class).getResultList();
        transaction.commit();
        return books;
    }

    public Collection<Book> getBooksByTitle(final String title)
    {
        final EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        final Collection<Book> books = manager.createQuery("select book from Book book where book.title = :title", Book.class).getResultList();
        transaction.commit();
        return books;
    }

    public Book createBook(final String title, final String author)
    {
        final EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        final Book book = new Book(title, author);
        manager.persist(book);
        transaction.commit();
        return book;
    }

    @Override
    public EntityManager getEntityManager()
    {
        return this.manager;
    }
}
