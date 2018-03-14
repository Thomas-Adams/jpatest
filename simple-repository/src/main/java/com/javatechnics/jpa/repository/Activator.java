package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.dao.EntityManagerService;
import com.javatechnics.jpa.repository.proxy.RepositoryProxyHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Activator
{
    @Reference
    private EntityManagerService entityManagerService;

    private final static String JPQL_PARAMETER_REGEX = ":[a-zA-Z]+";

    private final static Pattern JPQL_PATTERN = Pattern.compile(JPQL_PARAMETER_REGEX);

    private List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

    public static final String REPOSITORY_MANIFEST_HEADER = "Repository-Package";

    private static Logger LOG = Logger.getLogger("RepositoryActivator");

    @Activate
    public void activate(final BundleContext bundleContext) throws Exception
    {
        final Dictionary<String, String> headers = bundleContext.getBundle().getHeaders();
        String repositoryPackage = headers.get(REPOSITORY_MANIFEST_HEADER);
        if (repositoryPackage == null || repositoryPackage.isEmpty())
        {
            LOG.log(Level.INFO, "Repository packages not defined.");
        }
        else
        {
            LOG.log(Level.INFO, "Repository package is: " + repositoryPackage);
            final QueryScanner queryScanner = new QueryScanner();
            final Map<Class<?>, Map<Method, String>> queries = queryScanner.scanForQueries(new String[] {repositoryPackage}, bundleContext);
            registerRepositories(queries, bundleContext);
        }

    }

    @Deactivate
    public void deactivate(final BundleContext bundleContext) throws Exception
    {
        for (ServiceRegistration<?> serviceRegistration : serviceRegistrations)
        {
            serviceRegistration.unregister();
        }
    }

    private void registerRepositories(final Map<Class<?>, Map<Method,String>> queries, final BundleContext bundleContext)
    {
        //tODO: should this be the class loader for the repository?
        ClassLoader classLoader = Activator.class.getClassLoader();

        for (Map.Entry<Class<?>, Map<Method, String>> repoqueries : queries.entrySet())
        {

            Class<?> repoClass = repoqueries.getKey();
            Map<Method, String> methodRawQueries = repoqueries.getValue();
            final Map<Method, List<String>> methodQueryParameters = new HashMap<>();

            for (Map.Entry<Method, String> methodQuery : methodRawQueries.entrySet())
            {
                final Matcher matcher = JPQL_PATTERN.matcher(methodQuery.getValue());
                final List<String> queryParameters = new ArrayList<>();

                while(matcher.find())
                {
                    queryParameters.add(methodQuery.getValue().substring(matcher.start() + 1, matcher.end()));
                }
                methodQueryParameters.put(methodQuery.getKey(), queryParameters);
            }
            final RepositoryProxyHandler handler = new RepositoryProxyHandler(entityManagerService, methodRawQueries, methodQueryParameters);
            final Object repoProxy = repoClass.cast(Proxy.newProxyInstance(classLoader, new Class[]{repoClass}, handler));
            final ServiceRegistration<?> serviceRegistration = bundleContext.registerService(repoClass.getName(), repoProxy, null);
            serviceRegistrations.add(serviceRegistration);
        }
    }
}
