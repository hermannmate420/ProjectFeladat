package com.maven.vintage_project.service;

import com.maven.vintage_project.model.NewsletterSubscriber;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewsletterSubscriberService {

    private EntityManagerFactory emf;

    public NewsletterSubscriberService() {
        emf = Persistence.createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Feliratkozás
    public JSONObject subscribe(NewsletterSubscriber subscriber) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(subscriber);
            em.getTransaction().commit();
            response.put("status", "success").put("statusCode", 200);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.put("status", "error")
                    .put("statusCode", 500)
                    .put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // Összes feliratkozó lekérése
    public JSONObject getAllSubscribers() {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            TypedQuery<NewsletterSubscriber> query = em.createQuery("SELECT s FROM NewsletterSubscriber s", NewsletterSubscriber.class);
            List<NewsletterSubscriber> subscribers = query.getResultList();
            JSONArray resultArray = new JSONArray();
            for (NewsletterSubscriber sub : subscribers) {
                JSONObject subJson = new JSONObject();
                subJson.put("subscriberId", sub.getSubscriberId());
                subJson.put("email", sub.getEmail());
                subJson.put("fullName", sub.getFullName());
                subJson.put("createdAt", sub.getCreatedAt());
                resultArray.put(subJson);
            }
            response.put("status", "success").put("statusCode", 200).put("result", resultArray);
        } catch (Exception e) {
            response.put("status", "error")
                    .put("statusCode", 500)
                    .put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // Leiratkozás (feliratkozó törlése az ID alapján)
    public JSONObject unsubscribe(Integer subscriberId) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            NewsletterSubscriber sub = em.find(NewsletterSubscriber.class, subscriberId);
            if (sub == null) {
                response.put("status", "notFound").put("statusCode", 404);
                return response;
            }
            em.getTransaction().begin();
            em.remove(sub);
            em.getTransaction().commit();
            response.put("status", "success").put("statusCode", 200);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.put("status", "error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }
}
