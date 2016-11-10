package de.malkusch.localized.event;

import de.malkusch.localized.Localized;
import de.malkusch.localized.LocalizedIntegrator;
import de.malkusch.localized.LocalizedProperty;
import de.malkusch.localized.exception.LocalizedException;
import org.hibernate.StatelessSession;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Persists the entity's @{@link Localized} fields.
 *
 * @author Markus Malkusch <markus@malkusch.de>
 * @since 0.2.8
 */
public class WriteEventListener extends AbstractEventListener implements
        PostUpdateEventListener, PostInsertEventListener {

    private static final long serialVersionUID = -5028643125465610L;

    public WriteEventListener(LocalizedIntegrator integrator, SessionFactoryImplementor sessionFactory) {
        super(integrator, sessionFactory);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        handleFields(event, event.getEntity(), event.getId());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        handleFields(event, event.getEntity(), event.getId());
    }

    @Override
    protected void handleField(StatelessSession session, Field field, Object entity, LocalizedProperty property) throws LocalizedException {
        try {
            property.setValue((Serializable) field.get(entity));
            if (property.getId() == null) {
                session.insert(property);

            } else {
                session.update(property);

            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new LocalizedException(e);

        }
    }

    /**
     * XXX No idea what this means
     */
    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }

}
