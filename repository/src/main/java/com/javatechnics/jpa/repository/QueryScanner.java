package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.repository.annotation.Param;
import com.javatechnics.jpa.repository.annotation.Query;
import com.javatechnics.jpa.repository.annotation.Repository;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class QueryScanner
{
    private static final String CLASS_SUFFIX = ".class";
    private static final String CLASS_FILTER = "*.class";
    private static final Logger LOG = Logger.getLogger(QueryScanner.class.getName());

    public static Map<Class<?>, Map<Method, String>> scanForQueries(final String[] packages, BundleContext bundleContext)
    {
        final Map<Class<?>, Map<Method, String>> queries = new HashMap<>();
        final List<Class> interfaces = getClasses(packages, bundleContext).stream()
                .filter(aClass -> aClass.isInterface())
                .collect(Collectors.toList());
        for (final Class clazz : interfaces)
        {
            final Map<Method, String> classMethods = new HashMap<>();
            queries.put(clazz, classMethods);
            Repository repositoryAnnotation = (Repository) clazz.getAnnotation(Repository.class);
            if (repositoryAnnotation != null)
            {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods)
                {
                    Query queryAnnotation = method.getAnnotation(Query.class);
                    if (queryAnnotation != null)
                    {
                        classMethods.put(method, queryAnnotation.value());
                        LOG.log(Level.INFO, "Method: " + method.getName() + " Query: " + queryAnnotation.value());
                    }
                }
            }
        }
        return queries;
    }

    private static List<Class> getClasses(String[] packages, BundleContext bundleContext)
    {
        List<Class> classes = new ArrayList<>();
        Bundle bundle = bundleContext.getBundle();

        for (String repoPackage : packages)
        {
            final String path = repoPackage.replace(".", "/");
            final BundleWiring wiring = bundle.adapt(BundleWiring.class);
            final Collection<String> resources = wiring.listResources(path, CLASS_FILTER, BundleWiring.LISTRESOURCES_LOCAL);
            classes.addAll(
                    resources.stream()
                            .map(file ->
                            {
                                Class<?> clazz = null;
                                try
                                {
                                    final String className = file.replace("/", ".").substring(0, file.length() - CLASS_SUFFIX.length());
                                    clazz = bundle.loadClass(className);
                                }
                                catch (ClassNotFoundException e)
                                {
                                    LOG.log(Level.WARNING, e.getMessage());
                                }
                                return clazz;
                            })
                            .collect(Collectors.toList()));
        }
        return classes;
    }

}
