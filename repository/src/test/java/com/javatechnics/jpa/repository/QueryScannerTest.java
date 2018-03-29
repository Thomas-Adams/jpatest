package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.repository.testrepos.NoAnnotatedMethodParameters;
import com.javatechnics.jpa.repository.testrepos.OneAnnotatedMethodAndOneAnnotatedParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class QueryScannerTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundle;

    @Mock
    private BundleWiring bundleWiring;

    private final Class<?> clazz;
    private final String[] packages;
    private final List<String> annotatedMethodNames;

    @Parameterized.Parameters
    public static Iterable<Object[]> data()
    {
        return Arrays.asList(new Object[][]
                {
                        {
                                NoAnnotatedMethodParameters.class, new String[]{NoAnnotatedMethodParameters.class.getPackage().getName()},
                                Arrays.asList("getObjectusingUmltipleParams", "getObjectBySingleParameter")
                        },
                        {
                                OneAnnotatedMethodAndOneAnnotatedParam.class, new String[]{OneAnnotatedMethodAndOneAnnotatedParam.class.getPackage().getName()},
                                Arrays.asList("getObjectsBySingleName"),
                        }
                });
    }

    public QueryScannerTest(final Class<?> clazz, final String[] packages, final List<String> annotatedMethodNames)
    {
        MockitoAnnotations.initMocks(this);
        this.clazz = clazz;
        this.packages = packages;
        this.annotatedMethodNames = annotatedMethodNames;
    }

    @Test
    public void testAnnotatedMethodsAreExtracted() throws Exception
    {
        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.adapt(BundleWiring.class)).thenReturn(bundleWiring);
        when(bundleWiring.listResources(any(), any(), anyInt())).thenReturn(Arrays.asList(clazz.getCanonicalName() + ".class"));
        doReturn(clazz).when(bundle).loadClass(clazz.getCanonicalName());

        final Map<Class<?>, Map<Method, String>> methodQueries = QueryScanner.scanForQueries(packages, bundleContext);

        assertTrue("Incorrect number of entries in method queries.", methodQueries.size() == 1);

        for (final Map.Entry<Class<?>, Map<Method, String>> repoMethodQueries : methodQueries.entrySet())
        {
            assertEquals("Key NOT the expected class.", repoMethodQueries.getKey(), clazz);
            final List<String> extractedMethodNames = repoMethodQueries.getValue().keySet().stream().map(method -> method.getName()).collect(Collectors.toList());
            assertTrue("Not all or none expected @Query annotated methods in extracted list.", annotatedMethodNames.containsAll(extractedMethodNames));
        }
    }
}