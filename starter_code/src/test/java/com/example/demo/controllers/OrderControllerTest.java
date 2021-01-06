package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void order_submit() {
        User user = TestUtils.createMockedUser();
        Item item = TestUtils.createMockedItem();
        TestUtils.createMockedCart(user, item); //needed to set cart user relationship
        UserOrder order = UserOrder.createFromCart(user.getCart());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.save(order)).thenReturn(order);

        //submit success
        {
            ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            UserOrder orderSubmitted = response.getBody();
            assertNotNull(orderSubmitted);
            assertEquals(orderSubmitted.getUser(), order.getUser());
            assertEquals(orderSubmitted.getItems(), order.getItems());
            assertEquals(orderSubmitted.getTotal(), order.getTotal());
        }

        //submit fail
        {
            ResponseEntity<UserOrder> response = orderController.submit("unknown");
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Test
    public void order_history() {
        User user = TestUtils.createMockedUser();
        UserOrder order = TestUtils.createMockedUserOrder(user);
        List<UserOrder> orders = new ArrayList<>();
        orders.add(order);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        //find order by users success
        {
            ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<UserOrder> history = response.getBody();
            assertNotNull(history);

            Optional<UserOrder> opt = history.stream()
                    .filter(o -> o.getId().equals(order.getId()))
                    .findFirst();

            assertTrue(opt.isPresent());
        }

        //find order by users fail
        {
            ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("unknown");
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

}
