package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Message;
import java.util.List;
import javax.persistence.*;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

    public JSONObject sendMessageReplyEmail(String to, final String originalMessage, final String replyBody, final String adminName, final String ticketLink) {
        JSONObject responseJson = new JSONObject();
        try {
            // Betöltjük a sablont (message-reply.html)
            String htmlBody = loadEmailTemplate("re-feedback", new HashMap<String, String>() {
                {
                    put("originalMessage", originalMessage);
                    put("replyBody", replyBody);
                    put("adminName", adminName);
                    put("ticketLink", ticketLink); // Ha nincs link, akár üres string is lehet
                    put("currentYear", String.valueOf(java.time.Year.now().getValue()));
                }
            });

            // Email küldés paraméterei
            String subject = "Re: " + originalMessage; // vagy más, amit szeretnél
            boolean success = sendEmail(to, subject, htmlBody);

            if (success) {
                responseJson.put("status", 200);
                responseJson.put("message", "Email successfully sent.");
            } else {
                responseJson.put("status", 500);
                responseJson.put("error", "Failed to send email.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("status", 500);
            responseJson.put("error", e.getMessage());
        }
        return responseJson;
    }

    // Alap sendEmail metódus, hasonló a UserService-ben lévőhöz
    public boolean sendEmail(String to, String subject, String htmlBody) {
        try {
            Properties config = new Properties();
            try (InputStream input = MessageService.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new FileNotFoundException("config.properties not found");
                }
                config.load(input);
            }
            final String from = config.getProperty("mail.from");
            final String password = config.getProperty("mail.password");
            String host = config.getProperty("mail.host");
            String port = config.getProperty("mail.port");

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.ssl.enable", config.getProperty("mail.ssl.enable"));
            properties.put("mail.smtp.auth", config.getProperty("mail.smtp.auth"));

            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });
            session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html;charset=utf-8");

            Transport.send(message);
            return true;
        } catch (Exception ex) {
            System.err.println("Email sending error: " + ex.getLocalizedMessage());
            return false;
        }
    }

    // Method to load email template from resource folder and replace placeholders
    public String loadEmailTemplate(String templateName, Map<String, String> placeholders) throws IOException {
        InputStream is = MessageService.class.getClassLoader().getResourceAsStream("email-templates/" + templateName + ".html");
        if (is == null) {
            throw new FileNotFoundException("Template " + templateName + " not found");
        }
        String content = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return content;
    }
}
