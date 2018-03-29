package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.repository.annotation.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParameterScanner
{

    private final static String JPQL_PARAMETER_REGEX = ":[a-zA-Z]+";

    private final static Pattern JPQL_PATTERN = Pattern.compile(JPQL_PARAMETER_REGEX);

    /**
     * Scans a
     * @param repos
     * @return
     */
    public static Map<Method, Map<String, String>> scanMethodsForParameters(final Map<Method, String> repos)
    {
        final Map<Method, Map<String, String>> methodParameters = new HashMap<>();
        for (final Map.Entry<Method, String> methodQuery : repos.entrySet())
        {
            final Map<String, String> parameters = new HashMap<>();
            for (final Parameter parameter : methodQuery.getKey().getParameters())
            {
                Param param = parameter.getAnnotation(Param.class);
                if (param != null)
                {
                    //TODO: Check there are no repeat @Param values
                    parameters.put(parameter.getName(), param.value());
                }
            }
            methodParameters.put(methodQuery.getKey(), parameters);
        }

        return methodParameters;
    }

    public static Map<Method, List<String>> scanQueryForParameters(final Map<Method, String> methodQueries)
    {
        final Map<Method, List<String>> methodQueryParameters = new HashMap<>();

        for (Map.Entry<Method, String> methodQuery : methodQueries.entrySet())
        {
            final Matcher matcher = JPQL_PATTERN.matcher(methodQuery.getValue());
            final List<String> queryParameters = new ArrayList<>();

            while (matcher.find())
            {
                queryParameters.add(methodQuery.getValue().substring(matcher.start() + 1, matcher.end()));
            }
            methodQueryParameters.put(methodQuery.getKey(), queryParameters);
        }

        return methodQueryParameters;
    }

    public static void checkQueryAndMethodParamsMatch(final Map<Method, List<String>> queryParameters, final Map<Method, List<String>> methodParameters)
    {

    }
}
