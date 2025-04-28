package com.maven.vintage_project.controller;

import com.maven.vintage_project.model.NewsletterSubscriber;
import com.maven.vintage_project.service.NewsletterSubscriberService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/newsletterSubscriber")
public class NewsletterSubscriberController {

    private final NewsletterSubscriberService subscriberService = new NewsletterSubscriberService();

    // POST: /newsletterSubscriber/subscribe
    // JSON például:
    // {
    //   "email": "john@example.com",
    //   "fullName": "John Doe"
    // }
    @POST
    @Path("/subscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribe(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            NewsletterSubscriber sub = new NewsletterSubscriber();
            sub.setEmail(body.getString("email"));
            sub.setFullName(body.optString("fullName", ""));
            JSONObject result = subscriberService.subscribe(sub);
            return Response.status(result.optInt("statusCode", 500))
                    .entity(result.toString())
                    .build();
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("status", "BadRequest");
            error.put("statusCode", 400);
            error.put("error", e.getMessage());
            return Response.status(400).entity(error.toString()).build();
        }
    }

    // GET: /newsletterSubscriber/getAll
    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubscribers() {
        JSONObject result = subscriberService.getAllSubscribers();
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // DELETE: /newsletterSubscriber/unsubscribe?subscriberId=1
    @DELETE
    @Path("/unsubscribe")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unsubscribe(@QueryParam("subscriberId") Integer subscriberId) {
        JSONObject result = subscriberService.unsubscribe(subscriberId);
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }
}
