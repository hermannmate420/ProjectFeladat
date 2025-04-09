package com.maven.vintage_project.controller;

import com.maven.vintage_project.model.Message;
import com.maven.vintage_project.service.MessageService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/message")
public class MessageController {

    private final MessageService messageService = new MessageService();

    // GET: /message/getById?messageId=1
    @GET
    @Path("/getById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@QueryParam("messageId") Integer messageId) {
        JSONObject result = messageService.getMessageById(messageId);
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // GET: /message/getAll
    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        JSONObject result = messageService.getAllMessages();
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // POST: /message/create
    // Body JSON példa:
    // {
    //   "fullName": "John Doe",
    //   "messageText": "Hello, I have an issue",
    //   "email": "john@example.com",
    //   "subject": "Inquiry"
    // }
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMessage(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            Message msg = new Message();
            msg.setFullName(body.getString("fullName"));
            msg.setMessageText(body.getString("messageText"));
            msg.setEmail(body.getString("email"));
            msg.setSubject(body.getString("subject"));
            // createdAt automatikus az adatbázisban

            JSONObject result = messageService.createMessage(msg);
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

    // DELETE: /message/delete?messageId=1
    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMessage(@QueryParam("messageId") Integer messageId) {
        JSONObject result = messageService.deleteMessage(messageId);
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // POST endpoint reply email küldéséhez
    // JSON input példa:
    // {
    //   "to": "user@example.com",
    //   "originalMessage": "Eredeti ticket szöveg",
    //   "replyBody": "Kedves Ügyfél, itt a válasz...",
    //   "adminName": "Admin Neve",
    //   "ticketLink": "http://localhost:4200/ticket/123"
    // }
    @POST
    @Path("/reply")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendReplyEmail(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            String to = body.getString("to");
            String originalMessage = body.getString("originalMessage");
            String replyBody = body.getString("replyBody");
            String adminName = body.getString("adminName");
            String ticketLink = body.optString("ticketLink", "");  // Opcionális

            JSONObject result = messageService.sendMessageReplyEmail(to, originalMessage, replyBody, adminName, ticketLink);
            return Response.status(result.optInt("status", 500))
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
