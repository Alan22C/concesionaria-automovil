package com.mycompany.automovil.persistencia;

import java.io.Serializable;
import com.mycompany.automovil.logica.Automovil;
import com.mycompany.automovil.persistencia.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AutomovilJpaController implements Serializable {
    
     public AutomovilJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public AutomovilJpaController() {
        emf = Persistence.createEntityManagerFactory("AutoPU");
    }

    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Automovil auto) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(auto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Automovil auto) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            auto = em.merge(auto);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Integer id = auto.getId(); // Asegúrate de tener un método getId() en Automovil
            if (findAutomovil(id) == null) {
                throw new NonexistentEntityException("El automóvil con id " + id + " no existe.");
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Automovil auto;
            try {
                auto = em.getReference(Automovil.class, id);
                auto.getId(); // Para asegurarse de que se carga la entidad
            } catch (Exception e) {
                throw new NonexistentEntityException("El automóvil con id " + id + " no existe.", e);
            }
            em.remove(auto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Automovil findAutomovil(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Automovil.class, id);
        } finally {
            em.close();
        }
    }

    public List<Automovil> findAutomovilEntities() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT a FROM Automovil a", Automovil.class).getResultList();
        } finally {
            em.close();
        }
    }
}
