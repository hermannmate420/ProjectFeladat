package com.maven.vintage_project.controller;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.ShippingAddress;
import com.maven.vintage_project.service.ShippingAddressService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

@Path("/shippingAddress")
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService = new ShippingAddressService();

    // GET: Lekérdezi egy adott ShippingAddress adatait azonosító alapján
    @GET
    @Path("/getShippingAddressByUserId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShippingAddressByUserId(@HeaderParam("token") String jwt,
            @QueryParam("userId") Integer userId) {
        int isValid = JWT.validateJWT(jwt);
        if (isValid != 1) {
            if (isValid == 2) {
                return Response.status(498).entity("InvalidToken")
                        .type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(401).entity("TokenExpired")
                        .type(MediaType.APPLICATION_JSON).build();
            }
        }

        ShippingAddress shippingAddress = shippingAddressService.getShippingAddressByUserId(userId);
        if (shippingAddress == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new JSONObject().put("success", false)
                            .put("message", "Shipping address not found for the given user id")
                            .toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // JSON objektum összeállítása
        JSONObject json = new JSONObject();
        json.put("shippingAddressId", shippingAddress.getShippingAddressId());
        json.put("userId", shippingAddress.getUserId());
        json.put("recipientName", shippingAddress.getRecipientName());
        json.put("addressLine1", shippingAddress.getAddressLine1());
        json.put("addressLine2", shippingAddress.getAddressLine2());
        json.put("city", shippingAddress.getCity());
        json.put("state", shippingAddress.getState());
        json.put("postalCode", shippingAddress.getPostalCode());
        json.put("country", shippingAddress.getCountry());
        json.put("phone", shippingAddress.getPhone());
        json.put("createdAt", shippingAddress.getCreatedAt());
        json.put("updatedAt", shippingAddress.getUpdatedAt());

        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

    // GET: Lekéri az összes ShippingAddress bejegyzést
    @GET
    @Path("/getAllShippingAddresses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllShippingAddresses(@HeaderParam("token") String jwt) {
        int isValid = JWT.validateJWT(jwt);
        if (isValid != 1) {
            if (isValid == 2) {
                return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
            }
        }

        List<ShippingAddress> list = shippingAddressService.getAllShippingAddresses();
        JSONArray array = new JSONArray();
        for (ShippingAddress s : list) {
            JSONObject obj = new JSONObject();
            obj.put("shippingAddressId", s.getShippingAddressId());
            obj.put("userId", s.getUserId());
            obj.put("recipientName", s.getRecipientName());
            obj.put("addressLine1", s.getAddressLine1());
            obj.put("addressLine2", s.getAddressLine2());
            obj.put("city", s.getCity());
            obj.put("state", s.getState());
            obj.put("postalCode", s.getPostalCode());
            obj.put("country", s.getCountry());
            obj.put("phone", s.getPhone());
            obj.put("createdAt", s.getCreatedAt());
            obj.put("updatedAt", s.getUpdatedAt());
            array.put(obj);
        }

        JSONObject response = new JSONObject();
        response.put("shippingAddresses", array);
        response.put("success", true);

        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

    // POST: Új ShippingAddress létrehozása
    @POST
    @Path("/addShippingAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addShippingAddress(@HeaderParam("token") String jwt, ShippingAddress shippingAddress) {
        int isValid = JWT.validateJWT(jwt);
        if (isValid != 1) {
            if (isValid == 2) {
                return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
            }
        }

        try {
            ShippingAddress created = shippingAddressService.createShippingAddress(shippingAddress);
            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("shippingAddressId", created.getShippingAddressId());
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("success", false);
            response.put("message", "Hiba történt: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    // PUT: Létező ShippingAddress módosítása
    @PUT
    @Path("/updateShippingAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateShippingAddress(@HeaderParam("token") String jwt, ShippingAddress shippingAddress) {
        int isValid = JWT.validateJWT(jwt);
        if (isValid != 1) {
            if (isValid == 2) {
                return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
            }
        }

        try {
            ShippingAddress updated = shippingAddressService.updateShippingAddress(shippingAddress);
            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("shippingAddressId", updated.getShippingAddressId());
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("success", false);
            response.put("message", "Hiba történt: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    // DELETE: ShippingAddress törlése az azonosító alapján
    @DELETE
    @Path("/deleteShippingAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteShippingAddress(@HeaderParam("token") String jwt,
            @QueryParam("shippingAddressId") Integer shippingAddressId) {
        int isValid = JWT.validateJWT(jwt);
        if (isValid != 1) {
            if (isValid == 2) {
                return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
            }
        }

        try {
            shippingAddressService.deleteShippingAddress(shippingAddressId);
            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("message", "Shipping address törölve.");
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("success", false);
            response.put("message", "Hiba történt: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
