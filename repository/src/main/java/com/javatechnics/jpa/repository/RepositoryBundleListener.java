package com.javatechnics.jpa.repository;

import com.javatechnics.jpa.repository.proxy.RepositoryProxyHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(immediate = true, service = {})
public class RepositoryBundleListener implements SynchronousBundleListener
{
    @Reference(policy = ReferencePolicy.STATIC, target = "(osgi.unit.name=test)")
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private static final int FIRST = 0;

    private final static String JPQL_PARAMETER_REGEX = ":[a-zA-Z]+";

    private final static String PROXY_SERVICE_FILTER = "(proxyservice=true)";

    private final static Dictionary<String, String> PROXY_SERVICE_DICTIONARY = new Hashtable<>(1);

    private final static Pattern JPQL_PATTERN = Pattern.compile(JPQL_PARAMETER_REGEX);

    public static final String REPOSITORY_MANIFEST_HEADER = "Repository-Package";

    private static final Logger LOG = Logger.getLogger("RepositoryBundleListener");

    static
    {
        PROXY_SERVICE_DICTIONARY.put("proxyservice", "true");
    }

    @Activate
    public void activate(final BundleContext bundleContext)
    {
        entityManager = entityManagerFactory.createEntityManager();
        bundleContext.addBundleListener(this);
    }

    @Deactivate
    public void deactivate(final BundleContext bundleContext)
    {

        bundleContext.removeBundleListener(this);
        entityManager.close();
    }

    @Override
    public void bundleChanged(final BundleEvent bundleEvent)
    {
        switch (BundleLifeCycleEvent.getEnum(bundleEvent.getType()))
        {
            case STARTED:
                this.repositoryBundleStarted(bundleEvent);
                break;
            case STOPPED:
                break;
            case UPDATED:
                break;
            case RESOLVED:
                break;
            case STARTING:
                break;
            case STOPPING:
                this.repositoryBundleStopping(bundleEvent);
                break;
            case INSTALLED:
                break;
            case UNRESOLVED:
                break;
            case UNINSTALLED:
                break;
            case LAZY_ACTIVATION:
                break;
            default:
                LOG.log(Level.INFO, "Unrecognised lifecycle: "
                        + bundleEvent.getType() + " for Bundle: " + bundleEvent.getBundle().getSymbolicName());
                break;
        }
    }

    private void repositoryBundleStarted(final BundleEvent bundleEvent)
    {
        final BundleContext bundleContext = bundleEvent.getBundle().getBundleContext();
        final List<String> packages =  isRepositoryBundle(bundleContext);
        if (!packages.isEmpty())
        {
            final Map<Class<?>, Map<Method, String>> repoMethodQueries = QueryScanner.scanForQueries(new String[]{packages.get(FIRST)}, bundleContext);
            this.registerRepositories(repoMethodQueries, bundleContext);
        }
    }

    /**
     * Registers repositories under the given bundle context.
     * @param queries
     * @param bundleContext
     */
    private void registerRepositories(final Map<Class<?>, Map<Method, String>> queries, final BundleContext bundleContext)
    {
        for (Map.Entry<Class<?>, Map<Method, String>> repoQueries : queries.entrySet())
        {

            Class<?> repoClass = repoQueries.getKey();
            Map<Method, String> methodRawQueries = repoQueries.getValue();
            final Map<Method, List<String>> methodQueryParameters = ParameterScanner.scanQueryForParameters(methodRawQueries);

            if (!methodRawQueries.isEmpty())
            {
                final ClassLoader repoClassLoader = repoClass.getClassLoader();
                final RepositoryProxyHandler handler = new RepositoryProxyHandler(entityManager, methodRawQueries, methodQueryParameters);
                final Object repoProxy = repoClass.cast(Proxy.newProxyInstance(repoClassLoader, new Class[]{repoClass}, handler));
                bundleContext.registerService(repoClass.getName(), repoProxy, PROXY_SERVICE_DICTIONARY);
            }
        }
    }

    /**
     * Called when a bundle is stopping.
     * @param bundleEvent
     */
    private void repositoryBundleStopping(final BundleEvent bundleEvent)
    {
        final BundleContext bundleContext = bundleEvent.getBundle().getBundleContext();
        final List<String> packages =  isRepositoryBundle(bundleContext);
        if (!packages.isEmpty())
        {
            try
            {
                ServiceReference<?>[] proxyServiceRegistrations = bundleContext.getAllServiceReferences(null, PROXY_SERVICE_FILTER);
                if (proxyServiceRegistrations != null)
                {
                    for (ServiceReference<?> serviceReference : proxyServiceRegistrations)
                    {
                        bundleContext.ungetService(serviceReference);
                    }
                }
            }
            catch (InvalidSyntaxException exception)
            {
                LOG.log(Level.SEVERE, exception.getMessage());
            }
        }
    }

    /**
     * Returns a list of packages declared by the bundle that contain repositories.
     * @param bundleContext the bundle's context.
     * @return a populated List of packages otherwise and empty List.
     */
    private List<String> isRepositoryBundle(final BundleContext bundleContext)
    {
        final Dictionary<String, String> headers = bundleContext.getBundle().getHeaders();
        String repositoryPackage = headers.get(REPOSITORY_MANIFEST_HEADER);
        //TODO: parse the value of the manifest header for multiple packages.
        return repositoryPackage != null && !repositoryPackage.isEmpty() ? Arrays.asList(repositoryPackage) : new ArrayList<>();
    }
}
