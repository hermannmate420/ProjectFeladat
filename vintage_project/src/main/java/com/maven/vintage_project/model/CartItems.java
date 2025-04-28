/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author herma
 */
@Entity
@Table(name = "cart_items")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CartItems.findAll", query = "SELECT c FROM CartItems c"),
    @NamedQuery(name = "CartItems.findByCartItemid", query = "SELECT c FROM CartItems c WHERE c.cartItemid = :cartItemid"),
    @NamedQuery(name = "CartItems.findByQuantity", query = "SELECT c FROM CartItems c WHERE c.quantity = :quantity"),
    @NamedQuery(name = "CartItems.findByUnitPrice", query = "SELECT c FROM CartItems c WHERE c.unitPrice = :unitPrice")})
public class CartItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cartItem_id")
    private Integer cartItemid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @ManyToOne(optional = false)
    @JoinColumn(name = "cartId", referencedColumnName = "cartId")
    @JsonBackReference
    private Cart cartId;
    @JoinColumn(name = "productId", referencedColumnName = "product_id")
    @ManyToOne(optional = false)
    private Products productId;

    public CartItems() {
    }

    public CartItems(Integer cartItemid) {
        this.cartItemid = cartItemid;
    }

    public CartItems(Integer cartItemid, Cart cartId, Products productId, int quantity, BigDecimal unitPrice) {
        this.cartItemid = cartItemid;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Integer getCartItemid() {
        return cartItemid;
    }

    public void setCartItemid(Integer cartItemid) {
        this.cartItemid = cartItemid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Cart getCartId() {
        return cartId;
    }

    public void setCartId(Cart cartId) {
        this.cartId = cartId;
    }

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cartItemid != null ? cartItemid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CartItems)) {
            return false;
        }
        CartItems other = (CartItems) object;
        if ((this.cartItemid == null && other.cartItemid != null) || (this.cartItemid != null && !this.cartItemid.equals(other.cartItemid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.maven.vintage_project.model.CartItems[ cartItemid=" + cartItemid + " ]";
    }
    
}
