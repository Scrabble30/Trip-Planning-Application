package app.daos.impl;

import app.daos.AbstractDAO;
import app.entities.Guide;
import jakarta.persistence.EntityManagerFactory;

public class GuideDAO extends AbstractDAO<Guide, Long> {

    private static GuideDAO instance;

    private GuideDAO(EntityManagerFactory emf) {
        super(emf, Guide.class);
    }

    public static GuideDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new GuideDAO(emf);
        }

        return instance;
    }
}
