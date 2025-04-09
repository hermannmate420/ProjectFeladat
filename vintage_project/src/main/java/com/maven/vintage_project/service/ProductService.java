/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Category;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONArray;
import org.json.JSONObject;
import com.maven.vintage_project.model.Products;
import java.text.SimpleDateFormat;

public class ProductService {

    private static final String UPLOAD_DIR = "C:\\wildfly\\standalone\\deployments\\uploads\\products\\";
    private final Products model = new Products();


    /*public JSONObject getProductById(Integer id) {
        JSONObject response = new JSONObject();
        try {
            Products product = pf.find(Products.class, id);
            if (product == null) {
                response.put("status", "ProductNotFound").put("statusCode", 404);
                return response;
            }

            JSONObject obj = buildProductJson(product);
            response.put("status", "success").put("statusCode", 200).put("result", obj);
        } catch (Exception e) {
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        }
        return response;
    }*/
    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public JSONObject createProduct(Products p, boolean isAdmin, MultipartFormDataInput input) {
        JSONObject response = new JSONObject();
        File savedFile = null;
        String filename = null;

        if (!isAdmin) {
            return response.put("success", false).put("message", "Nincs jogosultság a művelethez.");
        }

        try {
            // Slug generálása, ha nincs megadva
            if (p.getSlug() == null || p.getSlug().isEmpty()) {
                String slug = p.getName()
                        .toLowerCase()
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("^-|-$", "");
                p.setSlug(slug);
            }

            // Meta title generálása, ha nincs megadva
            if (p.getMetaTitle() == null || p.getMetaTitle().isEmpty()) {
                p.setMetaTitle("Vásárolható termék: " + p.getName());
            }

            // Képfeltöltés kezelése
            List<InputPart> fileParts = input.getFormDataMap().get("file");
            if (fileParts != null && !fileParts.isEmpty()) {
                InputPart filePart = fileParts.get(0);
                InputStream inputStream = filePart.getBody(InputStream.class, null);

                // Fájlnév kinyerése
                String disposition = filePart.getHeaders().getFirst("Content-Disposition");
                String originalFilename = disposition.replaceAll(".*filename=\"([^\"]+)\".*", "$1");
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();

                if (!extension.matches("jpg|jpeg|png")) {
                    return response.put("success", false).put("message", "Nem támogatott fájltípus.");
                }

                filename = "product_" + System.currentTimeMillis() + "." + extension;
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                savedFile = new File(dir, filename);
                Files.copy(inputStream, savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                p.setImageUrl("/uploads/products/" + filename);
            } else {
                p.setImageUrl("/uploads/products/default.jpg");
            }

            // Termék mentése model rétegen keresztül
            boolean result = model.createProduct(p);

            if (!result && savedFile != null && savedFile.exists()) {
                savedFile.delete(); //Törlés, ha sikertelen a mentés
            }

            return response.put("success", result)
                    .put("message", result ? "Termék sikeresen létrehozva." : "Mentés sikertelen.");

        } catch (Exception e) {
            if (savedFile != null && savedFile.exists()) {
                savedFile.delete();
            }
            e.printStackTrace();
            return response.put("success", false)
                    .put("message", "Hiba történt a termék létrehozásakor: " + e.getMessage());
        }
    }

    public JSONObject getAllProducts() {
        JSONObject response = new JSONObject();
        JSONArray productsArray = new JSONArray();

        try {
            List<Products> productList = model.getAllProducts();

            if (productList == null || productList.isEmpty()) {
                response.put("success", false).put("message", "Nincs elérhető termék.");
                return response;
            }

            for (Products p : productList) {
                if (p.getDeletedAt() != null) {
                    continue; // skip soft-deleted
                }
                JSONObject productJson = new JSONObject();
                productJson.put("id", p.getProductId());
                productJson.put("name", p.getName());
                productJson.put("slug", p.getSlug());
                productJson.put("metaTitle", p.getMetaTitle());
                productJson.put("description", p.getDescription());
                productJson.put("price", p.getPrice());
                productJson.put("discountPrice", p.getDiscountPrice());
                productJson.put("stockQuanty", p.getStockQuanty());
                productJson.put("status", p.getStatus());
                productJson.put("imageUrl", p.getImageUrl());
                productJson.put("createdAt", formatDate(p.getCreatedAt()));
                productJson.put("updatedAt", formatDate(p.getUpdatedAt()));
                productJson.put("categoryId", p.getCategory() != null ? p.getCategory().getId() : null);

                productsArray.put(productJson);
            }

            response.put("success", true);
            response.put("products", productsArray);
            response.put("count", productsArray.length());

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false).put("message", "Hiba a lekérdezés során: " + e.getMessage());
        }

        return response;
    }

    public JSONObject getProductById(Integer id, boolean isAdmin) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        int statusCode = 200;

        Products modelResult = new Products(id);  // Ezzel betöltjük a terméket
        System.out.println("id" + modelResult.getProductId() + " " + modelResult.getName());

        // Ha nem admin, csak az 'active' termékeket látja
        if (!isAdmin && !"active".equalsIgnoreCase(modelResult.getStatus())) {
            status = "ProductNotFound";
            statusCode = 417;
        } else {
            JSONObject productJson = buildProductJson(modelResult);
            toReturn.put("result", productJson);
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    private JSONObject buildProductJson(Products product) {
        JSONObject obj = new JSONObject();
        obj.put("productId", product.getProductId());
        obj.put("name", product.getName());
        obj.put("description", product.getDescription());
        obj.put("price", product.getPrice());
        obj.put("stockQuanty", product.getStockQuanty());
        obj.put("productPicture", product.getImageUrl());

        if (product.getCategory() != null) {
            JSONObject categoryObj = new JSONObject();
            categoryObj.put("id", product.getCategory().getId());
            // Ha a Category entitásban van nevét tartalmazó mező, például:
            categoryObj.put("name", product.getCategory().getName());
            obj.put("category", categoryObj);
        } else {
            obj.put("category", JSONObject.NULL);
        }

        obj.put("status", product.getStatus());
        obj.put("createdAt", product.getCreatedAt());
        obj.put("updatedAt", product.getUpdatedAt());
        obj.put("deletedAt", product.getDeletedAt());

        return obj;
    }

    public JSONObject updateProduct(Products p, boolean isAdmin) {
        JSONObject response = new JSONObject();

        if (!isAdmin) {
            return response.put("success", false).put("message", "Nincs jogosultság.");
        }

        try {
            // Slug generálása, ha hiányzik
            if (p.getSlug() == null || p.getSlug().isEmpty()) {
                String slug = p.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
                p.setSlug(slug);
            }

            // Meta title generálása, ha hiányzik
            if (p.getMetaTitle() == null || p.getMetaTitle().isEmpty()) {
                p.setMetaTitle("Vásárolható termék: " + p.getName());
            }

            boolean result = model.updateProduct(p);

            return response.put("success", result)
                    .put("message", result ? "Sikeres frissítés." : "Frissítés sikertelen.");

        } catch (Exception e) {
            e.printStackTrace();
            return response.put("success", false)
                    .put("message", "Hiba frissítés közben: " + e.getMessage());
        }
    }

}
