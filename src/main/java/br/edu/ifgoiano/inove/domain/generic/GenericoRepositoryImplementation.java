package br.edu.ifgoiano.inove.domain.generic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class GenericoRepositoryImplementation {

    @PersistenceContext
    private EntityManager entityManager;

    protected Session getSession() {
        Session session;
        try {
            session = entityManager.unwrap(Session.class);
        } catch (Exception e) {
            session = entityManager.unwrap(Session.class).getSessionFactory().unwrap(SessionFactory.class).openSession();
        }

        return session;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
