package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Blog;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import net.coobird.thumbnailator.Thumbnails;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

public class BlogService {

    private EntityManagerFactory emf;

    public BlogService() {
        emf = Persistence.createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU");
    }

    private static final String UPLOAD_DIR = "C:\\wildfly\\standalone\\deployments\\uploads\\blogs\\";

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Create blog via stored procedure
    public JSONObject createBlog(Blog blog) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createBlog");
            spq.registerStoredProcedureParameter("p_title", String.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_content", String.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_author", String.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_author_id", Integer.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_likes_count", Integer.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_post_comment_id", Integer.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_image_url", String.class, javax.persistence.ParameterMode.IN);
            // Output paraméter: generált blog ID
            spq.registerStoredProcedureParameter("p_blog_id", Integer.class, ParameterMode.OUT);

            spq.setParameter("p_title", blog.getTitle());
            spq.setParameter("p_content", blog.getContent());
            spq.setParameter("p_author", blog.getAuthor());
            spq.setParameter("p_author_id", blog.getAuthorId());
            spq.setParameter("p_likes_count", blog.getLikesCount() != null ? blog.getLikesCount() : 0);
            spq.setParameter("p_post_comment_id", blog.getPostCommentId() != null ? blog.getPostCommentId() : 0);
            spq.setParameter("p_image_url", blog.getImageUrl() != null ? blog.getImageUrl() : "");

            em.getTransaction().begin();
            spq.execute();

            Integer generatedId = (Integer) spq.getOutputParameterValue("p_blog_id");

            em.getTransaction().commit();

