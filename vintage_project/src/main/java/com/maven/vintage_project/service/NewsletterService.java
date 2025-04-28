package com.maven.vintage_project.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

public class NewsletterService {

    /**
     * Betölti a megadott sablont és lecseréli a placeholder-eket.
     *
     * @param templateName A sablon neve (például "newsletter")
     * @param placeholders A sablonban lecserélendő helyőrzők map-je
     * @return A generált HTML tartalom
     * @throws IOException Ha a sablon nem található vagy olvasási hiba történik
     */
    public String loadTemplate(String templateName, Map<String, String> placeholders) throws IOException {
        String path = "email-templates/" + templateName + ".html";
        System.out.println("Betöltendő sablon elérési út: " + path);
        InputStream is = NewsletterService.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Template " + templateName + " not found at " + path);
        }
        String content = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return content;
    }

    /**
     * Küld egy newsletter emailt a megadott címre.
     *
     * @param to A címzett email címe
     * @param subject Az email tárgya
     * @param htmlContent Az email HTML tartalma
     * @return true, ha sikeres a küldés, különben false
     */
    public boolean sendNewsletter(String to, String subject, String htmlContent) {
        try {
            Properties config = new Properties();
            try (InputStream input = NewsletterService.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new FileNotFoundException("config.properties not found");
                }
                config.load(input);
            }
            final String from = config.getProperty("mail.from");
            final String password = config.getProperty("mail.password");
            String host = config.getProperty("mail.host");
            String port = config.getProperty("mail.port");

            Properties props = System.getProperties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.ssl.enable", config.getProperty("mail.ssl.enable"));
            props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth"));

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });
            session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html;charset=utf-8");

            Transport.send(message);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Newsletter küldése sablon alapján. A front-end most megadja a sablon
     * nevét is.
     *
     * @param to A címzett email cím
     * @param subject Az email tárgya
     * @param templateName A sablon neve, például "newsletter" vagy "alert",
     * stb.
     * @param placeholders A sablonhoz szükséges helyőrzők map-je
     * @return JSON objektum a küldés eredményéről
     */
    public JSONObject sendNewsletterEmail(String to, String subject, String templateName, Map<String, String> placeholders) {
        JSONObject response = new JSONObject();
        try {
            // Betöltjük a front-end által megadott sablont
            String htmlContent = loadTemplate(templateName, placeholders);
            boolean success = sendNewsletter(to, subject, htmlContent);
            if (success) {
                response.put("status", "success");
                response.put("statusCode", 200);
                response.put("message", "Newsletter sent successfully.");
            } else {
                response.put("status", "error");
                response.put("statusCode", 500);
                response.put("error", "Failed to send newsletter.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("statusCode", 500);
            response.put("error", e.getMessage());
        }
        return response;
    }
}
