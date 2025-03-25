/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.controller;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.User;
import com.maven.vintage_project.service.UserService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import org.glassfish.jersey.media.multipart.*;
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
    private static final String UPLOAD_DIR = "/var/www/uploads/";
    
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
    @Path("sendEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmail(@HeaderParam("token") String jwt, String bodyString) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid == 1) {
            JSONObject body = new JSONObject(bodyString);
            
            Boolean obj = User.sendEmail(body.getString("to"), body.getBoolean("ccMe"));
            return Response.status(200).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
        } else if (isValid == 2) {
            return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
        }

    }
    
    @POST
    @Path("/{id}/upload-profile-picture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProfilePicture(
            @PathParam("id") Integer userId,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData) {
        try {
            // Fájlnév beállítása és mentése
            String fileName = "profile_" + userId + "_" + fileMetaData.getFileName();
            String filePath = UPLOAD_DIR + fileName;

            File file = new File(filePath);
            try (FileOutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
            JSONObject result = layer.updateProfilePicture(userId, filePath);

            // HTTP státusz kód beállítása a JSON válasz alapján
            int statusCode = result.getInt("status");
            return Response.status(200).entity("{\"status\":200,\"message\":\"File uploaded successfully\",\"path\":\"" + filePath + "\"}").build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity("{\"status\":500,\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}