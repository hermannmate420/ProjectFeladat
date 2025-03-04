/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.controller;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.User;
import com.maven.vintage_project.service.UserService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
    public Response registerUser(String bodyString) {
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

        JSONObject obj = layer.registerUser(u);
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
    }
    
    /*@POST
    @Path("registerAdmin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAdmin(String bodyString) {
        JSONObject body = new JSONObject(bodyString);
        
        User u = new User(
                body.getString("user")
        )
    }*/
    
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
}
