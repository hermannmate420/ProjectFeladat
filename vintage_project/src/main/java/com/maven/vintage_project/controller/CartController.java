package com.maven.vintage_project.controller;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.Cart;
import com.maven.vintage_project.service.CartService;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author herma
 */
@Path("/cart")
@Stateless
public class CartController {

    @Inject
    private CartService cartService;

    @GET
    @Path("/getCart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@HeaderParam("token") String jwt) {
        int valid = JWT.validateJWT(jwt);
        if (valid != 1) {
            return Response.status(valid == 2 ? 498 : 401)
                    .entity("{\"error\"invalid or expired token\"}")
                    .build();
        }

        Integer userId = JWT.getUserIdByToken(jwt);

        Cart cart = cartService.getCart(userId);

        return Response.ok(cart).build();
    }

    @POST
    @Path("/addItem")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItem(@HeaderParam("token") String jwt, String jsonData) {
        // Parse jsonData; expect something like: {"userId":1, "productId":5, "quantity":3}
        System.out.println("Received JSON: " + jsonData);  // Debug log

        JSONObject input = new JSONObject(jsonData);
        if (!input.has("userId")) {
            return Response.status(400).entity("{\"error\": \"Missing userId\"}").build();
        }
        Integer userId = input.getInt("userId");

        if (!input.has("items")) {
            return Response.status(400).entity("{\"error\": \"Missing items\"}").build();
        }
        Object itemsObj = input.get("items");
        System.out.println("Type of 'items': " + itemsObj.getClass().getName());

        JSONArray itemsArray = null;
        if (itemsObj instanceof JSONArray) {
            itemsArray = (JSONArray) itemsObj;
        } else if (itemsObj instanceof JSONObject) {
            // If it's a single JSONObject, wrap it in an array.
            itemsArray = new JSONArray();
            itemsArray.put((JSONObject) itemsObj);
        } else {
            return Response.status(400).entity("{\"error\": \"Invalid format for items\"}").build();
        }

        // Log how many items we have.
        System.out.println("Number of items: " + itemsArray.length());

        int isValid = JWT.validateJWT(jwt);
        if (isValid != 1) {
            return Response.status(isValid == 2 ? 498 : 401)
                    .entity(new JSONObject().put("error", "Invalid or expired token").toString())
                    .build();
        }

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemJson = itemsArray.getJSONObject(i);
            System.out.println("Processing item: " + itemJson.toString());
            int productId = itemJson.getInt("productId");
            int quantity = itemJson.getInt("quantity");

            System.out.println("Adding product with ID " + productId + " quantity " + quantity + " for user " + userId);
            cartService.addItemToCart(userId, productId, quantity);
        }

        Cart updatedCart = cartService.getCart(userId);
        return Response.ok(updatedCart).build();
    }

    // Remove an item from the cart.
    @DELETE
    @Path("/removeItem")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeItemFromCart(
            @HeaderParam("token") String jwt,
            @QueryParam("productId") Integer productId) {

        int valid = JWT.validateJWT(jwt);
        if (valid != 1) {
            return Response.status(valid == 2 ? 498 : 401)
                    .entity("{\"error\":\"invalid or expired token\"}")
                    .build();
        }

        Integer userId = JWT.getUserIdByToken(jwt);

        Cart cart = cartService.removeItemFromCart(userId, productId);

        return Response.ok(cart).build();
    }

    @PUT
    @Path("/updateItemQuantity")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateItemQuantity(
            @HeaderParam("token") String jwt,
            @QueryParam("productId") Integer productId,
            @QueryParam("newQuantity") Integer newQuantity) {

        int valid = JWT.validateJWT(jwt);
        if (valid != 1) {
            return Response.status(valid == 2 ? 498 : 401)
                    .entity("{\"error\":\"invalid or expired token\"}")
                    .build();
        }

        Integer userId = JWT.getUserIdByToken(jwt);

        Cart cart = cartService.updateItemQuantity(userId, productId, newQuantity);

        return Response.ok(cart).build();
    }
}
