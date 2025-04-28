package com.maven.vintage_project.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "comments")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "blog_id", nullable = false)
    private Integer blogId;

    @Lob
    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @Column(name = "commenter", nullable = false, length = 255)
    private String commenter;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Alapértelmezett konstruktor
    public Comment() {
    }

    // Paraméteres konstruktor
    public Comment(Integer commentId, Integer blogId, String commentText, String commenter, Date createdAt) {
        this.commentId = commentId;
        this.blogId = blogId;
        this.commentText = commentText;
        this.commenter = commenter;
        this.createdAt = createdAt;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getBlogId() {
        return blogId;
    }

    public void setBlogId(Integer blogId) {
        this.blogId = blogId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // Állítsd be a createdAt dátumot, ha szükséges
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment[ commentId=" + commentId + ", blogId=" + blogId
                + ", commenter=" + commenter + ", createdAt=" + createdAt + " ]";
    }

    public boolean getAuthor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