            // Kiolvassuk az output paraméterből a generált blog ID-t
            response.put("status", "success");
            response.put("statusCode", 200);
            response.put("blogId", generatedId);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            response.put("status", "Error");
            response.put("statusCode", 500);
            response.put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // Update blog via stored procedure
    public JSONObject updateBlog(Blog blog) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateBlog");
            spq.registerStoredProcedureParameter("p_blog_id", Integer.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_title", String.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_content", String.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_author", String.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_author_id", Integer.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_likes_count", Integer.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_post_comment_id", Integer.class, javax.persistence.ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_image_url", String.class, javax.persistence.ParameterMode.IN);

            spq.setParameter("p_blog_id", blog.getBlogId());
            spq.setParameter("p_title", blog.getTitle());
            spq.setParameter("p_content", blog.getContent());
            spq.setParameter("p_author", blog.getAuthor());
            spq.setParameter("p_author_id", blog.getAuthorId());
            spq.setParameter("p_likes_count", blog.getLikesCount() != null ? blog.getLikesCount() : 0);
            spq.setParameter("p_post_comment_id", blog.getPostCommentId() != null ? blog.getPostCommentId() : 0);
            spq.setParameter("p_image_url", blog.getImageUrl() != null ? blog.getImageUrl() : "");

            em.getTransaction().begin();
            spq.execute();
            em.getTransaction().commit();
            response.put("status", "success").put("statusCode", 200);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // Get blog by ID via stored procedure
    public JSONObject getBlogById(Integer id) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getBlogById", Blog.class);
            spq.registerStoredProcedureParameter("p_blog_id", Integer.class, javax.persistence.ParameterMode.IN);
            spq.setParameter("p_blog_id", id);
            Blog blog;
            try {
                blog = (Blog) spq.getSingleResult();
            } catch (NoResultException nre) {
                blog = null;
            }
            if (blog == null) {
                response.put("status", "BlogNotFound").put("statusCode", 404);
            } else {
                JSONObject blogJson = new JSONObject();
                blogJson.put("blogId", blog.getBlogId());
                blogJson.put("title", blog.getTitle());
                blogJson.put("content", blog.getContent());
                blogJson.put("author", blog.getAuthor());
                blogJson.put("createdAt", blog.getCreatedAt());
                blogJson.put("authorId", blog.getAuthorId());
                blogJson.put("likesCount", blog.getLikesCount());
                blogJson.put("postCommentId", blog.getPostCommentId());
                blogJson.put("imageUrl", blog.getImageUrl());
                response.put("status", "success");
                response.put("statusCode", 200);
                response.put("result", blogJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // Get all blogs via stored procedure
    public JSONObject getAllBlogs() {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllBlogs", Blog.class);
            List<Blog> blogs = spq.getResultList();
            if (blogs.isEmpty()) {
                response.put("status", "NoBlogsFound").put("statusCode", 404);
            } else {
                JSONArray resultArray = new JSONArray();
                for (Blog blog : blogs) {
                    JSONObject blogJson = new JSONObject();
                    blogJson.put("blogId", blog.getBlogId());
                    blogJson.put("title", blog.getTitle());
                    blogJson.put("content", blog.getContent());
                    blogJson.put("author", blog.getAuthor());
                    blogJson.put("createdAt", blog.getCreatedAt());
                    blogJson.put("authorId", blog.getAuthorId());
                    blogJson.put("likesCount", blog.getLikesCount());
                    blogJson.put("postCommentId", blog.getPostCommentId());
                    blogJson.put("imageUrl", blog.getImageUrl());
                    resultArray.put(blogJson);
                }
                response.put("status", "success").put("statusCode", 200).put("result", resultArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // Delete blog via stored procedure
    public JSONObject deleteBlog(Integer blogId) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("deleteBlog");
            spq.registerStoredProcedureParameter("p_blog_id", Integer.class, javax.persistence.ParameterMode.IN);
            spq.setParameter("p_blog_id", blogId);
            em.getTransaction().begin();
            spq.execute();
            em.getTransaction().commit();
            response.put("status", "success").put("statusCode", 200);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    // Increment blog likes via stored procedure
    public JSONObject incrementLikes(Integer blogId) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("incrementBlogLikes");
            spq.registerStoredProcedureParameter("p_blog_id", Integer.class, javax.persistence.ParameterMode.IN);
            spq.setParameter("p_blog_id", blogId);
            em.getTransaction().begin();
            spq.execute();
            em.getTransaction().commit();
            response.put("status", "success").put("statusCode", 200);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            response.put("status", "Error").put("statusCode", 500).put("error", e.getMessage());
        } finally {
            em.close();
        }
        return response;
    }

    /**
     * Feltölti a blog képét, elmenti a szerveren, opcionálisan átméretezi, majd
     * frissíti a blog rekord imageUrl mezőjét.
     *
     * @param blogId A blog azonosítója.
     * @param input A feltöltött fájl MultipartFormDataInput formában.
     * @return JSON objektum a státusszal, üzenettel és az imageUrl-lel.
     */
    public JSONObject uploadBlogImage(Integer blogId, MultipartFormDataInput input) {
        JSONObject responseJson = new JSONObject();
        try {
            String uploadDirPath = "C:\\wildfly\\standalone\\deployments\\uploads\\blogs";
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Get file parts
            java.util.Map<String, java.util.List<InputPart>> uploadForm = input.getFormDataMap();
            java.util.List<InputPart> fileParts = uploadForm.get("file");
            if (fileParts == null || fileParts.isEmpty()) {
                responseJson.put("status", 400);
                responseJson.put("error", "No file uploaded");
                return responseJson;
            }
            InputPart filePart = fileParts.get(0);
            InputStream fileInputStream = filePart.getBody(InputStream.class, null);

            // Extract filename from header
            String contentDisposition = filePart.getHeaders().getFirst("Content-Disposition");
            Pattern pattern = Pattern.compile("filename=\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(contentDisposition);
            String originalFileName = matcher.find() ? matcher.group(1) : "default.jpg";
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();

            if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("jpeg")) {
                responseJson.put("status", 415);
                responseJson.put("error", "Only PNG and JPG files are allowed.");
                return responseJson;
            }

            // Generate unique file name: blog_<id>_timestamp.ext
            String fileName = "blog_" + blogId + "_" + System.currentTimeMillis() + "." + extension;
            String filePath = uploadDirPath + File.separator + fileName;
            File fileToSave = new File(filePath);
            Files.copy(fileInputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Optional: Resize the image (e.g., to max 800x600)
            Thumbnails.of(fileToSave)
                    .size(800, 600)
                    .outputFormat(extension)
                    .toFile(fileToSave);

            // Construct image URL (typically relative to context)
            String imageUrl = fileName;

            // Update blog record with new imageUrl
            EntityManager em = getEntityManager();
            try {
                em.getTransaction().begin();
                Blog blog = em.find(Blog.class, blogId);
                if (blog == null) {
                    em.getTransaction().rollback();
                    responseJson.put("status", 404);
                    responseJson.put("error", "Blog not found");
                    return responseJson;
                }
                blog.setImageUrl(imageUrl);
                em.merge(blog);
                em.getTransaction().commit();
                responseJson.put("status", 200);
                responseJson.put("message", "Image uploaded and blog updated successfully");
                responseJson.put("imageUrl", imageUrl);
            } finally {
                em.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("status", 500);
            responseJson.put("error", ex.getMessage());
        }
        return responseJson;
    }

    public File getBlogPicture(String fileName) {
        File file = new File(UPLOAD_DIR + fileName);
        return file.exists() ? file : null;
    }
}
