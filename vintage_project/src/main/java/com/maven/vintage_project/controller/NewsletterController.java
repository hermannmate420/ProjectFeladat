package com.maven.vintage_project.controller;

import com.maven.vintage_project.service.NewsletterService;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService = new NewsletterService();

    /**
     * Endpoint newsletter küldéséhez. Példa JSON bemenet: { "to":
     * "recipient@example.com", "subject": "Hírlevél - Újdonságok", "template":
     * "newsletter", "name": "Kedves Olvasó", "mainContent": "Itt az első, fő
     * tartalom.", "content": "Itt a további, részletes tartalom.",
     * "otherContent": "Egyéb információk itt.", "homelink":
     * "http://example.com/home", "currentYear": "2023" }
     */
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendNewsletter(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            String to = body.getString("to");
            String subject = body.getString("subject");
            // A front-end által megadott sablon neve
            String templateName = body.getString("template");

            // Gyűjtsd össze a sablonhoz szükséges helyőrzőket.
            // Figyelj arra, hogy a kulcsok pontosan egyezzenek a sablonban találhatókkal.
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("name", body.optString("name", "Olvasó"));
            placeholders.put("mainContent", body.optString("mainContent", ""));
            placeholders.put("content", body.optString("content", ""));
            placeholders.put("otherContent", body.optString("otherContent", ""));
            placeholders.put("homelink", body.optString("homelink", "http://localhost:4200/home"));
            placeholders.put("currentYear", body.optString("currentYear", String.valueOf(java.time.Year.now().getValue())));

            JSONObject result = newsletterService.sendNewsletterEmail(to, subject, templateName, placeholders);
            return Response.status(result.optInt("statusCode", 500))
                    .entity(result.toString())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "BadRequest");
            error.put("statusCode", 400);
            error.put("error", e.getMessage());
            return Response.status(400).entity(error.toString()).build();
        }
    }
}
