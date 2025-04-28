package com.maven.vintage_project.controller;

import com.maven.vintage_project.model.Blog;
import com.maven.vintage_project.service.BlogService;
import com.maven.vintage_project.service.ProductService;
import java.io.File;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/blog")
public class BlogController {

    private BlogService layer = new BlogService();
    private final BlogService blogService = new BlogService();

    // GET: /blog/getById?blogId=1
    @GET
    @Path("/getById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogById(@QueryParam("blogId") Integer blogId) {
        JSONObject result = blogService.getBlogById(blogId);
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // GET: /blog/getAll
    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlogs() {
        JSONObject result = blogService.getAllBlogs();
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // POST: /blog/create
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBlog(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            Blog blog = new Blog();
            blog.setTitle(body.getString("title"));
            blog.setContent(body.getString("content"));
            blog.setAuthor(body.getString("author"));
            blog.setAuthorId(body.getInt("authorId"));
            blog.setLikesCount(body.optInt("likesCount", 0));
            blog.setPostCommentId(body.optInt("postCommentId", 0));
            blog.setImageUrl(body.optString("imageUrl", ""));
            JSONObject result = blogService.createBlog(blog);
            return Response.status(result.optInt("statusCode", 500))
                    .entity(result.toString())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "BadRequest");
            error.put("statusCode", 400);
            error.put("error", e.getMessage());
            return Response.status(400)
                    .entity(error.toString())
                    .build();
        }
    }

    // PUT: /blog/update
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBlog(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            Blog blog = new Blog();
            blog.setBlogId(body.getInt("blogId"));
            blog.setTitle(body.getString("title"));
            blog.setContent(body.getString("content"));
            blog.setAuthor(body.getString("author"));
            blog.setAuthorId(body.getInt("authorId"));
            blog.setLikesCount(body.optInt("likesCount", 0));
            blog.setPostCommentId(body.optInt("postCommentId", 0));
            blog.setImageUrl(body.optString("imageUrl", ""));
            JSONObject result = blogService.updateBlog(blog);
            return Response.status(result.optInt("statusCode", 500))
                    .entity(result.toString())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "BadRequest");
            error.put("statusCode", 400);
            error.put("error", e.getMessage());
            return Response.status(400)
                    .entity(error.toString())
                    .build();
        }
    }

    // DELETE: /blog/delete?blogId=1
    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBlog(@QueryParam("blogId") Integer blogId) {
        JSONObject result = blogService.deleteBlog(blogId);
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // PUT: /blog/like
    @PUT
    @Path("/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response likeBlog(String bodyString) {
        try {
            JSONObject body = new JSONObject(bodyString);
            Integer blogId = body.getInt("blogId");
            JSONObject result = blogService.incrementLikes(blogId);
            return Response.status(result.optInt("statusCode", 500))
                    .entity(result.toString())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "BadRequest");
            error.put("statusCode", 400);
            error.put("error", e.getMessage());
            return Response.status(400)
                    .entity(error.toString())
                    .build();
        }
    }

    // POST: /blog/{blogId}/uploadImage
    // Feltölti a bloghoz tartozó képet, majd a blog rekord imageUrl mezőjét frissíti.
    @POST
    @Path("/{blogId}/uploadImage")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadBlogImage(@PathParam("blogId") Integer blogId, MultipartFormDataInput input) {
        JSONObject result = blogService.uploadBlogImage(blogId, input);
        int statusCode = result.optInt("status", 500);
        return Response.status(statusCode)
                .entity(result.toString())
                .build();
    }

    @GET
    @Path("/uploads/blogs/{fileName}")
    public Response getBlogPicture(@PathParam("fileName") String fileName) {
        File file = layer.getBlogPicture(fileName);

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
}
