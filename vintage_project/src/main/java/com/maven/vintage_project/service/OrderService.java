package com.maven.vintage_project.service;

import com.maven.vintage_project.model.Cart;
import com.maven.vintage_project.model.CartItems;
import com.maven.vintage_project.model.User;
import com.maven.vintage_project.model.Orders;
import com.maven.vintage_project.model.OrderItems;
import com.maven.vintage_project.model.Products;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OrderService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private CartService cartService;

    public OrderResponse checkoutCart(Integer userId) {
        OrderResponse response = new OrderResponse();

        // VALIDÁCIÓ: userId null-e?
        if (userId == null) {
            return new OrderResponse(400, "User ID must not be null", null);
        }
        User user = em.find(User.class, userId);
        if (user == null) {
            return new OrderResponse(404, "User not found with ID: " + userId, null);
        }

        Cart cart = cartService.getOrCreateCart(userId);
        em.refresh(cart);
        // VALIDÁCIÓ: létezik-e a kosár és vannak-e benne tételek?
        if (cart.getCartItemsCollection() == null || cart.getCartItemsCollection().isEmpty()) {
            return new OrderResponse(400, "The Cart is empty", null);
        }

        try {
            Orders order = new Orders();
            order.setUser(user);
            order.setOrderDate(new Date());
            order.setStatus("PENDING");

            BigDecimal total = BigDecimal.ZERO;
            BigDecimal tax = BigDecimal.valueOf(1.27);
            BigDecimal deliveryFee = BigDecimal.valueOf(1000);

            for (CartItems cartItem : cart.getCartItemsCollection()) {
                System.out.println(">>> Item: productId = " + cartItem.getProductId().getProductId()
                        + ", quantity = " + cartItem.getQuantity());
                Products product = cartItem.getProductId();
                if (product == null) {
                    return new OrderResponse(400, "Product in cart is missing or invalid", null);
                }
                BigDecimal priceWithTax = product.getPrice().multiply(tax).setScale(BigDecimal.ROUND_HALF_UP);

                OrderItems orderItem = new OrderItems();
                orderItem.setProduct(product);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(priceWithTax);
                BigDecimal itemtotal = priceWithTax.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                orderItem.setPrice(itemtotal);
                orderItem.setOrder(order);

                total = total.add(itemtotal);
                order.getItems().add(orderItem);
            }
            
            total = total.add(deliveryFee);

            order.setTotal(total);
            System.out.println("Rendelési végösszeg: " + total);

            em.persist(order);

            cart.setStatus("CLOSED");
            cart.setUpdatedAt(new Date());
            em.merge(cart);

            boolean emailSent = false;
            try {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("username", order.getUser().getUsername());
                placeholders.put("orderId", order.getId().toString());
                placeholders.put("total", order.getTotal().toPlainString());

                String emailBody = User.loadEmailTemplate("order", placeholders);
                emailSent = User.sendEmail(order.getUser().getEmail(), "Rendelés visszaigazolása", emailBody);

            } catch (Exception e) {
                System.err.println("Hiba email küldés közben: " + e.getMessage());
            }

            if (emailSent) {
                return new OrderResponse(200, "Order placed and email sent successfully", order.getId());
            } else {
                return new OrderResponse(207, "Order placed successfully, but email could not be sent", order.getId());
            }

        } catch (Exception e) {
            return new OrderResponse(500, "Internal server error: " + e.getMessage(), null);
        }
    }
    
    public List<Orders> getAllOrders() {
        return em.createNamedQuery("Orders.findAll", Orders.class)
                .getResultList();
    }

// Response wrapper
    public static class OrderResponse {

        private int statusCode;
        private String message;
        private Object data;

        public OrderResponse() {
        }

        public OrderResponse(int statusCode, String message, Object data) {
            this.statusCode = statusCode;
            this.message = message;
            this.data = data;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

}
