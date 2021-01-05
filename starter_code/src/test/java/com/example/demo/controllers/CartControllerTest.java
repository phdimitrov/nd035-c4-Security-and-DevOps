package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart() {
        User user = TestUtils.createMockedUser();
        Item item = TestUtils.createMockedItem();
        Cart cart = TestUtils.createMockedCart(user, item);

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(cartRepository.findByUser(user)).thenReturn(cart);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(item.getId());
        request.setQuantity(1);
        request.setUsername(user.getUsername());

        //add to cart successful
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cartResponse = response.getBody();
        assertNotNull(cartResponse);
        assertEquals(cart.getId(), cartResponse.getId());
        assertEquals(user.getUsername(), cartResponse.getUser().getUsername());
        assertTrue(cartResponse.getItems().contains(item));

        //add to cart bad user
        ModifyCartRequest requestBadUser = new ModifyCartRequest();
        requestBadUser.setItemId(item.getId());
        requestBadUser.setQuantity(1);
        requestBadUser.setUsername("unknown");
        response = cartController.addTocart(requestBadUser);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        //add to cart bad item
        ModifyCartRequest requestBadItem = new ModifyCartRequest();
        requestBadItem.setItemId(Long.MAX_VALUE);
        requestBadItem.setQuantity(1);
        requestBadItem.setUsername(user.getUsername());
        response = cartController.addTocart(requestBadItem);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_from_cart() {
        User user = TestUtils.createMockedUser();
        Item item = TestUtils.createMockedItem();
        Cart cart = TestUtils.createMockedCart(user, item);

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(cartRepository.findByUser(user)).thenReturn(cart);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(item.getId());
        request.setQuantity(1);
        request.setUsername(user.getUsername());

        //remove from cart successful
        {
            ResponseEntity<Cart> response = cartController.removeFromcart(request);
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Cart cartResponse = response.getBody();
            assertNotNull(cartResponse);
            assertEquals(cart.getId(), cartResponse.getId());
            assertEquals(user.getUsername(), cartResponse.getUser().getUsername());
            assertFalse(cartResponse.getItems().contains(item));
        }

        //remove from cart bad user
        {
            ModifyCartRequest requestBadUser = new ModifyCartRequest();
            requestBadUser.setItemId(item.getId());
            requestBadUser.setQuantity(1);
            requestBadUser.setUsername("unknown");
            ResponseEntity<Cart> response = cartController.removeFromcart(requestBadUser);
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        //remove from cart bad item
        {
            ModifyCartRequest requestBadItem = new ModifyCartRequest();
            requestBadItem.setItemId(Long.MAX_VALUE);
            requestBadItem.setQuantity(1);
            requestBadItem.setUsername(user.getUsername());
            ResponseEntity<Cart> response = cartController.removeFromcart(requestBadItem);
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        //remove from cart on empty
        {
            cart.setItems(null);
            cart.setTotal(null);
            ResponseEntity<Cart> response = cartController.removeFromcart(request);
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Cart cartResponse = response.getBody();
            assertNotNull(cartResponse);
            assertEquals(cart.getId(), cartResponse.getId());
            assertEquals(user.getUsername(), cartResponse.getUser().getUsername());
            assertFalse(cartResponse.getItems().contains(item));
        }
    }

}
