package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Comment;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommentService {

    // Segédfüggvény az EntityManager létrehozásához
    private EntityManager getEntityManager() {
        return Persistence
                .createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU")
                .createEntityManager();
    }

    // 1) getCommentById
    public JSONObject getCommentsByBlogId(Integer blogId) {
        JSONObject response = new JSONObject();
        if (blogId == null) {
            response.put("status", "MissingParameter")
                    .put("statusCode", 400)
                    .put("error", "The blogId parameter cannot be null.");
            return response;
        }
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getCommentsByBlogId", Comment.class);
            spq.registerStoredProcedureParameter("p_blog_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_blog_id", blogId);
            List<Comment> comments = spq.getResultList();
            // tovább a JSON építés...
            if (comments.isEmpty()) {
                response.put("status", "NoCommentsFound").put("statusCode", 404);
            } else {
                JSONArray resultArray = new JSONArray();
                for (Comment c : comments) {
                    JSONObject commentJson = new JSONObject();
                    commentJson.put("commentId", c.getCommentId());
                    commentJson.put("blogId", c.getBlogId());
                    commentJson.put("commentText", c.getCommentText());
                    commentJson.put("commenter", c.getCommenter());
                    commentJson.put("createdAt", c.getCreatedAt());
                    resultArray.put(commentJson);
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

    // 2) getAllComments
    public JSONObject getAllComments() {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllComments", Comment.class);
            List<Comment> comments = spq.getResultList();

            if (comments.isEmpty()) {
                response.put("status", "NoCommentsFound").put("statusCode", 404);
            } else {
                JSONArray resultArray = new JSONArray();
                for (Comment c : comments) {
                    JSONObject commentJson = new JSONObject();
                    commentJson.put("commentId", c.getCommentId());
                    commentJson.put("blogId", c.getBlogId());
                    commentJson.put("commentText", c.getCommentText());
                    commentJson.put("commenter", c.getCommenter());
                    commentJson.put("createdAt", c.getCreatedAt());
                    resultArray.put(commentJson);
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

    // 3) createComment
    public JSONObject createComment(Comment comment) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createComment");
            spq.registerStoredProcedureParameter("p_blog_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_comment_text", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_commenter", String.class, ParameterMode.IN);

            spq.setParameter("p_blog_id", comment.getBlogId());
            spq.setParameter("p_comment_text", comment.getCommentText());
            spq.setParameter("p_commenter", comment.getCommenter());

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

    // 4) updateComment – frissítjük a comment_text-et egy komment ID alapján
    public JSONObject updateComment(Integer commentId, String newCommentText) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateComment");
            spq.registerStoredProcedureParameter("p_comment_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_comment_text", String.class, ParameterMode.IN);

            spq.setParameter("p_comment_id", commentId);
            spq.setParameter("p_comment_text", newCommentText);

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

    // 5) deleteComment – törlés stored procedure segítségével
    public JSONObject deleteComment(Integer commentId) {
        JSONObject response = new JSONObject();
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("deleteComment");
            spq.registerStoredProcedureParameter("p_comment_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_comment_id", commentId);

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
}
