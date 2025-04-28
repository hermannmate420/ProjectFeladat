package com.maven.vintage_project.controller;

import com.maven.vintage_project.config.JWT;
import com.maven.vintage_project.model.Orders;
import com.maven.vintage_project.service.OrderService;
import com.maven.vintage_project.service.OrderService.OrderResponse;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/orders")
@Stateless
public class OrderController {

    @Inject
    private OrderService orderService;

//    /**
//     * POST /orders Fogadja az új rendelés JSON payload-ját, ami tartalmazza a rendeléshez szükséges adatokat. A JSON-ban szerepelnek a DTO mezők is, mint a userId és az inputItems (részletként a termékek és mennyiségek).
//     *
//     * Példa JSON payload: { "userId": 1, "inputItems": [ { "productId": 5, "quantity": 2 }, { "productId": 9, "quantity": 1 } ] }
//     *
//     * A metódus meghívja az OrderService.createFromRequest() metódust, amely: - Lekéri a felhasználót a megadott userId alapján. - Végigiterál az inputItems listán, és minden termékhez lekéri a megfelelő Product entitást. - Létrehozza a OrderItems entitásokat, kiszámolja az egyes tételek árait, majd a teljes rendelés végösszegét. - Végül elmenti a Order entitást (és kaskádlva az OrderItems-eket is) az adatbázisba.
//     *
//     * A POST végpont a létrehozott rendelést adja vissza JSON formátumban, mely tartalmazza az adatbázis által generált azonosítót és egyéb információkat (pl. orderDate, status, total).
//     *
//     * @param order Az új rendelés DTO-ként érkező reprezentációja (Orders entitás + Transient DTO mezők)
//     * @return HTTP Response a létrehozott rendelés adataival
//     */

    @POST
    @Path("/checkout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkout(
            @HeaderParam("token") String jwt) {
        // JWT validálás…
        int valid = JWT.validateJWT(jwt);
        if (valid != 1) {
            return Response.status(valid == 2 ? 498 : 401)
                    .entity("{\"error\":\"invalid or expired token\"}")
                    .build();
        }

        int userId = JWT.getUserIdByToken(jwt);
        OrderResponse resp = orderService.checkoutCart(userId);
        return Response.status(resp.getStatusCode())
                .entity(resp)
                .build();
    }
    
    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders() {
        List<Orders> orders = orderService.getAllOrders();
        return Response.ok(orders).build();
    }

}
