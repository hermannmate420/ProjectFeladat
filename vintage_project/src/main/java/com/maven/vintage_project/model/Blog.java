package com.maven.vintage_project.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "blogs")
public class Blog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Integer blogId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "author_id", nullable = false)
    private Integer authorId;

    @Column(name = "likes_count")
    private Integer likesCount;

    @Column(name = "post_comment_id")
    private Integer postCommentId;

    // Új mező: kép URL
    @Column(name = "image_url")
    private String imageUrl;

    public Blog() {
    }

    public Blog(Integer blogId, String title, String content, String author, Date createdAt,
            Integer authorId, Integer likesCount, Integer postCommentId, String imageUrl) {
        this.blogId = blogId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.authorId = authorId;
        this.likesCount = likesCount;
        this.postCommentId = postCommentId;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public Integer getBlogId() {
        return blogId;
    }

    public void setBlogId(Integer blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getPostCommentId() {
        return postCommentId;
    }

    public void setPostCommentId(Integer postCommentId) {
        this.postCommentId = postCommentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
