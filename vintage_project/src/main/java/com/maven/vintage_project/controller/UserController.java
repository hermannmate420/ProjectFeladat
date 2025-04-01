/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.controller;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.User;
import com.maven.vintage_project.service.UserService;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author herma
 */
@Path("user")
public class UserController {

    @Context
    private UriInfo context;
    private UserService layer = new UserService();

    public UserController() {
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String bodyString) {
        JSONObject body = new JSONObject(bodyString);

        JSONObject obj = layer.login(body.getString("email"), body.getString("password"));
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("registerUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(String bodyString) {
        //System.out.println("Received JSON: " + bodyString);
        JSONObject body;
        try {
            body = new JSONObject(bodyString);
        } catch (JSONException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid JSON format").type(MediaType.APPLICATION_JSON).build();
        }

        /*System.out.println("Parsed Data:");
        System.out.println("Username: " + body.optString("username"));
        System.out.println("Email: " + body.optString("email"));
        System.out.println("Password: " + body.optString("password"));*/
        User u = new User(
                body.getString("username"),
                body.getString("firstName"),
                body.getString("lastName"),
                body.getString("email"),
                body.getString("phoneNumber"),
                body.getString("password")
        );

        /*System.out.println("Mapped User Object:");
        System.out.println("Username: " + u.getUsername());
        System.out.println("Email: " + u.getEmail());*/
        JSONObject obj = layer.registerUser(u);
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("registerAdmin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAdmin(@HeaderParam("token") String jwt, String bodyString) {
        JSONObject body;
        try {
            body = new JSONObject(bodyString);
        } catch (JSONException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid JSON format").type(MediaType.APPLICATION_JSON).build();
        }

        User u = new User(
                body.getString("username"),
                body.getString("firstName"),
                body.getString("lastName"),
                body.getString("email"),
                body.getString("phoneNumber"),
                body.getString("password")
        );

        JSONObject obj = layer.registerAdmin(u, jwt);
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();

    }

    @GET
    @Path("getAllUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUser(@HeaderParam("token") String jwt) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid == 1) {
            JSONObject obj = layer.getAllUser();
            return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
        } else if (isValid == 2) {
            return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
        }

    }

    @GET
    @Path("getUserById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserById(@HeaderParam("token") String jwt, @QueryParam("userId") Integer userId) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid == 1) {
            JSONObject obj = layer.getUserById(userId);
            return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
        } else if (isValid == 2) {
            return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
        }

    }

    @PUT
    @Path("changePassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@HeaderParam("token") String jwt, String bodyString) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid != 1) {
            return Response.status(isValid == 2 ? 498 : 401).entity(isValid == 2 ? "InvalidToken" : "TokenExpired").build();
        }
        JSONObject body;
        try {
            body = new JSONObject(bodyString);
        } catch (Exception e) {
            return Response.status(400).entity("Invalid JSON format").type(MediaType.APPLICATION_JSON).build();
        }

        if (!body.has("userId") || !body.has("newPassword")) {
            return Response.status(400).entity("Missing userId or newPassword field").type(MediaType.APPLICATION_JSON).build();
        }

        Integer userId = body.getInt("userId");
        String newPassword = body.getString("newPassword");
        Integer creator = JWT.getUserIdByToken(jwt);

        JSONObject obj = layer.changePassword(userId, newPassword, creator);
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmail(@HeaderParam("token") String jwt, String bodyString) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid == 1) {
            JSONObject body = new JSONObject(bodyString);
            String to = body.getString("to");
            String subject = body.getString("subject");
            String template = body.getString("template");
            JSONObject vars = body.getJSONObject("placeholders");

            Map<String, String> placeholders = new HashMap<>();
            for (String key : vars.keySet()) {
                placeholders.put(key, vars.getString(key));
            }

            JSONObject response = layer.sendEmail(to, subject, template, placeholders);

            return Response.status(response.getInt("status")).entity(response.toString()).build();
        } else if (isValid == 2) {
            return Response.status(498).entity("{\"error\": \"InvalidToken\"}").build();
        } else {
            return Response.status(401).entity("{\"error\": \"TokenExpired\"}").build();
        }
    }

    @POST
    @Path("/{id}/upload-profile-picture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProfilePicture(
            @PathParam("id") Integer userId,
            MultipartFormDataInput input) {

        JSONObject result = layer.uploadAndResizeProfilePicture(userId, input);
        return Response.status(result.getInt("status")).entity(result.toString()).build();
    }

    @GET
    @Path("/uploads/{fileName}")
    public Response getProfilePicture(@PathParam("fileName") String fileName) {
        File file = layer.getProfilePicture(fileName);

        if (file == null || !file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).entity("File not found").build();
        }

        String mimeType = null;
        try {
            mimeType = java.nio.file.Files.probeContentType(file.toPath());
        } catch (Exception e) {
            mimeType = "application/octet-stream"; // Fallback
        }

        return Response.ok(file, mimeType)
                .header("Content-Disposition", "inline; filename=\"" + file.getName() + "\"")
                .build();
    }

    @PUT
    @Path("/{modifierId}/update/{targetUserId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("modifierId") Integer modifierId,
            @PathParam("targetUserId") int targetUserId, User u) {
        System.out.println(">>> JSON received: " + layer);
        JSONObject result = layer.updateUser(modifierId, targetUserId, u);
        return Response.status(result.getInt("status"))
                .entity(result.toString())
                .build();
    }

    @POST
    @Path("/forgot-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(Map<String, String> payload) {
        String email = payload.get("email");
        System.out.println("[DEBUG] Forgot password request received for email: " + email);

        try {
            boolean success = layer.resetPasswordWithoutToken(email);

            System.out.println("[DEBUG] Controller példányosított UserService: " + layer.getClass().getName());
            if (success) {
                System.out.println("[DEBUG] Reset link successfully sent to: " + email);
                return Response.ok("{\"message\": \"Reset link sent.\"}").build();
            } else {
                System.out.println("[DEBUG] No user found with email: " + email);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"User not found.\"}").build();
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in forgotPassword: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Something went wrong.\"}")
                    .build();
        }
    }

    @PUT
    @Path("/deleteUser/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logicalDeleteUser(
            @PathParam("id") Integer targetUserId,
            @HeaderParam("token") String token) {
        try {
            Integer requesterId = JWT.getUserIdByToken(token);
            Boolean isAdmin = JWT.isAdmin(token);

            JSONObject result = layer.logicallyDeleteUser(targetUserId, requesterId, isAdmin);
            int code = result.optInt("statusCode", 500);
            return Response.status(code).entity(result.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid or missing token.\"}")
                    .build();
        }
    }

}
