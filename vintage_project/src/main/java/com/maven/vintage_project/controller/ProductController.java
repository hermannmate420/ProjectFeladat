/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.controller;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.Category;
import com.maven.vintage_project.model.Products;
import com.maven.vintage_project.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONObject;

@Path("/product")
public class ProductController {

    private final ProductService productService = new ProductService();

    @GET
    @Path("getProductById")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductById(@HeaderParam("token") String jwt, @QueryParam("productId") Integer productId) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid == 1) {
            boolean isAdmin = JWT.isAdmin(jwt);

            if (!isAdmin) {
                return Response.status(403).entity("Forbidden").type(MediaType.APPLICATION_JSON).build();
            }

            JSONObject obj = productService.getProductById(productId, true);
            return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();

        } else if (isValid == 2) {
            return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
        }
    }

    private String getValue(Map<String, List<InputPart>> form, String key) throws Exception {
        List<InputPart> parts = form.get(key);
        if (parts == null || parts.isEmpty()) {
            throw new Exception(key + " mező hiányzik.");
        }
        String value = parts.get(0).getBodyAsString().trim();
        return value.isEmpty() ? "default_value" : value;  // Alapértelmezett érték
    }

    private String getOptionalValue(Map<String, List<InputPart>> form, String key) {
        try {
            List<InputPart> parts = form.get(key);
            if (parts == null || parts.isEmpty()) {
                return null;  // Ha nincs adat, null
            }
            return parts.get(0).getBodyAsString().trim();  // Visszaadjuk a string értéket
        } catch (Exception e) {
            return null;  // Ha hiba van, null
        }
    }

    @POST
    @Path("/addProduct")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(@HeaderParam("token") String jwt, MultipartFormDataInput input) {

        int isValid = JWT.validateJWT(jwt);

        if (isValid != 1) {
            if (isValid == 2) {
                return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
            }
        }

        boolean isAdmin = JWT.isAdmin(jwt); // Jogosultság

        try {
            Map<String, List<InputPart>> form = input.getFormDataMap();

            // Kategória ID beállítása
            Category category = new Category();
            category.setId(Integer.parseInt(getValue(form, "categoryId")));

            // Alap termék objektum létrehozása konstruktorral
            Products product = new Products(
                    getValue(form, "name"),
                    getValue(form, "description"),
                    new BigDecimal(getValue(form, "price")),
                    Integer.parseInt(getValue(form, "stockQuanty")),
                    getValue(form, "status"),
                    category
            );

            // Opcionális mezők
            String discount = getOptionalValue(form, "discountPrice");
            if (discount != null && !discount.equalsIgnoreCase("null") && !discount.isEmpty()) {
                product.setDiscountPrice(new BigDecimal(discount));
            }

            // Slug, MetaTitle is generálódik a Service-ben, ha nincs
            // Service meghívása
            JSONObject response = productService.createProduct(product, isAdmin, input);

            return Response.status(response.getBoolean("success") ? 200 : 400)
                    .entity(response.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new JSONObject()
                            .put("success", false)
                            .put("message", "Hiányzó vagy hibás mezők: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/getAllProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProducts() {
        JSONObject response = productService.getAllProducts();

        return Response.status(response.optBoolean("success", false) ? 200 : 400)
                .entity(response.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("/updateProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(@HeaderParam("token") String jwt, Products p) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid != 1) {
            return Response.status(isValid == 2 ? 498 : 401)
                    .entity("Hibás vagy lejárt token")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        boolean isAdmin = JWT.isAdmin(jwt);

        JSONObject response = productService.updateProduct(p, isAdmin);

        return Response.status(response.getBoolean("success") ? 200 : 400)
                .entity(response.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
