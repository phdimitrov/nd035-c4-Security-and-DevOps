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
import com.example.demo.model.requests.ModifyCartRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void find_item() {
        User user = TestUtils.createMockedUser();
        Item item = TestUtils.createMockedItem();
        TestUtils.createMockedCart(user, item);

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.findByName(item.getName())).thenReturn(items);
        when(itemRepository.findAll()).thenReturn(items);

        //find all
        {
            ResponseEntity<List<Item>> responseAll = itemController.getItems();
            assertNotNull(responseAll);
            assertEquals(HttpStatus.OK, responseAll.getStatusCode());
            List<Item> itemsResponse = responseAll.getBody();
            assertNotNull(itemsResponse);
            assertTrue(itemsResponse.containsAll(items) && items.containsAll(itemsResponse));
        }

        //find by id
        {
            ResponseEntity<Item> response = itemController.getItemById(item.getId());
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Item itemResponse = response.getBody();
            assertNotNull(itemResponse);
            assertEquals(item.getId(), itemResponse.getId());
            assertEquals(item.getDescription(), itemResponse.getDescription());
            assertEquals(item.getName(), itemResponse.getName());
            assertEquals(item.getPrice(), itemResponse.getPrice());
            assertEquals(item.hashCode(), itemResponse.hashCode());
        }

        //find by name
        {
            ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<Item> itemsResponse = response.getBody();
            assertNotNull(itemsResponse);
            assertTrue(items.containsAll(itemsResponse));
        }
    }

}
