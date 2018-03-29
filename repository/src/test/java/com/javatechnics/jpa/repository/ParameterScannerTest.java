package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.repository.testrepos.NoAnnotatedMethodParameters;
import com.javatechnics.jpa.repository.testrepos.NoAnnotatedMethods;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ParameterScannerTest
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
    private final List<List<String>> annotatedMethodParameters;
    private final List<List<String>> EXPECTED_QUERY_PARAMS;

    @Parameterized.Parameters
    public static Iterable<Object[]> data()
    {
        return Arrays.asList(new Object[][]
                {
                        {
                                NoAnnotatedMethodParameters.class, new String[]{NoAnnotatedMethodParameters.class.getPackage().getName()},
                                Arrays.asList("getObjectusingUmltipleParams", "getObjectBySingleParameter"),
                                Arrays.asList(new ArrayList<String>(), new ArrayList<String>()),
                                Arrays.asList(new ArrayList<String>(), new ArrayList<String>())
                        },
                        {
                                NoAnnotatedMethods.class, new String[]{NoAnnotatedMethods.class.getPackage().getName()},
                                new ArrayList<String>(),
                                new ArrayList<List<String>>(),
                                new ArrayList<List<String>>()
                        },
                        {
                                OneAnnotatedMethodAndOneAnnotatedParam.class, new String[] {OneAnnotatedMethodAndOneAnnotatedParam.class.getPackage().getName()},
                                Arrays.asList("getObjectsBySingleName"),
                                Arrays.asList(Arrays.asList("name")),
                                Arrays.asList(Arrays.asList("name"))
                        }
                });
    }

    public ParameterScannerTest(final Class<?> clazz, final String[] packages, final List<String> annotatedMethodNames,
                                final List<List<String>> annotatedMethodParameters, final List<List<String>> queryParameters)
    {
        MockitoAnnotations.initMocks(this);
        this.clazz = clazz;
        this.packages = packages;
        this.annotatedMethodNames = annotatedMethodNames;
        this.annotatedMethodParameters = annotatedMethodParameters;
        this.EXPECTED_QUERY_PARAMS = queryParameters;
    }

    /*
    1. Class name
    2. Names of annotated methods
    3.
     */

    @Test
    public void testQueryScannerFindsCorrectNumberOfQueryAnnotatedMethods() throws ClassNotFoundException
    {
        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.adapt(BundleWiring.class)).thenReturn(bundleWiring);
        when(bundleWiring.listResources(any(), any(), anyInt())).thenReturn(Arrays.asList(clazz.getCanonicalName() + ".class"));
        doReturn(clazz).when(bundle).loadClass(clazz.getCanonicalName());

        final Map<Class<?>, Map<Method, String>> classQueries =
                QueryScanner.scanForQueries(packages, bundleContext);
        assertTrue(classQueries.size() == 1);
        assertTrue("Incorrect number of @Query annotated methods found.",
                classQueries.get(clazz).size() == annotatedMethodNames.size());
    }

    @Test
    public void testParameterScannerFindsCorrectNumberOfAnnotatedParams() throws ClassNotFoundException
    {
        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.adapt(BundleWiring.class)).thenReturn(bundleWiring);
        when(bundleWiring.listResources(any(), any(), anyInt())).thenReturn(Arrays.asList(clazz.getCanonicalName() + ".class"));
        doReturn(clazz).when(bundle).loadClass(clazz.getCanonicalName());

        final Map<Class<?>, Map<Method, String>> classQueries =
                QueryScanner.scanForQueries(packages, bundleContext);
        assertTrue("Specified class is not in class queries map.", classQueries.containsKey(clazz));
        final Map<Method, Map<String, String>> methodParameters = ParameterScanner.scanMethodsForParameters(classQueries.get(clazz));

        for (Map.Entry<Method, Map<String, String>> methodMap : methodParameters.entrySet())
        {
            assertTrue("Extracted method name NOT in the expected annotated method list.", annotatedMethodNames.contains(methodMap.getKey().getName()));
            assertEquals("Extracted parameter count NOT the expected parameter count.",
                    annotatedMethodParameters.get(annotatedMethodNames.indexOf(methodMap.getKey().getName())).size(),
                    methodMap.getValue().size());
        }
    }

    @Test
    public void testParameterScannerFindsCorrectNumberOfParams() throws ClassNotFoundException
    {
        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.adapt(BundleWiring.class)).thenReturn(bundleWiring);
        when(bundleWiring.listResources(any(), any(), anyInt())).thenReturn(Arrays.asList(clazz.getCanonicalName() + ".class"));
        doReturn(clazz).when(bundle).loadClass(clazz.getCanonicalName());

        final Map<Class<?>, Map<Method, String>> classQueries = QueryScanner.scanForQueries(packages, bundleContext);
        final Map<Method, List<String>> query = ParameterScanner.scanQueryForParameters(classQueries.get(clazz));

        for (Map.Entry<Method, List<String>> methodMap : query.entrySet())
        {
            final List<String> expectedParameters = EXPECTED_QUERY_PARAMS.get(annotatedMethodNames.indexOf(methodMap.getKey().getName()));
            final List<String> extractedParameters = methodMap.getValue();
            final int extractedParameterCount = extractedParameters.size();
            final int expectedParameterCount = expectedParameters.size();
            assertEquals("Extracted parameter count from QUERY string not equal to expected count.",
                    expectedParameterCount, extractedParameterCount);
        }
    }

    @Test
    public void testParameterScannerExtractsCorrectParameters() throws ClassNotFoundException
    {
        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.adapt(BundleWiring.class)).thenReturn(bundleWiring);
        when(bundleWiring.listResources(any(), any(), anyInt())).thenReturn(Arrays.asList(clazz.getCanonicalName() + ".class"));
        doReturn(clazz).when(bundle).loadClass(clazz.getCanonicalName());

        final Map<Class<?>, Map<Method, String>> classQueries = QueryScanner.scanForQueries(packages, bundleContext);
        final Map<Method, List<String>> query = ParameterScanner.scanQueryForParameters(classQueries.get(clazz));

        for (Map.Entry<Method, List<String>> methodMap : query.entrySet())
        {
            final List<String> expectedParameters = EXPECTED_QUERY_PARAMS.get(annotatedMethodNames.indexOf(methodMap.getKey().getName()));
            final List<String> extractedParameters = methodMap.getValue();
            assertTrue("Not all expected parameters in extracted parameter set.", extractedParameters.containsAll(expectedParameters));
        }
    }

}