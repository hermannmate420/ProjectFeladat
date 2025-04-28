package com.maven.vintage_project.controller;

import com.maven.vintage_project.service.CategoryService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/category")
public class CategoryController {

    private final CategoryService categoryService = new CategoryService();

    // GET: /category/getAll
    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories() {
        JSONObject result = categoryService.getAllCategories();
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }

    // GET: /category/getById?categoryId=1
    @GET
    @Path("/getById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryById(@QueryParam("categoryId") Integer categoryId) {
        JSONObject result = categoryService.getCategoryById(categoryId);
        return Response.status(result.optInt("statusCode", 500))
                .entity(result.toString())
                .build();
    }
}
