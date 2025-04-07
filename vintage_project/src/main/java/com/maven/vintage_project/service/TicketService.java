package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Ticket;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class TicketService {

    // Segédfüggvény az EntityManager létrehozásához
    public static EntityManager getEntityManager() {
        return Persistence
                .createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU")
                .createEntityManager();
    }

    // 1) Lekérdezés stored procedure segítségével ID alapján
    public JSONObject getTicketById(Integer id) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getTicketById", Ticket.class);
            spq.registerStoredProcedureParameter("p_ticket_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_ticket_id", id);
            Ticket ticket = (Ticket) spq.getSingleResult();
            if (ticket == null) {
                response.put("status", "TicketNotFound").put("statusCode", 404);
            } else {
                JSONObject ticketJson = new JSONObject();
                ticketJson.put("ticketId", ticket.getTicketId());
                ticketJson.put("userId", ticket.getUserId());
                ticketJson.put("ticketBody", ticket.getTicketBody());
                ticketJson.put("status", ticket.getStatus());
                ticketJson.put("createdAt", ticket.getCreatedAt());
                response.put("status", "success").put("statusCode", 200).put("result", ticketJson);
            }
        } catch (Exception e) {
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // 2) Összes ticket lekérdezése stored procedure segítségével
    public JSONObject getAllTickets() {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllTickets", Ticket.class);
            List<Ticket> tickets = spq.getResultList();
            if (tickets.isEmpty()) {
                response.put("status", "NoTicketsFound").put("statusCode", 404);
            } else {
                JSONArray resultArray = new JSONArray();
                for (Ticket t : tickets) {
                    JSONObject ticketJson = new JSONObject();
                    ticketJson.put("ticketId", t.getTicketId());
                    ticketJson.put("userId", t.getUserId());
                    ticketJson.put("ticketBody", t.getTicketBody());
                    ticketJson.put("status", t.getStatus());
                    ticketJson.put("createdAt", t.getCreatedAt());
                    resultArray.put(ticketJson);
                }
                response.put("status", "success").put("statusCode", 200).put("result", resultArray);
            }
        } catch (Exception e) {
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // 3) Új ticket létrehozása stored procedure segítségével
    public JSONObject addTicket(Ticket ticket) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("addTicket");
            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_ticket_body", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
            spq.setParameter("p_user_id", ticket.getUserId());
            spq.setParameter("p_ticket_body", ticket.getTicketBody());
            spq.setParameter("p_status", ticket.getStatus());
            em.getTransaction().begin();
            spq.execute();
            em.getTransaction().commit();
            response.put("status", "success").put("statusCode", 200);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // 4) Ticket frissítése (pl. status) stored procedure segítségével
    public JSONObject updateTicket(Integer ticketId, String newStatus) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateTicket");
            spq.registerStoredProcedureParameter("p_ticket_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_new_status", String.class, ParameterMode.IN);
            spq.setParameter("p_ticket_id", ticketId);
            spq.setParameter("p_new_status", newStatus);
            em.getTransaction().begin();
            spq.execute();
            em.getTransaction().commit();
            response.put("status", "success").put("statusCode", 200);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // 5) Ticket törlése stored procedure segítségével
    public JSONObject deleteTicket(Integer ticketId) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("deleteTicket");
            spq.registerStoredProcedureParameter("p_ticket_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_ticket_id", ticketId);
            em.getTransaction().begin();
            spq.execute();
            em.getTransaction().commit();
            response.put("status", "success").put("statusCode", 200);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }
}
