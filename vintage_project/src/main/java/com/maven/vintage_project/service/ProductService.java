/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Products;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import net.coobird.thumbnailator.Thumbnails;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProductService {

    private EntityManager em = Persistence.createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU").createEntityManager();
    private static final String UPLOAD_DIR = "C:\\wildfly\\standalone\\deployments\\uploads\\products\\";

    public JSONObject getProductById(Integer id) {
        JSONObject response = new JSONObject();
        try {
            Products product = em.find(Products.class, id);
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
    }

    public JSONObject getAllProducts() {
        JSONObject response = new JSONObject();
        try {
            TypedQuery<Products> query = em.createQuery("SELECT p FROM Products p", Products.class);
            List<Products> products = query.getResultList();
            JSONArray array = new JSONArray();

            for (Products p : products) {
                array.put(buildProductJson(p));
            }

            response.put("status", "success").put("statusCode", 200).put("result", array);
        } catch (Exception e) {
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        }
        return response;
    }

    public JSONObject addProductWithImage(Products product, MultipartFormDataInput input) {
        JSONObject response = new JSONObject();

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Kép feldolgozása
            List<InputPart> fileParts = input.getFormDataMap().get("file");
            if (fileParts == null || fileParts.isEmpty()) {
                response.put("status", "NoFileUploaded").put("statusCode", 400);
                return response;
            }

            InputPart filePart = fileParts.get(0);
            InputStream fileInputStream = filePart.getBody(InputStream.class, null);

            // Fájlnév kinyerése
            String contentDisposition = filePart.getHeaders().getFirst("Content-Disposition");
            Pattern pattern = Pattern.compile("filename=\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(contentDisposition);
            String originalFilename = matcher.find() ? matcher.group(1) : "product.jpg";
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();

            if (!extension.matches("jpg|jpeg|png")) {
                response.put("status", "UnsupportedFileType").put("statusCode", 415);
                return response;
            }

            String filename = "product_" + System.currentTimeMillis() + "." + extension;
            File targetFile = new File(UPLOAD_DIR + filename);

            Files.copy(fileInputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Átméretezés
            Thumbnails.of(targetFile)
                    .size(500, 500)
                    .outputFormat(extension)
                    .toFile(targetFile);

            // Termék mentése
            product.setProductPicture("/webresources/product/uploads/products/" + filename);

            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();

            response.put("status", "success").put("statusCode", 201).put("message", "Product added successfully");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        }

        return response;
    }

    private JSONObject buildProductJson(Products product) {
        JSONObject obj = new JSONObject();
        obj.put("productId", product.getProductId());
        obj.put("name", product.getName());
        obj.put("description", product.getDescription());
        obj.put("price", product.getPrice());
        obj.put("stockQuanty", product.getStockQuanty());
        obj.put("productPicture", product.getProductPicture());

        JSONObject categoryObj = new JSONObject();
        categoryObj.put("id", product.getCategory().getId());
        categoryObj.put("name", product.getCategory().getName());

        obj.put("category", categoryObj);
        return obj;
    }
}
