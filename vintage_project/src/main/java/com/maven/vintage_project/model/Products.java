/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONObject;

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
    @NamedQuery(name = "Products.findBySlug", query = "SELECT p FROM Products p WHERE p.slug = :slug"),
    @NamedQuery(name = "Products.findByMetaTitle", query = "SELECT p FROM Products p WHERE p.metaTitle = :metaTitle"),
    @NamedQuery(name = "Products.findByPrice", query = "SELECT p FROM Products p WHERE p.price = :price"),
    @NamedQuery(name = "Products.findByDiscountPrice", query = "SELECT p FROM Products p WHERE p.discountPrice = :discountPrice"),
    @NamedQuery(name = "Products.findByStockQuanty", query = "SELECT p FROM Products p WHERE p.stockQuanty = :stockQuanty"),
    @NamedQuery(name = "Products.findByStatus", query = "SELECT p FROM Products p WHERE p.status = :status"),
    @NamedQuery(name = "Products.findByCategoryId", query = "SELECT p FROM Products p WHERE p.category.id = :categoryId"),
    @NamedQuery(name = "Products.findByCreatedAt", query = "SELECT p FROM Products p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "Products.findByUpdatedAt", query = "SELECT p FROM Products p WHERE p.updatedAt = :updatedAt"),
    @NamedQuery(name = "Products.findByImageUrl", query = "SELECT p FROM Products p WHERE p.imageUrl = :imageUrl"),
    @NamedQuery(name = "Products.findByDeletedAt", query = "SELECT p FROM Products p WHERE p.deletedAt = :deletedAt")

})
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
    @Size(max = 255)
    @Column(name = "slug")
    private String slug;
    @Size(max = 255)
    @Column(name = "meta_title")
    private String metaTitle;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "discount_price")
    private BigDecimal discountPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_quanty")
    private int stockQuanty;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", insertable = false, updatable = false)
    private Category category;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

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
        if (productId == null) {
            throw new IllegalArgumentException("Termék ID nem lehet null.");
        }

        EntityManager em = getEntityManager();
        try {
            Products p = em.find(Products.class, productId);
            if (p != null) {
                this.productId = p.getProductId();
                this.name = p.getName();
                this.slug = p.getSlug();
                this.metaTitle = p.getMetaTitle();
                this.description = p.getDescription();
                this.price = p.getPrice();
                this.discountPrice = p.getDiscountPrice();
                this.stockQuanty = p.getStockQuanty();
                this.status = p.getStatus();
                this.category = p.getCategory();
                this.createdAt = p.getCreatedAt();
                this.updatedAt = p.getUpdatedAt();
                this.imageUrl = p.getImageUrl();
                this.deletedAt = p.getDeletedAt();
            }
        } catch (Exception ex) {
            System.err.println("Hiba a Products betöltésekor: " + ex.getMessage());
        } finally {
            em.close();
        }
    }

    public Products(String name, String description, BigDecimal price, int stockQuanty, String status, Category categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuanty = stockQuanty;
        this.status = status;
        this.category = categoryId;

        // Ha nincs slug, akkor generáljuk
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }

        // Ha nincs metaTitle, akkor generáljuk
        if (this.metaTitle == null || this.metaTitle.isEmpty()) {
            this.metaTitle = "Vásárolható termék: " + name;
        }
    }

    public Products(Integer productId, String name, String description, BigDecimal price, int stockQuanty, String status, int categoryId, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuanty = stockQuanty;
        this.status = status;
        Category category = new Category();
        category.setId(categoryId);
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public Products(Integer productId, String name, String slug, String metaTitle, String description,
            BigDecimal price, BigDecimal discountPrice, int stockQuanty, String status, Category category,
            Date createdAt, Date updatedAt, String imageUrl, Date deletedAt) {
        this.productId = productId;
        this.name = name;
        this.slug = slug;
        this.metaTitle = metaTitle;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stockQuanty = stockQuanty;
        this.status = status;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imageUrl = imageUrl;
        this.deletedAt = deletedAt;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getStockQuanty() {
        return stockQuanty;
    }

    public void setStockQuanty(int stockQuanty) {
        this.stockQuanty = stockQuanty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCategoryId(int categoryId) {
        if (this.category == null) {
            this.category = new Category();  // Új Category objektum inicializálása
        }
        this.category.setId(categoryId);  // categoryId beállítása a Category objektumban
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
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

    public Boolean createProduct(Products p) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("create_product");
            spq.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("slugIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("meta_titleIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("descriptionIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("priceIN", BigDecimal.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("discount_priceIN", BigDecimal.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("stockIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("statusIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("image_urlIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("categoryIN", Integer.class, ParameterMode.IN);

            spq.setParameter("nameIN", p.getName());
            spq.setParameter("slugIN", p.getSlug());
            spq.setParameter("meta_titleIN", p.getMetaTitle());
            spq.setParameter("descriptionIN", p.getDescription());
            spq.setParameter("priceIN", p.getPrice());
            spq.setParameter("discount_priceIN", p.getDiscountPrice() != null ? p.getDiscountPrice() : BigDecimal.ZERO);
            spq.setParameter("stockIN", p.getStockQuanty());
            spq.setParameter("statusIN", p.getStatus());
            spq.setParameter("image_urlIN", p.getImageUrl());
            spq.setParameter("categoryIN", p.getCategory().getId());

            spq.execute();
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Hiba createProduct-ben: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public Products findProductById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            StoredProcedureQuery query = em
                    .createStoredProcedureQuery("getProductByID", Products.class);
            query.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            query.setParameter("idIN", id);

            return (Products) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public Boolean updateProduct(Products p) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            StoredProcedureQuery query = em.createStoredProcedureQuery("updateProduct");

            query.registerStoredProcedureParameter("productIN", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("slugIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("meta_titleIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("descriptionIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("priceIN", BigDecimal.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("discount_priceIN", BigDecimal.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("stock_quantityIN", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("statusIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("category_IN", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("image_urlIN", String.class, ParameterMode.IN);

            query.setParameter("productIN", p.getProductId());
            query.setParameter("nameIN", p.getName());
            query.setParameter("slugIN", p.getSlug());
            query.setParameter("meta_titleIN", p.getMetaTitle());
            query.setParameter("descriptionIN", p.getDescription());
            query.setParameter("priceIN", p.getPrice());
            query.setParameter("discount_priceIN", p.getDiscountPrice() != null ? p.getDiscountPrice() : BigDecimal.ZERO);
            query.setParameter("stock_quantityIN", p.getStockQuanty());
            query.setParameter("statusIN", p.getStatus());
            query.setParameter("category_IN", p.getCategory().getId());
            query.setParameter("image_urlIN", p.getImageUrl());

            query.execute();
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Hiba updateProduct-ben: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public Boolean softDeleteProduct(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            StoredProcedureQuery query = em.createStoredProcedureQuery("deleteProduct");
            query.registerStoredProcedureParameter("product_id", Integer.class, ParameterMode.IN);
            query.setParameter("product_id", id);

            query.execute();
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Hiba softDelete-ben: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public List<Products> getAllProducts() {
        EntityManager em = getEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllProduct");
            spq.execute();

            List<Products> toReturn = new ArrayList<>();
            List<Object[]> resultList = spq.getResultList();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Object[] record : resultList) {
                Integer id = (Integer) record[0];
                String name = (String) record[1];
                String slug = (String) record[2];
                String metaTitle = (String) record[3];
                String description = (String) record[4];
                BigDecimal price = (BigDecimal) record[5];
                BigDecimal discountPrice = (BigDecimal) record[6];
                int stockQuanty = Integer.parseInt(record[7].toString());
                String status = (String) record[8];

                // category_id-ból Category objektum
                int categoryId = Integer.parseInt(record[9].toString());
                Category category = new Category();
                category.setId(categoryId);

                Date createdAt = record[10] != null ? formatter.parse(record[10].toString()) : null;
                Date updatedAt = record[11] != null ? formatter.parse(record[11].toString()) : null;
                String imageUrl = record[12] != null ? record[12].toString() : null;
                Date deletedAt = record[13] != null ? formatter.parse(record[13].toString()) : null;

                Products p = new Products(id, name, slug, metaTitle, description, price,
                        discountPrice, stockQuanty, status, category, createdAt, updatedAt, imageUrl, deletedAt);

                toReturn.add(p);
            }

            return toReturn;

        } catch (Exception e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }
}
