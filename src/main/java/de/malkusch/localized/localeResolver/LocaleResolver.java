package de.malkusch.localized.localeResolver;

import java.util.Locale;

import org.hibernate.Session;

import de.malkusch.localized.LocalizedIntegrator;
import de.malkusch.localized.exception.UnresolvedLocaleException;

/**
 * Resolves the locale based on the {@link Session}.
 * 
 * Register a resolver at {@link LocalizedIntegrator#setLocaleResolver(LocaleResolver)}. 
 * 
 * @author Markus Malkusch <markus@malkusch.de>
 */
public interface LocaleResolver {
	
	/**
	 * Resolves the locale for the current {@link Session}.
	 * @throws UnresolvedLocaleException 
	 */
	public Locale resolveLocale(Session session) throws UnresolvedLocaleException;
	
}