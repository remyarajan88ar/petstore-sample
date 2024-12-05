package org.agoncal.application.petstore.service;

import org.agoncal.application.petstore.domain.*;
import org.agoncal.application.petstore.exception.ValidationException;
import org.agoncal.application.petstore.util.Loggable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */

@Stateless
@Loggable
public class OrderService implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // ======================================
    // =             Attributes             =
    // ======================================

    @Inject
    private EntityManager em;

    // ======================================
    // =              Public Methods        =
    // ======================================

    public Order createOrder(final Customer customer, final CreditCard creditCard, final List<CartItem> cartItems) {

        // OMake sure the object is valid
        if (cartItems == null || cartItems.size() == 0)
            throw new ValidationException("Shopping cart is empty"); // TODO exception bean validation

        // Creating the order
        Order order = new Order(em.merge(customer), creditCard, customer.getHomeAddress());

        // From the shopping cart we create the order lines
        List<OrderLine> orderLines = new ArrayList<OrderLine>();

        for (CartItem cartItem : cartItems) {
            orderLines.add(new OrderLine(cartItem.getQuantity(), em.merge(cartItem.getItem())));
            orderPrice(cartItem.getItem().getUnitCost(), cartItem.getQuantity());
        }
        order.setOrderLines(orderLines);

        // Persists the object to the database
        em.persist(order);

        return order;
    }



    public Order findOrder(Long orderId) {
        if (orderId == null)
            throw new ValidationException("Invalid order id");

        return em.find(Order.class, orderId);
    }

    public List<Order> findAllOrders() {
        TypedQuery<Order> typedQuery = em.createNamedQuery(Order.FIND_ALL, Order.class);
        return typedQuery.getResultList();
    }

    public void removeOrder(Order order) {
        if (order == null)
            throw new ValidationException("Order object is null");

        em.remove(em.merge(order));
    }

    public void orderPrice(Float unitCost, Integer quantity ){
        Query query = em.createNativeQuery("CALL orderPrice(:quantity, :unitCost)");
        query.setParameter("quantity",quantity );
        query.setParameter("unitCost", unitCost);
        Float result = (float) query.getFirstResult();
        log.info("Order price: {}", result);

    }


}
