package com.javatechnics.jpa.commands;

import com.javatechnics.jpa.repository.TestRepository;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "book", name = "repo", description = "Tests the Book Repository")
@Service
public class RepoTest implements Action
{
    @Reference
    private TestRepository testRepository;

    public Object execute() throws Exception
    {
        testRepository.getBook(1L);
        return null;
    }
}
