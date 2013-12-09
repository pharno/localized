package de.malkusch.localized.test.rule;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.malkusch.localized.LocalizedIntegrator;
import de.malkusch.localized.LocalizedDAO;
import de.malkusch.localized.localeResolver.ThreadLocalLocaleResolver;
import de.malkusch.localized.test.model.Book;

public class SessionRule implements MethodRule {

	private Session session;

	private SessionFactory sessionFactory;

	private LocalizedDAO dao;
	
	private ThreadLocalLocaleResolver localeResolver;
	
	public LocalizedDAO getDao() {
		return dao;
	}
	
	public Session getSession() {
		return session;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public ThreadLocalLocaleResolver getLocaleResolver() {
		return localeResolver;
	}

	@Override
	public Statement apply(final Statement statement, FrameworkMethod method,
			Object object) {

		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				sessionFactory = createSessionFactory();
				session = sessionFactory.openSession();
				try {
					dao = new LocalizedDAO(session);
					localeResolver = new ThreadLocalLocaleResolver();
					LocalizedIntegrator.setLocaleResolver(localeResolver);
					
					statement.evaluate();
					
				} finally {
					session.close();
					sessionFactory.close();
				}
			}
		};

	}

	@SuppressWarnings("deprecation")
	private SessionFactory createSessionFactory() {
		Configuration configuration = new Configuration();
		configuration.configure();
		configuration.addAnnotatedClass(Book.class);
		
		// precompile once to get LocalizedProperty registered.
		configuration.buildSessionFactory();
		
		return configuration.buildSessionFactory();
	}

}
