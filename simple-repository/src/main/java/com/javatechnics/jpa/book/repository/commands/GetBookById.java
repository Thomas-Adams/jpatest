package com.javatechnics.jpa.book.repository.commands;

import com.javatechnics.jpa.Book;
import com.javatechnics.jpa.book.repository.BookRepository;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

@Command(scope = "book", name = "getid", description = "Gets a book by ID.")
@Service
public class GetBookById implements Action
{
    @Reference
    private BookRepository bookRepository;

    @Argument(name = "ID", description = "The ID of the book to return.")
    private Long id;

    public Object execute() throws Exception
    {
        List<Book> result = bookRepository.getBook(id);
        if (result.isEmpty())
        {
            System.out.println("Could not find book for ID: " + id);
        }
        else
        {
            final Book book = result.get(0);
            System.out.println("ID: " + id + " TITLE: " + book.getTitle() + " AUTHOR: " + book.getAuthor());
        }

        return null;
    }
}
