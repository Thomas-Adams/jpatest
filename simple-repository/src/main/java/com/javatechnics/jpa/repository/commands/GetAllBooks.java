package com.javatechnics.jpa.repository.commands;

import com.javatechnics.jpa.Book;
import com.javatechnics.jpa.repository.BookRepository;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

@Service
@Command(scope = "book", name = "all", description = "Gets all books.")
public class GetAllBooks implements Action
{
    @Reference
    private BookRepository bookRepository;

    @Override
    public Object execute() throws Exception
    {
        List<Book> results = bookRepository.getAllBooks();
        if (results.isEmpty())
        {
            System.out.println("There are no books in the DB.");
        }
        else
        {
            for (final Book book : results)
            {
                System.out.println("ID: " + book.getId() + " TITLE: " + book.getTitle() + " AUTHOR: " + book.getAuthor());
            }
        }
        return null;
    }
}
