/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.controller;

import com.maven.vintage_project.model.Category;
import com.maven.vintage_project.model.Products;
import com.maven.vintage_project.service.ProductService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONObject;

@Path("/product")
public class ProductController {

    private final ProductService productService = new ProductService();

    @GET
    @Path("/getProductById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductById(@QueryParam("id") Integer id) {
        JSONObject result = productService.getProductById(id);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getAllProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProducts() {
        JSONObject result = productService.getAllProducts();
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/addProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(MultipartFormDataInput input) {
        try {
            Products product = new Products();

            String name = input.getFormDataMap().get("name").get(0).getBodyAsString();
            String description = input.getFormDataMap().get("description").get(0).getBodyAsString();
            Double price = Double.parseDouble(input.getFormDataMap().get("price").get(0).getBodyAsString());
            Integer stock = Integer.parseInt(input.getFormDataMap().get("stockQuanity").get(0).getBodyAsString());
            Integer categoryId = Integer.parseInt(input.getFormDataMap().get("categoryId").get(0).getBodyAsString());

            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuanty(stock);

            // Csak ID-t állítunk be, nem töltjük be teljes Category-t
            Category category = new Category();
            category.setId(categoryId);
            product.setCategory(category);

            JSONObject result = productService.addProductWithImage(product, input);
            return Response.status(result.getInt("statusCode")).entity(result.toString()).build();

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("status", "ParsingError");
            error.put("statusCode", 400);
            error.put("error", e.getMessage());
            return Response.status(400).entity(error.toString()).build();
        }
    }
}
