package com.javatechnics.jpa.commands;

import com.javatechnics.jpa.dao.BookServiceDao;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "book", name = "create", description = "Creates a book.")
@Service
public class CreateBook implements Action
{
    @Reference
    private BookServiceDao bookService;

    @Argument(name = "title")
    String title;

    @Argument(index = 1, name = "author")
    String author;

    @Override
    public Object execute() throws Exception
    {
        return bookService.createBook(title, author);
    }
}
