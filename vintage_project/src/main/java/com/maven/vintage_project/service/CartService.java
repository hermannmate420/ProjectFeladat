package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Cart;
import com.maven.vintage_project.model.CartItems;
import com.maven.vintage_project.model.Products;
import com.maven.vintage_project.model.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author herma
 */
@Stateless
public class CartService {

    @PersistenceContext
    private EntityManager em;

    public Cart getOrCreateCart(Integer userId) {
        // lekérdezzük a User entitást
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for id: " + userId);
        }
        Cart cart;
        try {
            // átadjuk a User objektumot
            System.out.println("USER PARAM TÍPUSA: " + user.getClass().getName());
            System.out.println("CART FIND TRY FOR USER ID: " + user.getId());

            cart = em.createNamedQuery("Cart.findOpenCartByUserId", Cart.class)
                    .setParameter("userId", user)
                    .getSingleResult();
        } catch (NoResultException e) {
            cart = new Cart();
            cart.setUserId(user);
            cart.setCreatedAt(new Date());
            cart.setUpdatedAt(new Date());
            cart.setStatus("OPEN");
            cart.setCartItemsCollection(new ArrayList<CartItems>());
            em.persist(cart);
        }

        if (cart.getCartItemsCollection() == null) {
            cart.setCartItemsCollection(new ArrayList<CartItems>());
        }
        return cart;
    }

    public Cart addItemToCart(Integer userId, int productId, int quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItems existingItem = null;
        for (CartItems item : cart.getCartItemsCollection()) {
            if (item.getProductId().getProductId().equals(productId)) {
                existingItem = item;
                break;
            }
        }

        Products product = em.find(Products.class, productId);
        if (product == null) {
            throw new IllegalArgumentException("No product with id " + productId);
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUnitPrice(product.getPrice());
        } else {
            CartItems newItem = new CartItems();
            newItem.setCartId(cart);
            newItem.setProductId(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());

            cart.getCartItemsCollection().add(newItem);
            em.persist(newItem);
        }
        cart.setUpdatedAt(new Date());
        return em.merge(cart);
    }

    public Cart removeItemFromCart(Integer userId, Integer productId) {
        Cart cart = getOrCreateCart(userId);

        CartItems toRemove = null;
        for (CartItems item : cart.getCartItemsCollection()) {
            if (item.getProductId().getProductId().equals(productId)) {
                toRemove = item;
                break;
            }
        }

        if (toRemove != null) {
            cart.getCartItemsCollection().remove(toRemove);
            em.remove(em.contains(toRemove) ? toRemove : em.merge(toRemove));
            cart.setUpdatedAt(new Date());
            em.merge(cart);
        }

        return cart;
    }

    public Cart updateItemQuantity(Integer userId, Integer productId, int newQuantity) {
        Cart cart = getOrCreateCart(userId);

        for (CartItems item : cart.getCartItemsCollection()) {
            if (item.getProductId().getProductId().equals(productId)) {
                if (newQuantity <= 0) {
                    // Ha új mennyiség 0 vagy kevesebb, töröljük
                    cart.getCartItemsCollection().remove(item);
                    em.remove(em.contains(item) ? item : em.merge(item));
                } else {
                    item.setQuantity(newQuantity);
                    item.setUnitPrice(item.getProductId().getPrice().multiply(new BigDecimal(newQuantity)));
                    em.merge(item);
                }
                break;
            }
        }

        cart.setUpdatedAt(new Date());
        return em.merge(cart);
    }

    public Cart getCart(Integer userId) {
        return getOrCreateCart(userId);
    }
}
