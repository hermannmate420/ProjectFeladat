/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author herma
 */
@Entity
@Table(name = "products")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Products.findAll", query = "SELECT p FROM Products p"),
    @NamedQuery(name = "Products.findByProductId", query = "SELECT p FROM Products p WHERE p.productId = :productId"),
    @NamedQuery(name = "Products.findByName", query = "SELECT p FROM Products p WHERE p.name = :name"),
    @NamedQuery(name = "Products.findByPrice", query = "SELECT p FROM Products p WHERE p.price = :price"),
    @NamedQuery(name = "Products.findByStockQuanty", query = "SELECT p FROM Products p WHERE p.stockQuanty = :stockQuanty"),
    @NamedQuery(name = "Products.findByCategoryId", query = "SELECT p FROM Products p WHERE p.categoryId = :categoryId"),
    @NamedQuery(name = "Products.findByCreatedAt", query = "SELECT p FROM Products p WHERE p.createdAt = :createdAt")})
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "product_id")
    private Integer productId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private long price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_quanty")
    private int stockQuanty;
    @Basic(optional = false)
    @NotNull
    @Column(name = "category_id")
    private int categoryId;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @PersistenceUnit
    private static EntityManagerFactory emf;

    public static EntityManager getEntityManager() {
        if (emf == null) {
        emf = Persistence.createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU");
        }
        return emf.createEntityManager();
    }

    public Products() {
    }

    public Products(Integer productId) {
        EntityManager em = getEntityManager();
        try {
            Products p = em.find(Products.class, productId);
            
            this.productId = p.getProductId();
            this.name = p.getName();
            this.description = p.getDescription();
            this.price = p.getPrice();
            this.stockQuanty = p.getStockQuanty();
            this.categoryId = p.getCategoryId();
            this.createdAt = p.getCreatedAt();
        } catch (Exception ex) {
            System.err.println("Hiba: " + ex.getLocalizedMessage());
        } finally {
            em.clear();
            em.close();
        }
    }
    public Products(Integer productId, String name, String description, long price, int stockQuanty, int categoryId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuanty = stockQuanty;
        this.categoryId = categoryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getStockQuanty() {
        return stockQuanty;
    }

    public void setStockQuanty(int stockQuanty) {
        this.stockQuanty = stockQuanty;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productId != null ? productId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Products)) {
            return false;
        }
        Products other = (Products) object;
        if ((this.productId == null && other.productId != null) || (this.productId != null && !this.productId.equals(other.productId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.maven.vintage_project.model.Products[ productId=" + productId + " ]";
    }
    
    public Boolean addProducts(Products p) {
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("addProduct");
            
            spq.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("descIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("priceIN", Long.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("quantityIN", Integer.class, ParameterMode.IN);
            
            spq.setParameter("nameIN", p.getName());
            spq.setParameter("descIN", p.getDescription());
            spq.setParameter("priceIN", p.getPrice());
            spq.setParameter("quantityIN", p.getStockQuanty());
            
            spq.execute();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    /*public Boolean getProductById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            
        }
    }*/
}
