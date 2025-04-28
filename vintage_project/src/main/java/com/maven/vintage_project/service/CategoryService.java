package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Category;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;

public class CategoryService {

    private EntityManagerFactory emf;

    public CategoryService() {
        emf = Persistence.createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Lekéri az összes kategóriát.
     */
    public JSONObject getAllCategories() {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            List<Category> categories = em.createQuery("SELECT c FROM Category c", Category.class)
                    .getResultList();
            if (categories.isEmpty()) {
                response.put("status", "NoCategoriesFound")
                        .put("statusCode", 404);
            } else {
                JSONArray arr = new JSONArray();
                for (Category c : categories) {
                    JSONObject obj = new JSONObject()
                            .put("id", c.getId())
                            .put("name", c.getName())
                            .put("description", c.getDescription());
                    arr.put(obj);
                }
                response.put("status", "success")
                        .put("statusCode", 200)
                        .put("result", arr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "Error")
                    .put("statusCode", 500)
                    .put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    /**
     * Lekér egy kategóriát azonosító alapján.
     */
    public JSONObject getCategoryById(Integer id) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            Category c = em.find(Category.class, id);
            if (c == null) {
                response.put("status", "CategoryNotFound")
                        .put("statusCode", 404);
            } else {
                JSONObject obj = new JSONObject()
                        .put("id", c.getId())
                        .put("name", c.getName())
                        .put("description", c.getDescription());
                response.put("status", "success")
                        .put("statusCode", 200)
                        .put("result", obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "Error")
                    .put("statusCode", 500)
                    .put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }
}
