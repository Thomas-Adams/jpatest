package com.javatechnics.jpa.commands;

import com.javatechnics.jpa.Book;
import com.javatechnics.jpa.dao.BookServiceDao;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.Collection;

@Command(scope = "book", name = "getall", description = "Gets all books")
@Service
public class GetBooks implements Action
{
    @Reference
    private BookServiceDao bookService;

    @Override
    public Object execute() throws Exception
    {
        Collection<Book> books = bookService.getBooks();
        for (Book book : books)
        {
            System.out.println("ID: " + book.getId() + ", AUTHOR: " + book.getAuthor() + ", TITLE: " + book.getTitle());
        }
        return null;
    }
}
