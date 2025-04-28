package com.maven.vintage_project.controller;

import com.maven.vintage_project.model.Comment;
import com.maven.vintage_project.service.CommentService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/comment")
public class CommentController {

    private final CommentService commentService = new CommentService();

    // GET: /comment/getByBlogId?commentId=1
    @GET
    @Path("/getByBlogId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommentsByBlogId(@QueryParam("blogId") Integer blogId) {
        JSONObject result = commentService.getCommentsByBlogId(blogId);
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // GET: /comment/getAll
    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllComments() {
        JSONObject result = commentService.getAllComments();
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // POST: /comment/create
    // Példa JSON bemenet:
    // {
    //    "blogId": 1,
    //    "commentText": "Ez egy komment",
    //    "commenter": "Kommentelő Név"
    // }
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComment(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            Comment comment = new Comment();
            comment.setBlogId(body.getInt("blogId"));
            comment.setCommentText(body.getString("commentText"));
            comment.setCommenter(body.getString("commenter"));
            // createdAt automatikusan az adatbázisban lesz beállítva, ha van DEFAULT CURRENT_TIMESTAMP

            JSONObject result = commentService.createComment(comment);
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

    // PUT: /comment/update
    // Példa JSON bemenet:
    // {
    //   "commentId": 1,
    //   "commentText": "Frissített komment szöveg"
    // }
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateComment(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            Integer commentId = body.getInt("commentId");
            String newCommentText = body.getString("commentText");
            JSONObject result = commentService.updateComment(commentId, newCommentText);
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

    // DELETE: /comment/delete?commentId=1
    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteComment(@QueryParam("commentId") Integer commentId) {
        JSONObject result = commentService.deleteComment(commentId);
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }
}
