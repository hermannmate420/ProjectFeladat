package com.maven.vintage_project.controller;

import com.maven.vintage_project.model.Ticket;
import com.maven.vintage_project.service.TicketService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/ticket")
public class TicketController {

    private final TicketService ticketService = new TicketService();

    // GET: /ticket/getTicketById?ticketId=1
    @GET
    @Path("/getTicketById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTicketById(@QueryParam("ticketId") Integer ticketId) {
        JSONObject result = ticketService.getTicketById(ticketId);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .build();
    }

    // GET: /ticket/getAllTickets
    @GET
    @Path("/getAllTickets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTickets() {
        JSONObject result = ticketService.getAllTickets();
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .build();
    }

    // POST: /ticket/addTicket
    // Body: { "userId": 123, "ticketBody": "Szöveg...", "status": "open" }
    @POST
    @Path("/addTicket")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTicket(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            Ticket ticket = new Ticket();
            ticket.setUserId(body.getInt("userId"));
            ticket.setTicketBody(body.getString("ticketBody"));
            ticket.setStatus(body.getString("status"));
            // created_at-t a DB kitölti, ha van DEFAULT CURRENT_TIMESTAMP

            JSONObject result = ticketService.addTicket(ticket);
            return Response.status(result.getInt("statusCode"))
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

    // PUT: /ticket/updateTicket
    // Body: { "ticketId": 1, "status": "closed" }
    @PUT
    @Path("/updateTicket")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTicket(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            Integer ticketId = body.getInt("ticketId");
            String newStatus = body.getString("status");

            JSONObject result = ticketService.updateTicket(ticketId, newStatus);
            return Response.status(result.getInt("statusCode"))
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

    // DELETE: /ticket/deleteTicket?ticketId=1
    @DELETE
    @Path("/deleteTicket")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTicket(@QueryParam("ticketId") Integer ticketId) {
        JSONObject result = ticketService.deleteTicket(ticketId);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .build();
    }
}
