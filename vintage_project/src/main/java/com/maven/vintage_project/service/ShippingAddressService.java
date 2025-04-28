package com.maven.vintage_project.service;

import com.maven.vintage_project.model.ShippingAddress;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

public class ShippingAddressService {

    private EntityManagerFactory emf;
    private EntityManager em;

    public ShippingAddressService() {
        // Győződj meg róla, hogy a persistence unit neve megfelelően van beállítva!
        emf = Persistence.createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU");
        em = emf.createEntityManager();
    }

    public ShippingAddress createShippingAddress(ShippingAddress shippingAddress) {
        try {
            em.getTransaction().begin();
            shippingAddress.setCreatedAt(new Date());
            em.persist(shippingAddress);
            em.getTransaction().commit();
            return shippingAddress;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        }
    }

    public ShippingAddress updateShippingAddress(ShippingAddress shippingAddress) {
        try {
            em.getTransaction().begin();
            // Lekérjük a meglévő rekordot az adatbázisból a kapott shippingAddressId alapján
            ShippingAddress existing = em.find(ShippingAddress.class, shippingAddress.getShippingAddressId());
            if (existing == null) {
                throw new RuntimeException("Shipping address not found with id: " + shippingAddress.getShippingAddressId());
            }
            // Csak a módosítani kívánt mezőket állítjuk be, a shippingAddressId-t nem érintjük
            existing.setUserId(shippingAddress.getUserId());
            existing.setRecipientName(shippingAddress.getRecipientName());
            existing.setAddressLine1(shippingAddress.getAddressLine1());
            existing.setAddressLine2(shippingAddress.getAddressLine2());
            existing.setCity(shippingAddress.getCity());
            existing.setState(shippingAddress.getState());
            existing.setPostalCode(shippingAddress.getPostalCode());
            existing.setCountry(shippingAddress.getCountry());
            existing.setPhone(shippingAddress.getPhone());
            existing.setUpdatedAt(new Date());
            // Az existing entity-t merge-eljük (megújítjuk) az adatbázisban
            ShippingAddress updated = em.merge(existing);
            em.getTransaction().commit();
            return updated;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        }
    }

    // Eredeti metódus – shippingAddressId alapján történő lekérdezés
    public ShippingAddress getShippingAddressById(Integer id) {
        return em.find(ShippingAddress.class, id);
    }

    // Új metódus – user_id alapján történő lekérdezés
    public ShippingAddress getShippingAddressByUserId(Integer userId) {
        try {
            return em.createQuery("SELECT s FROM ShippingAddress s WHERE s.userId = :userId", ShippingAddress.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<ShippingAddress> getAllShippingAddresses() {
        return em.createQuery("SELECT s FROM ShippingAddress s", ShippingAddress.class)
                .getResultList();
    }

    public void deleteShippingAddress(Integer id) {
        try {
            em.getTransaction().begin();
            ShippingAddress address = em.find(ShippingAddress.class, id);
            if (address != null) {
                em.remove(address);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        }
    }

    public void close() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }
}
