package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Message;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONArray;
import org.json.JSONObject;

public class MessageService {

    // Segédfüggvény az EntityManager létrehozásához
    private EntityManager getEntityManager() {
        return Persistence
                .createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU")
                .createEntityManager();
    }

    // 1) getMessageById
    public JSONObject getMessageById(Integer id) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getMessageById", Message.class);
            spq.registerStoredProcedureParameter("p_message_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_message_id", id);

            Message msg = null;
            try {
                msg = (Message) spq.getSingleResult();
            } catch (NoResultException nre) {
                msg = null;
            }

            if (msg == null) {
                response.put("status", "MessageNotFound")
                        .put("statusCode", 404);
            } else {
                JSONObject msgJson = new JSONObject();
                msgJson.put("messageId", msg.getMessageId());
                msgJson.put("fullName", msg.getFullName());
                msgJson.put("messageText", msg.getMessageText());
                msgJson.put("createdAt", msg.getCreatedAt());
                msgJson.put("email", msg.getEmail());
                msgJson.put("subject", msg.getSubject());

                response.put("status", "success")
                        .put("statusCode", 200)
                        .put("result", msgJson);
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

    // 2) getAllMessages
    public JSONObject getAllMessages() {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllMessages", Message.class);
            List<Message> messages = spq.getResultList();

            if (messages.isEmpty()) {
                response.put("status", "NoMessagesFound")
                        .put("statusCode", 404);
            } else {
                JSONArray resultArray = new JSONArray();
                for (Message m : messages) {
                    JSONObject msgJson = new JSONObject();
                    msgJson.put("messageId", m.getMessageId());
                    msgJson.put("fullName", m.getFullName());
                    msgJson.put("messageText", m.getMessageText());
                    msgJson.put("createdAt", m.getCreatedAt());
                    msgJson.put("email", m.getEmail());
                    msgJson.put("subject", m.getSubject());
                    resultArray.put(msgJson);
                }
                response.put("status", "success")
                        .put("statusCode", 200)
                        .put("result", resultArray);
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

    // 3) createMessage
    public JSONObject createMessage(Message msg) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();

        try {
            // Ha te akarsz created_at-ot beállítani, teheted: msg.setCreatedAt(new Date());

            StoredProcedureQuery spq = em.createStoredProcedureQuery("createMessage");
            spq.registerStoredProcedureParameter("p_full_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_message", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_subject", String.class, ParameterMode.IN);

            spq.setParameter("p_full_name", msg.getFullName());
            spq.setParameter("p_message", msg.getMessageText());
            spq.setParameter("p_email", msg.getEmail());
            spq.setParameter("p_subject", msg.getSubject());

            em.getTransaction().begin();
            spq.execute();
            em.getTransaction().commit();

            response.put("status", "success")
                    .put("statusCode", 200);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            response.put("status", "Error")
                    .put("statusCode", 500)
                    .put("error", e.getMessage());
        } finally {
            em.close();
        }

        return response;
    }

    // 4) deleteMessage
    public JSONObject deleteMessage(Integer messageId) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("deleteMessage");
            spq.registerStoredProcedureParameter("p_message_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_message_id", messageId);

            em.getTransaction().begin();
            spq.execute();
            em.getTransaction().commit();

            response.put("status", "success")
                    .put("statusCode", 200);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
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
