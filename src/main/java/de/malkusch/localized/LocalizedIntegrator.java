package de.malkusch.localized;

import de.malkusch.localized.event.DeleteEventListener;
import de.malkusch.localized.event.ReadEventListener;
import de.malkusch.localized.event.WriteEventListener;
import de.malkusch.localized.localeResolver.LocaleResolver;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.beanvalidation.DuplicationStrategyImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import javax.annotation.Resource;
import java.util.ServiceLoader;

/**
 * Automatic integration into Hibernate.
 * <p>
 * This is the entry point into Hibernate. Hibernate discovers this
 * service with {@link ServiceLoader}.
 *
 * @author Markus Malkusch <markus@malkusch.de>
 * @since 0.2.8
 */
public class LocalizedIntegrator implements Integrator {

    /**
     * The name of a configuration setting that registers a locale resolver.
     * Default is de.malkusch.localized.localeResolver.DefaultLocaleResolver
     */
    public static final String LOCALE_RESOLVER = "hibernate.listeners.localized.locale_resolver";

    @Resource
    private static LocaleResolver localeResolver;


    @Override
    public void integrate(Metadata configuration,
                          SessionFactoryImplementor sessionFactory,
                          SessionFactoryServiceRegistry serviceRegistry) {

        /**
         * I had hard times to figure out how to add this entity automatically
         * to the current persistence unit. As Configuration is considered to
         * be removed this might break in future. So these are the three other
         * ways which put the entity into the persistence unit:
         *
         * 1. If you have no persistence.xml and configure the EntityManagerFactory with
         * Spring's LocalContainerEntityManagerFactoryBean#setPackagesToScan(), you have
         * to add LocalizedProperty.class.getPackage().getName() to those Packages.
         *
         * 2. If you use persistence.xml add <mapping-file>META-INF/localized.xml</mapping-file>
         * to your persistence-unit.
         *
         * 3. If you configure it programmatically (e.g. in a test), use Configuration.addAnnotatedClass()
         */


      /*  if (localeResolverClassName != null) {
            try {
                setLocaleResolver((LocaleResolver) Class.forName(localeResolverClassName).newInstance());

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new ConfigurationException(String.format(
                        "could not instantiate LocaleResolver %s from hibernate option %s",
                        localeResolverClassName, LOCALE_RESOLVER), e);

            }
        } else {
            DefaultLocaleResolver localeResolver = new DefaultLocaleResolver();
            localeResolver.setWarnOnce(true);
            setLocaleResolver(localeResolver);

        }*/


        final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        eventListenerRegistry.addDuplicationStrategy(DuplicationStrategyImpl.INSTANCE);

        eventListenerRegistry.appendListeners(EventType.POST_LOAD, new ReadEventListener(this, sessionFactory));
        eventListenerRegistry.appendListeners(EventType.POST_UPDATE, new WriteEventListener(this, sessionFactory));
        eventListenerRegistry.appendListeners(EventType.POST_INSERT, new WriteEventListener(this, sessionFactory));
        eventListenerRegistry.appendListeners(EventType.POST_DELETE, new DeleteEventListener(this, sessionFactory));
    }

    /**
     * Registers a {@link LocaleResolver}.
     * <p>
     * You can also configure this by the hibernate property {@value #LOCALE_RESOLVER}.
     */
    static public void setLocaleResolver(LocaleResolver localeResolver) {
        LocalizedIntegrator.localeResolver = localeResolver;
    }

    public LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory,
                             SessionFactoryServiceRegistry serviceRegistry) {

    }
}
