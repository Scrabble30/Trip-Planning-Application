package app.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractDAO<T, I> implements IDAO<T, I> {
    protected final EntityManagerFactory emf;
    protected final Class<T> entityClass;

    protected AbstractDAO(EntityManagerFactory emf, Class<T> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    @Override
    public T create(T t) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();
            return t;
        }
    }

    @Override
    public T getById(I id) {
        try (EntityManager em = emf.createEntityManager()) {
            T foundEntity = em.find(entityClass, id);

            if (foundEntity == null) {
                throw new EntityNotFoundException(String.format("%s with id %s could not be found", entityClass.getSimpleName(), id));
            }

            return foundEntity;
        }
    }

    @Override
    public Set<T> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<T> query = em.createNamedQuery(String.format("%s.getAll", entityClass.getSimpleName()), entityClass);

            return query.getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public T update(I id, T t) {
        try (EntityManager em = emf.createEntityManager()) {
            T foundEntity = em.find(entityClass, id);

            if (foundEntity == null) {
                throw new EntityNotFoundException(String.format("%s with id %s does not exist", entityClass.getSimpleName(), id));
            }

            em.getTransaction().begin();
            em.merge(t);
            em.getTransaction().commit();
            return t;
        }
    }

    @Override
    public void delete(I id) {
        try (EntityManager em = emf.createEntityManager()) {
            T foundEntity = em.find(entityClass, id);

            if (foundEntity == null) {
                throw new EntityNotFoundException(String.format("%s with id %s could not be found", entityClass.getSimpleName(), id));
            }

            em.getTransaction().begin();
            em.remove(foundEntity);
            em.getTransaction().commit();
        }
    }
}