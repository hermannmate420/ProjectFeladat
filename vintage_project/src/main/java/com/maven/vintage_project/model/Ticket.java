package com.maven.vintage_project.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

@Entity
@Table(name = "ticket") // vagy a tábla pontos neve
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "ticket_body", nullable = false, length = 255)
    private String ticketBody;

    @Column(name = "status", nullable = false, length = 255)
    private String status;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // --- Konstruktorok ---
    public Ticket() {
    }

    public Ticket(Integer ticketId, Integer userId, String ticketBody, String status, Date createdAt) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.ticketBody = ticketBody;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- Getterek és Setterek ---
    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTicketBody() {
        return ticketBody;
    }

    public void setTicketBody(String ticketBody) {
        this.ticketBody = ticketBody;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // Ha szeretnéd, hogy a Java kód állítsa be a dátumot, akkor itt tedd be: 
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Ticket[ ticketId=" + ticketId + ", userId=" + userId
                + ", status=" + status + ", createdAt=" + createdAt + " ]";
    }

    // --- A persistence egység eléréséhez ---
    @PersistenceUnit
    private static EntityManagerFactory emf;

    public static EntityManager getEntityManager() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU");
        }
        return emf.createEntityManager();
    }

    // --- Stored procedure alapú lekérdezések ---
    // 1. Ticket lekérése ID alapján
    public static Ticket getTicketById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getTicketById", Ticket.class);
            spq.registerStoredProcedureParameter("p_ticket_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_ticket_id", id);
            return (Ticket) spq.getSingleResult();
        } catch (Exception e) {
            System.err.println("Hiba getTicketById során: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    // 2. Összes ticket lekérése
    public static List<Ticket> getAllTickets() {
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllTickets", Ticket.class);
            List<Ticket> tickets = spq.getResultList();
            return tickets;
        } catch (Exception e) {
            System.err.println("Hiba getAllTickets során: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // 3. Új ticket hozzáadása
    public static boolean addTicket(Ticket ticket) {
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("addTicket");
            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_ticket_body", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);

            spq.setParameter("p_user_id", ticket.getUserId());
            spq.setParameter("p_ticket_body", ticket.getTicketBody());
            spq.setParameter("p_status", ticket.getStatus());
            spq.execute();
            return true;
        } catch (Exception e) {
            System.err.println("Hiba addTicket során: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    // 4. Ticket frissítése (például a status módosítása)
    public static boolean updateTicketStatus(Integer ticketId, String newStatus) {
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateTicket");
            spq.registerStoredProcedureParameter("p_ticket_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_new_status", String.class, ParameterMode.IN);

            spq.setParameter("p_ticket_id", ticketId);
            spq.setParameter("p_new_status", newStatus);
            spq.execute();
            return true;
        } catch (Exception e) {
            System.err.println("Hiba updateTicketStatus során: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    // 5. Ticket törlése ID alapján
    public static boolean deleteTicket(Integer ticketId) {
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("deleteTicket");
            spq.registerStoredProcedureParameter("p_ticket_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_ticket_id", ticketId);
            spq.execute();
            return true;
        } catch (Exception e) {
            System.err.println("Hiba deleteTicket során: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}
