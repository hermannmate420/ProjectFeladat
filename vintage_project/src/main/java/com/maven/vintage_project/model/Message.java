package com.maven.vintage_project.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "messages") // Pontosan egyezzen az adatbázis tábla nevével
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    // 'message' oszlopra figyelni kell, hogy ne ütközzön a Java 'message' szóval
    @Column(name = "message", nullable = false, length = 255)
    private String messageText;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    // Az adatbázisban 'Subject' a neve, nagy S-sel. Ezt tartsd, vagy módosítsd, hogy megegyezzen.
    @Column(name = "Subject", nullable = false, length = 255)
    private String subject;

    public Message() {
    }

    public Message(Integer messageId, String fullName, String messageText, Date createdAt, String email, String subject) {
        this.messageId = messageId;
        this.fullName = fullName;
        this.messageText = messageText;
        this.createdAt = createdAt;
        this.email = email;
        this.subject = subject;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // Ha szeretnéd te magad beállítani, itt is megteheted
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
